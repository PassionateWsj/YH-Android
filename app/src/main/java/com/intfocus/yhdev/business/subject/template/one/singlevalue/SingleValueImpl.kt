package com.intfocus.yhdev.business.subject.templateone.singlevalue

import com.alibaba.fastjson.JSON
import com.intfocus.yhdev.business.subject.template.one.entity.MDRPUnitSingleValue
import com.intfocus.yhdev.general.gen.ReportDao
import com.intfocus.yhdev.general.util.DaoUtil
import com.intfocus.yhdev.constant.Params.REPORT_TYPE_SINGLE_VALUE
import rx.Observable
import rx.Observer
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
            INSTANCE = null
        }
    }

    override fun getData(uuid: String, index: Int, callback: SingleValueModel.LoadDataCallback) {
        val reportDao = DaoUtil.getReportDao()
        val report = reportDao.queryBuilder()
                .where(reportDao.queryBuilder().and(ReportDao.Properties.Uuid.eq(uuid), ReportDao.Properties.Type.eq(REPORT_TYPE_SINGLE_VALUE), ReportDao.Properties.Index.eq(index)))
                .unique()

        Observable.just(report.config)
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<MDRPUnitSingleValue>(it, MDRPUnitSingleValue::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<MDRPUnitSingleValue> {
                    override fun onNext(t: MDRPUnitSingleValue?) {
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
