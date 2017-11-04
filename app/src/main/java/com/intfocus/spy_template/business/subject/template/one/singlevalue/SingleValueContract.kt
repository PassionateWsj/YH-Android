package com.intfocus.spy_template.business.subject.templateone.singlevalue

import com.intfocus.spy_template.general.base.BasePresenter
import com.intfocus.spy_template.general.base.BaseView

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
        fun showData(data: com.intfocus.spy_template.business.subject.template.one.entity.MDRPUnitSingleValue)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(mParam: String)
    }
}