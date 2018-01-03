package com.intfocus.template.subject.nine.collectionlist

import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.model.gen.CollectionDao
import rx.Observable
import rx.Subscriber
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 上午11:46
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CollectionListModelImpl : CollectionListModel<List<Collection>> {

    companion object {
        private val TAG = "CollectionListModelImpl"
        private var INSTANCE: CollectionListModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): CollectionListModelImpl {
            return INSTANCE ?: CollectionListModelImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            INSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    override fun getData(keyWord: String, callback: LoadDataCallback<List<Collection>>) {
        val mDao = DaoUtil.getCollectionDao()
        val queryBuilder = mDao.queryBuilder()
        observable = Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    queryBuilder.where(
                            queryBuilder.or(CollectionDao.Properties.H1.like("%$keyWord%"),
                                    CollectionDao.Properties.H2.like("%$keyWord%"),
                                    CollectionDao.Properties.H3.like("%$keyWord%")))
                            .orderDesc(CollectionDao.Properties.Updated_at)
                            .list()
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<List<Collection>>() {
                    override fun onError(e: Throwable?) {
                        e?.let { callback.onError(it) }
                    }

                    override fun onCompleted() {
                        callback.onComplete()
                    }

                    override fun onNext(data: List<Collection>?) {
                        data?.let { callback.onSuccess(it) }
                    }
                }
                )
    }

    override fun upload() {
    }
}