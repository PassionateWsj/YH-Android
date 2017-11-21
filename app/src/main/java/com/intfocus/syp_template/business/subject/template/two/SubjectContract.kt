package com.intfocus.syp_template.business.subject.template.two

import android.content.Context
import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
interface SubjectContract {
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
