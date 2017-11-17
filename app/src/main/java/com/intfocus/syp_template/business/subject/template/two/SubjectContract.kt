package com.intfocus.syp_template.business.subject.template.two

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
    }

    interface Presenter: BasePresenter {
        fun load(reportId: String, templateId: String, groupId: String)
    }
}
