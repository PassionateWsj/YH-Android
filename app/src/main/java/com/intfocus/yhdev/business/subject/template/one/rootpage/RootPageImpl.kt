package com.intfocus.yhdev.business.subject.templateone.rootpage

import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.yhdev.business.subject.template.one.ModeImpl
import com.intfocus.yhdev.business.subject.template.one.entity.MDetailUnitEntity
import com.intfocus.yhdev.business.subject.template.one.entity.msg.MDetailRootPageRequestResult
import com.intfocus.yhdev.general.bean.Report
import com.intfocus.yhdev.general.gen.ReportDao
import com.intfocus.yhdev.general.util.DaoUtil
import com.intfucos.yhdev.constant.Params.REPORT_TYPE_MAIN_DATA
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.StringReader
import java.util.*

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
        Log.i(TAG, "StartAnalysisTime:" + TimeUtil.getNowTime())
        Observable.just("")
                .subscribeOn(Schedulers.io())
                .map {
                    var reports: List<Report>
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
                        Log.i(TAG, "EndAnalysisTime:" + TimeUtil.getNowTime())
                    }

                })
    }

}
