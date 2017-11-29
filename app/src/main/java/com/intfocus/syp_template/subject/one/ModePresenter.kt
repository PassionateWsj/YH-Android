package com.intfocus.syp_template.subject.one

import android.content.Context
import com.intfocus.syp_template.subject.one.entity.Filter
import com.intfocus.syp_template.util.LogUtil

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

    override fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String) {
        mModel.getData(reportId, templateId, groupId, ReportDataCallback())
    }

    override fun saveFilterSelected(display: String) {
        mModel.updateFilter(display, ReportDataCallback())
    }

    inner class ReportDataCallback: ModeModel.LoadDataCallback {
        override fun onDataLoaded(reports: List<String>, filter: Filter) {
            mView.dataLoaded(reports, filter)
        }

        override fun onDataNotAvailable(e: Throwable) {
            LogUtil.d("testlog", e.toString())
        }
    }
}
