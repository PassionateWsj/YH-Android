package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.model.response.home.ReportListResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ReportPresenter(
        private val mModel: ReportImpl,
        private val mView: ReportContract.View
) : ReportContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(ctx: Context) {
        mModel.getData(ctx, object : ReportModel.LoadDataCallback {
            override fun onDataLoaded(data: ReportListResult) {
                mView.dataLoaded(data)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }
}
