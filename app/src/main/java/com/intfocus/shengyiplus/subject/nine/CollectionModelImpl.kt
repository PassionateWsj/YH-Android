package com.intfocus.shengyiplus.subject.nine

import android.content.Context
import android.content.Intent
import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.shengyiplus.SYPApplication.globalContext
import com.intfocus.shengyiplus.model.entity.Collection
import com.intfocus.shengyiplus.model.entity.Source
import com.intfocus.shengyiplus.general.net.RetrofitUtil
import com.intfocus.shengyiplus.model.DaoUtil
import com.intfocus.shengyiplus.service.CollectionUploadService
import com.intfocus.shengyiplus.util.*
import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import com.intfocus.shengyiplus.subject.nine.entity.CollectionEntity
import com.intfocus.shengyiplus.subject.nine.entity.Content
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
class CollectionModelImpl : CollectionModel<CollectionEntity> {
    companion object {
        private val TAG = CollectionModelImpl::class.java.simpleName
        lateinit var uuid: String
        lateinit var reportId: String

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
                sourceDb.reportId = reportId
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
            collectionDb.id = null
            collectionDb.reportId = reportId
            collectionDb.uuid = uuid
            collectionDb.dJson = ""
            collectionDb.status = 0
            collectionDb.imageStatus = 0

            DaoUtil.getCollectionDao().insert(collectionDb)
        }
    }

    override fun getData(reportId: String, templateID: String, groupId: String, callback: LoadDataCallback<CollectionEntity>) {
        uuid = UUID.randomUUID().toString()
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    val response = RetrofitUtil.getHttpService(globalContext).getJsonReportData(reportId, templateID, groupId).execute()
                    var responseString = response.body()!!.string()
                    val stringReader = StringReader(responseString)
                    val reader = JSONReader(stringReader)
                    reader.startObject()
                    val entity = CollectionEntity()
                    entity.data = ArrayList()
                    LogUtil.d(TAG, "analysisDataReaderTime2:")

                    while (reader.hasNext()) {
                        val key = reader.readString()
                        when (key) {
                            "title" -> {
                                entity.name = reader.readObject().toString()
                                LogUtil.d(TAG, "name:")
                            }

                            "content" -> {
                                LogUtil.d(TAG, "dataStart:")
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
                                LogUtil.d(TAG, "dataEnd:")
                            }
                            "id" -> Companion.reportId = reader.readString()
                            else -> {
                                Log.i("testlog", key + reader.readString() + " is error reason")
                            }
                        }
                    }
                    reader.endObject()
                    LogUtil.d(TAG, "analysisDataEndTime:")
                    entity
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CollectionEntity>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: CollectionEntity?) {
                        t?.let { callback.onSuccess(it) }

                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e!!)
                    }

                })
    }

    override fun upload(ctx: Context) {
        var intent = Intent(ctx, CollectionUploadService::class.java)
        ctx.startService(intent)
    }
}
