package com.intfucos.yhdev.collection

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.yhdev.YHApplication.globalContext
import com.intfocus.yhdev.business.collection.entity.CollectionRequestBody
import com.intfocus.yhdev.business.login.bean.Device
import com.intfocus.yhdev.general.bean.Source
import com.intfocus.yhdev.general.data.response.BaseResult
import com.intfocus.yhdev.general.gen.SourceDao
import com.intfocus.yhdev.general.net.ApiException
import com.intfocus.yhdev.general.net.CodeHandledSubscriber
import com.intfocus.yhdev.general.net.RetrofitUtil
import com.intfocus.yhdev.general.util.DaoUtil
import com.intfocus.yhdev.general.util.ToastUtils
import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.collection.entity.CollectionEntity
import com.intfucos.yhdev.collection.entity.Content
import org.json.JSONArray
import org.json.JSONObject
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
class CollectionModelImpl: CollectionModel<CollectionEntity> {
    companion object {
        private val TAG = CollectionModelImpl::class.java.simpleName
        lateinit var uuid: String
        lateinit var acquisitionId: String

        private var INSTANCE: CollectionModelImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): CollectionModelImpl {
            return INSTANCE ?: CollectionModelImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }

        @JvmStatic
        fun insertData(entityList: ArrayList<Content>) {
            for (entity in entityList) {
                var sourceDao = DaoUtil.getDaoSession()!!.sourceDao
                var dataDb = Source()
                dataDb.id = null
                dataDb.uuid = uuid
                dataDb.type = entity.type
                dataDb.isList = entity.is_list
                dataDb.isShow = entity.is_show
                dataDb.isFilter = entity.is_filter
                dataDb.config = entity.config
                dataDb.key = entity.key
                dataDb.value = entity.value

                sourceDao.insert(dataDb)
            }
        }
    }

    override fun getData(id: String, callback: LoadDataCallback<CollectionEntity>) {
        uuid = UUID.randomUUID().toString()
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    val response = RetrofitUtil.getHttpService(globalContext).getCollectionConfig(id).execute()
                    var responseString = response.body()!!.string()
                    val stringReader = StringReader(JSONObject(responseString)["data"].toString())
                    val reader = JSONReader(stringReader)
                    reader.startObject()
                    val entity = CollectionEntity()
                    entity.data = ArrayList()
                    Log.i(TAG, "analysisDataReaderTime2:")

                    while (reader.hasNext()) {
                        val key = reader.readString()
                        when (key) {
                            "title" -> {
                                entity.name = reader.readObject().toString()
                                Log.i(TAG, "name:")
                            }

                            "content" -> {
                                Log.i(TAG, "dataStart:")
                                reader.startArray()

                                while (reader.hasNext()) {
                                    reader.startObject()
                                    val data = CollectionEntity.PageData()
                                    while (reader.hasNext()) {
                                        val dataKey = reader.readString()
                                        when (dataKey) {
                                            "parts" -> data.content = reader.readObject().toString()

                                            "name" -> data.title = reader.readObject().toString()
                                        }
                                    }
                                    reader.endObject()
                                    entity.data!!.add(data)
                                }
                                reader.endArray()
                                Log.i(TAG, "dataEnd:")
                            }
                            "id" -> acquisitionId = reader.readString()
                            else -> {Log.i("testlog", key + reader.readString() + " is error reason")}
                        }
                    }
                    reader.endObject()
                    Log.i(TAG, "analysisDataEndTime:")
                    entity
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CollectionEntity>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: CollectionEntity?) {
                        t?.let { callback.onDataLoaded(it) }

                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                })
    }

    /**
     * 加载 模板一 本地 json 测试数据的方法
     * @param context
     * @return
     */
    private fun getAssetsJsonData(context: Context): String {
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var sb: StringBuilder? = null
        try {
            inputStream = context.resources.assets.open("collection.json")
            reader = BufferedReader(InputStreamReader(inputStream!!))
            sb = StringBuilder()
            var line: String?
            while (true) {
                line = reader.readLine()
                if (line != null) {
                    sb.append(line + "\n")
                } else break
            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
                if (inputStream != null) {
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb!!.toString()
    }

    override fun upload() {
        var requestBody = generateRequestBody()
        RetrofitUtil.getHttpService(globalContext).submitCollection(requestBody)
                .compose(RetrofitUtil.CommonOptions())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onError(apiException: ApiException) {
                    }

                    /**
                     * 上传采集信息成功
                     * @param data 返回的数据
                     */
                    override fun onBusinessNext(data: BaseResult) {
                        ToastUtils.show(globalContext, "提交成功")
                    }

                    override fun onCompleted() {}
                })
    }

    private fun generateDJson(): JSONObject {
        var sourceDao = DaoUtil.getDaoSession()!!.sourceDao
        var dataList = sourceDao.queryBuilder().where(SourceDao.Properties.Uuid.eq(uuid)).list()
        var dJson = JSONObject()
        dJson.put("uuid", uuid)

        for (data in dataList) {
            dJson.put(data.key, JSONObject(data.config)["value"])
        }

        return dJson
    }

    private fun generateRequestBody(): CollectionRequestBody{
        var requestBody = CollectionRequestBody()
        var requestData = requestBody.data
        requestData!!.acquisition_id = acquisitionId
        requestData!!.user_num = globalContext.getSharedPreferences("UserBean",Context.MODE_PRIVATE).getString("user_num", "")
        requestData!!.content = generateDJson()

        return requestBody
    }
}