package com.intfocus.template.subject.templateone.curvechart

import com.intfocus.template.subject.one.entity.Chart

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:54
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ChartPresenter(
        private val mModel: ChartImpl,
        private val mView: ChartContract.View
) : ChartContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(rootId: Int, index: Int) {
        mModel.getData(rootId, index, object : ChartModel.LoadDataCallback {
            override fun onDataLoaded(data: Chart) {
                mView.showData(data)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }

        })
    }

}
