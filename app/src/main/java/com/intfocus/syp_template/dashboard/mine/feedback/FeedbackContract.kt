package com.intfocus.syp_template.dashboard.mine.feedback

import com.intfocus.syp_template.base.BasePresenter
import com.intfocus.syp_template.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/28
 * @describe
 */
interface FeedbackContract {
    interface View: BaseView<Presenter> {
        fun showNullPage()
        fun showList()
    }

    interface Presenter: BasePresenter {
        fun load()
    }
}