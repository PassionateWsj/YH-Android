package com.intfocus.syp_template.business.subject.templateone.curvechart

import com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitCurveChartEntity

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:54
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CurveChartPresenter(
        private val mModel: CurveChartImpl,
        private val mView: CurveChartContract.View
) : CurveChartContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(uuid: String, index: Int) {
        mModel.getData(uuid, index, object :CurveChartModel.LoadDataCallback{
            override fun onDataLoaded(data: MDRPUnitCurveChartEntity) {
                mView.showData(data)
            }
            override fun onDataNotAvailable(e: Throwable) {

            }

        })
    }

}
