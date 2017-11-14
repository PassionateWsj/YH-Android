package com.intfocus.syp_template.business.subject.templateone.rootpage

import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView
import com.intfocus.syp_template.general.bean.Report

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
        fun loadData(uuid: String, page: Int)
    }
}
