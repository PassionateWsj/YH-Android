package com.intfocus.shengyiplus.subject.one.rootpage

import com.intfocus.shengyiplus.subject.one.ModeImpl
import com.intfocus.shengyiplus.model.entity.Report
import com.intfocus.shengyiplus.subject.templateone.rootpage.RootPageModel
import com.intfocus.shengyiplus.util.LogUtil
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * author jameswong
 * created on: 17/10/25 上午10:05
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class RootPageImpl : RootPageModel {
    private val TAG = "hjjzz"

    companion object {

        private var INSTANCE: RootPageImpl? = null
        private var observable: Subscription? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): RootPageImpl {
            return INSTANCE ?: RootPageImpl()
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

    override fun getData(uuid: String, pageId: Int, callback: RootPageModel.LoadDataCallback) {
        LogUtil.d(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime())
        observable = Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    ModeImpl.getInstance().queryPageData(pageId)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<List<Report>> {
                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                    override fun onNext(t: List<Report>?) {
                        callback.onDataLoaded(t!!)
                    }

                    override fun onCompleted() {
                        LogUtil.d(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime())
                    }

                })
    }

}
