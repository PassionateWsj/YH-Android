package com.intfocus.yhdev.business.subject.templateone.curvechart

import com.alibaba.fastjson.JSON
import com.intfocus.yhdev.business.subject.template.one.entity.MDRPUnitCurveChartEntity
import com.intfocus.yhdev.general.gen.ReportDao
import com.intfocus.yhdev.general.util.DaoUtil
import com.intfocus.yhdev.constant.Params.REPORT_TYPE_CHART
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

    override fun getData(uuid: String, index: Int, callback: CurveChartModel.LoadDataCallback) {
        val reportDao = DaoUtil.getReportDao()
        val report = reportDao.queryBuilder()
                .where(reportDao.queryBuilder().and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq(REPORT_TYPE_CHART), ReportDao.Properties.Index.eq(index)))
                .unique()

        Observable.just(report.config)
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
