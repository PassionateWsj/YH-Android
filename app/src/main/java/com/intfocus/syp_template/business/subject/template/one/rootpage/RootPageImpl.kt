package com.intfocus.syp_template.business.subject.templateone.rootpage

import com.intfocus.syp_template.general.bean.Report
import com.intfocus.syp_template.general.gen.ReportDao
import com.intfocus.syp_template.general.util.DaoUtil
import com.intfocus.syp_template.general.util.LogUtil
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Observer
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
            INSTANCE = null
        }
    }

    override fun getData(uuid: String, page: Int, callback: RootPageModel.LoadDataCallback) {
        LogUtil.d(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime())
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    val reports: List<Report>
                    val reportDao = DaoUtil.getReportDao()
                    reports = reportDao.queryBuilder()
                            .where(reportDao.queryBuilder()
                                    .and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Page.eq(page)))
                            .list()
                    reports
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
