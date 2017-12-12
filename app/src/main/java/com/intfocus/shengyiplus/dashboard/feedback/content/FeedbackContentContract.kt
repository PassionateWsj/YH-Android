package com.intfocus.shengyiplus.dashboard.feedback.content

import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView
import com.intfocus.shengyiplus.model.response.mine_page.FeedbackContent

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
