package com.intfocus.syp_template.business.subject.templateone.curvechart

import com.alibaba.fastjson.JSON
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
                .map { JSON.parseObject<com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity>(it, com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity> {
                    override fun onNext(t: com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity?) {
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
