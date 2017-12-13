package com.intfocus.template.dashboard.feedback.content

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.mine_page.FeedbackContent

/**
 * @author liuruilin
 * @data 2017/12/6
 * @describe
 */
interface FeedbackContentContract {
    interface View: BaseView<Presenter> {
        fun showNullPage()
        fun showContent(data: FeedbackContent)
    }

    interface Presenter: BasePresenter {
        fun getContent(id: Int)
    }
}
