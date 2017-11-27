package com.intfocus.syp_template.subject.templateone.singlevalue

import com.alibaba.fastjson.JSON
import com.intfocus.syp_template.subject.one.ModeImpl
import com.intfocus.syp_template.subject.one.entity.SingleValue
import com.intfocus.syp_template.model.gen.ReportDao
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class SingleValueImpl : SingleValueModel {

    companion object {

        private var INSTANCE: SingleValueImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): SingleValueImpl {
            return INSTANCE ?: SingleValueImpl()
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

    override fun getData(rootId: Int, index: Int, callback: SingleValueModel.LoadDataCallback) {
        observable = Observable.just(ModeImpl.getInstance().queryModuleConfig(index, rootId))
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<SingleValue>(it, SingleValue::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<SingleValue> {
                    override fun onNext(t: SingleValue?) {
                        t?.let { callback.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e)
                    }

                    override fun onCompleted() {
                    }
                })
    }

}
