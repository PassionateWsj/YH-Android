package com.intfocus.template.subject.seven.indicatorlist

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.attention.ConcernedListData

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
        fun showConcernedListData(data: ConcernedListData)
        fun updateConcernedListTitle(title: String)
    }

    interface Presenter : BasePresenter {
        fun loadConcernedList()
        fun updateConcernedList()
        fun loadConcernedListData(filterId:String, concernedId:Array<String>)
    }
}