package com.intfocus.yhdev.subject.template_v1.singlevalue

import com.intfocus.yhdev.base.BasePresenter
import com.intfocus.yhdev.base.BaseView
import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSingleValue

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
        fun loadData(mParam: String)
    }
}
