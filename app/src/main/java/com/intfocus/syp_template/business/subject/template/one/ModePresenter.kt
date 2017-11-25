package com.intfocus.syp_template.business.subject.template.one

import android.content.Context
import com.intfocus.syp_template.general.bean.Report
import com.intfocus.syp_template.general.util.LogUtil

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ModePresenter(
        private val mModel: ModeImpl,
        private val mView: ModeContract.View
) : ModeContract.Presenter {
    init {
        mView.presenter = this
    }
    override fun start() {
    }

    override fun loadData(ctx: Context, groupId: String, reportId: String) {
        mModel.checkReportData(reportId, "1", groupId, object : ModeModel.LoadDataCallback {
            override fun onDataLoaded(reports: List<String>) {
                mView.initRootView(reports)
            }

            override fun onDataNotAvailable(e: Throwable) {
                LogUtil.d("testlog", e.toString())
            }
        })
    }

    override fun loadFilterData() {
        mModel.checkFilter()
    }
}
