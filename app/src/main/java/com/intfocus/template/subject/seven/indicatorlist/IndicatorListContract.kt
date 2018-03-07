package com.intfocus.template.subject.seven.indicatorlist

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.subject.seven.bean.ConcernItemsBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:40
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface IndicatorListContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showConcernedListData(data: List<ConcernItemsBean.ConcernItem>)
    }

    interface Presenter : BasePresenter {
        fun loadConcernedList(id: String, rep: String)
        fun updateConcernedList()
        fun loadConcernedListData(filterId: String, concernedId: Array<String>)
    }
}