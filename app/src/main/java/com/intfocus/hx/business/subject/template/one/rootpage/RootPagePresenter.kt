package com.intfocus.hx.business.subject.templateone.rootpage

import com.intfocus.hx.business.subject.template.one.entity.msg.MDetailRootPageRequestResult

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

    override fun loadData(mParam: String) {
        mModel.getData(mParam, object : RootPageModel.LoadDataCallback {
            override fun onDataLoaded(data: MDetailRootPageRequestResult) {
                mView.showData(data)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
    }
}
