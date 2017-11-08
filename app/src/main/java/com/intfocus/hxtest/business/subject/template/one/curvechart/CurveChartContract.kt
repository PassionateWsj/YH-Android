package com.intfocus.hxtest.business.subject.templateone.curvechart

import com.intfocus.hxtest.general.base.BasePresenter
import com.intfocus.hxtest.general.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface CurveChartContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(entity: com.intfocus.hxtest.business.subject.template.one.entity.MDRPUnitCurveChartEntity)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(mParam: String)
    }
}
