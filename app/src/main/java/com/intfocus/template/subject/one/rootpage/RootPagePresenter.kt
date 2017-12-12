package com.intfocus.template.subject.templateone.rootpage

import com.intfocus.template.model.entity.Report
import com.intfocus.template.subject.one.rootpage.RootPageContract
import com.intfocus.template.subject.one.rootpage.RootPageImpl

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

    override fun loadData(uuid: String, pageId: Int) {
        mModel.getData(uuid, pageId, object : RootPageModel.LoadDataCallback {
            override fun onDataLoaded(reports: List<Report>) {
                mView.showData(reports)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
    }
}
