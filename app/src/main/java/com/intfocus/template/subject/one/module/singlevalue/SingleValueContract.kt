package com.intfocus.template.subject.templateone.singlevalue

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.subject.one.entity.SingleValue

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
        fun showData(data: SingleValue)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(rootId: Int, index: Int)
    }
}
