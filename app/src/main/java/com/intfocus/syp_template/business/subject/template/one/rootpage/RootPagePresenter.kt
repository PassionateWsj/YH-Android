package com.intfocus.syp_template.business.subject.templateone.rootpage

import com.intfocus.syp_template.general.bean.Report

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 上午10:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class RootPagePresenter(
        private val mModel: RootPageImpl,
        private val mView: RootPageContract.View
) : RootPageContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(uuid: String, page: Int) {
        mModel.getData(uuid, page, object : RootPageModel.LoadDataCallback {
            override fun onDataLoaded(reports: List<Report>) {
                mView.showData(reports)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
    }
}