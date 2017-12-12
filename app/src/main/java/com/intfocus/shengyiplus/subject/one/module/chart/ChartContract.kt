package com.intfocus.shengyiplus.subject.templateone.curvechart

import com.intfocus.shengyiplus.subject.one.entity.Chart
import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ChartContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(entity: Chart)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(rootId: Int, index: Int)
    }
}
