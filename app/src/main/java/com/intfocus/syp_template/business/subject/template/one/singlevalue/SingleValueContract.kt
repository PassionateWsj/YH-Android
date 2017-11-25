package com.intfocus.syp_template.business.subject.templateone.singlevalue

import com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitSingleValue
import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface SingleValueContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(data: MDRPUnitSingleValue)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(rootId: Int, index: Int)
    }
}
