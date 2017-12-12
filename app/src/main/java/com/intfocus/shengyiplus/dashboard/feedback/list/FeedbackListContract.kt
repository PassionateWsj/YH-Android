package com.intfocus.shengyiplus.dashboard.feedback.list

import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackList

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
