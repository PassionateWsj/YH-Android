package com.intfocus.template.subject.nine

import android.content.Context
import android.content.Intent
import com.alibaba.fastjson.JSON
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.model.entity.Source
import com.intfocus.template.service.CollectionUploadService
import com.intfocus.template.subject.nine.entity.CollectionEntity
import com.intfocus.template.subject.nine.entity.Content
import com.intfocus.template.util.LoadAssetsJsonUtil
import com.intfocus.template.util.LogUtil
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.util.*

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
class CollectionModelImpl : CollectionModel<CollectionEntity> {
    val mCollectionDao = DaoUtil.getCollectionDao()

    companion object {
        lateinit var uuid: String
        private val TAG = CollectionModelImpl::class.java.simpleName

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


    }

    override fun getData(ctx: Context, reportId: String, templateID: String, groupId: String, callback: LoadDataCallback<CollectionEntity>) {
        uuid = UUID.randomUUID().toString()
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    //                    val response = RetrofitUtil.getHttpService(ctx).getJsonReportData("json", reportId, templateID, groupId).execute()
//                    val responseString = response.body()!!.string()

                    val responseString = LoadAssetsJsonUtil.getAssetsJsonData("collection1.json")

                    val entity = JSON.parseObject(responseString, CollectionEntity::class.java)
                    LogUtil.d(this@CollectionModelImpl, responseString)
                    entity
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<CollectionEntity>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: CollectionEntity?) {
                        t?.let {
                            callback.onSuccess(it)
                            it.content?.forEachIndexed { index, item ->
                                insertData(uuid, reportId, index, item.parts!!)
                            }
                        }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onError(e!!)
                    }

                })
    }

    fun insertData(uuid: String, reportId: String, pageIndex: Int, entityList: ArrayList<Content>) {
        for (entity in entityList) {
            val sourceDao = DaoUtil.getSourceDao()
            val sourceDb = Source()
            sourceDb.id = null
            sourceDb.reportId = reportId
            sourceDb.uuid = uuid
            sourceDb.pageIndex = pageIndex
            entity.type?.let {
                sourceDb.type = it
            }
            entity.list?.let {
                sourceDb.isList = it
            }
            entity.show?.let {
                sourceDb.isShow = it
            }
            entity.filter?.let {
                sourceDb.isFilter = it
            }
            sourceDb.config = entity.config ?: ""
            sourceDb.key = entity.key ?: ""
            sourceDb.value = entity.value ?: ""

            sourceDao.insert(sourceDb)
        }

        val collectionDb = Collection()
        collectionDb.reportId = reportId
        collectionDb.uuid = uuid
        collectionDb.dJson = ""
        collectionDb.status = -1
        collectionDb.imageStatus = 0
        val currentTime = System.currentTimeMillis()
        collectionDb.created_at = currentTime
        collectionDb.updated_at = currentTime
        mCollectionDao.insert(collectionDb)
    }

    fun updateModifyTime() {
        mCollectionDao.queryBuilder().unique()
    }

    override fun upload(ctx: Context) {
        val intent = Intent(ctx, CollectionUploadService::class.java)
        ctx.startService(intent)
    }
}
