package com.intfucos.yhdev.collection

import android.content.Context
import android.content.Intent
import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.yhdev.YHApplication.globalContext
import com.intfocus.yhdev.business.collection.entity.CollectionRequestBody
import com.intfocus.yhdev.business.login.bean.Device
import com.intfocus.yhdev.general.bean.Collection
import com.intfocus.yhdev.general.bean.Source
import com.intfocus.yhdev.general.data.response.BaseResult
import com.intfocus.yhdev.general.gen.SourceDao
import com.intfocus.yhdev.general.net.ApiException
import com.intfocus.yhdev.general.net.CodeHandledSubscriber
import com.intfocus.yhdev.general.net.RetrofitUtil
import com.intfocus.yhdev.general.service.CollectionUploadService
import com.intfocus.yhdev.general.util.*
import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.collection.entity.CollectionEntity
import com.intfucos.yhdev.collection.entity.Content
import okhttp3.*
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
                var sourceDao = DaoUtil.getSourceDao()
                var sourceDb = Source()
                sourceDb.id = null
                sourceDb.uuid = uuid
                sourceDb.type = entity.type
                sourceDb.isList = entity.is_list
                sourceDb.isShow = entity.is_show
                sourceDb.isFilter = entity.is_filter
                sourceDb.config = entity.config
                sourceDb.key = entity.key
                sourceDb.value = entity.value

                sourceDao.insert(sourceDb)
            }

            var collectionDb = Collection()
            collectionDb.dJson = ""
            collectionDb.localId = null
            collectionDb.uuid = uuid
            collectionDb.status = 0
            collectionDb.image_status = 0

            DaoUtil.getCollectionDao().insert(collectionDb)
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

    override fun upload(ctx: Context) {
        var intent = Intent(ctx, CollectionUploadService::class.java)
        ctx.startService(intent)
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
        requestBody.data = CollectionRequestBody.Data()
        requestBody.data!!.acquisition_id = acquisitionId
        requestBody.data!!.user_num = globalContext.getSharedPreferences("UserBean",Context.MODE_PRIVATE).getString("user_num", "")
        requestBody.data!!.content = generateDJson()

        return requestBody
    }
}
