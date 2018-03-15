package com.intfocus.template.subject.seven

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.seven.bean.ConcernComponentBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface MyConcernContract {
    interface View : BaseView<Presenter> {
        // 检查数据是否有更新
        fun initFilterView(filter: Filter)
        fun initFilterView(data: Test2, filter: Filter)
        fun generateReportItemViews(data: List<ConcernComponentBean.ConcernComponent>)
        fun showErrorMsg(msg:String)
    }

    interface Presenter : BasePresenter {
        fun loadData(reportId: String)
        fun loadFilterData(repCode: String,reportId: String)
        fun loadData(reportId: String, objNum: String)
    }
}