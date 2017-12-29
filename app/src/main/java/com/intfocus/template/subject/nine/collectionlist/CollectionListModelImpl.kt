package com.intfocus.template.subject.nine.collectionlist

import android.content.Context
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Collection
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

    override fun getData(ctx: Context, callback: LoadDataCallback<List<Collection>>) {
        observable = Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    DaoUtil.getCollectionDao().queryBuilder().list()
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

    override fun upload(ctx: Context) {
    }
}