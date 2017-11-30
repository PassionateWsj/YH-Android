package com.intfocus.template.subject.templateone.rootpage

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.entity.Report

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface RootPageContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(reports: List<Report>)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(uuid: String, pageId: Int)
    }
}
