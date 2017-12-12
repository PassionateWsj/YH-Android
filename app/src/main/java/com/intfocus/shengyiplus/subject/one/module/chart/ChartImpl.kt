package com.intfocus.shengyiplus.subject.templateone.curvechart

import com.alibaba.fastjson.JSON
import com.intfocus.shengyiplus.subject.one.ModeImpl
import com.intfocus.shengyiplus.subject.one.entity.Chart
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:58
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ChartImpl : ChartModel {

    companion object {

        private var sINSTANCE: ChartImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ChartImpl {
            return sINSTANCE ?: ChartImpl()
                    .apply { sINSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            sINSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    override fun getData(rootId: Int, index: Int, callback: ChartModel.LoadDataCallback) {
        observable = Observable.just(ModeImpl.getInstance().queryModuleConfig(index, rootId))
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<Chart>(it, Chart::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Chart> {
                    override fun onNext(t: Chart?) {
                        t?.let { callback.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                    override fun onCompleted() {
                    }
                })
    }

}
