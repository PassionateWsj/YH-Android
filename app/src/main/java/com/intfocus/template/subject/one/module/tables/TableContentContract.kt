package com.intfocus.template.subject.one.module.tables

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.subject.one.entity.Tables

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface TableContentContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(data: Tables)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(data: Tables)
    }
}
