package com.intfocus.yhdev.subject.template_v1.curvechart

import com.alibaba.fastjson.JSON
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitCurveChartEntity
import rx.Observable
import rx.Observer
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
class CurveChartImpl : CurveChartModel {

    companion object {

        private var INSTANCE: CurveChartImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): CurveChartImpl {
            return INSTANCE ?: CurveChartImpl()
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

    override fun getData(mParam: String, callback: CurveChartModel.LoadDataCallback) {
        Observable.just(mParam)
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<MDRPUnitCurveChartEntity>(it, MDRPUnitCurveChartEntity::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MDRPUnitCurveChartEntity> {
                    override fun onNext(t: MDRPUnitCurveChartEntity?) {
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
