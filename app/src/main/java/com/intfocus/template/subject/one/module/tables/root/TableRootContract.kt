package com.intfocus.template.subject.one.module.tables.root

import com.intfocus.template.subject.one.entity.MDetailRootPageRequestResult
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface TableRootContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun dataLoaded(data: MDetailRootPageRequestResult)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(rootId: Int, index: Int)
    }
}