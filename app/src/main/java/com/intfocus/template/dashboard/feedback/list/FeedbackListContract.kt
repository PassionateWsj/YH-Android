package com.intfocus.template.dashboard.feedback.list

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.mine_page.FeedbackList

/**
 * @author liuruilin
 * @data 2017/12/5
 * @describe
 */
interface FeedbackListContract {

    interface View: BaseView<Presenter> {
        fun showNullPage()
        fun showList(data: FeedbackList)
    }

    interface Presenter: BasePresenter {
        fun getList()
    }
}
