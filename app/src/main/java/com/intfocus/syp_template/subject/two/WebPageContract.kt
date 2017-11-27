package com.intfocus.syp_template.subject.two

import com.intfocus.syp_template.base.BasePresenter
import com.intfocus.syp_template.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
interface WebPageContract {
    interface View: BaseView<Presenter> {
        fun show(path: String)
        fun showPDF(path: String)
        fun refresh()
        fun goBack()
        fun finishActivity()
        fun setBannerTitle(title: String)
        fun setBannerVisibility(state: Int)
        fun setBannerBackVisibility(state: Int)
        fun setBannerMenuVisibility(state: Int)
        fun setAddressFilterText(text: String)
    }

    interface Presenter: BasePresenter {
        fun load(reportId: String, templateId: String, groupId: String, url: String)
    }
}
