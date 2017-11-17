package com.intfocus.syp_template.business.subject.template.one

import android.content.Context
import android.util.Log
import com.intfocus.syp_template.business.subject.templateone.entity.MererDetailEntity

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
            override fun onDataLoaded(entity: MererDetailEntity) {
                mView.initRootView(entity)
            }

            override fun onDataNotAvailable(e: Throwable) {
                Log.i("testlog", e.toString())
            }
        })
    }
}
