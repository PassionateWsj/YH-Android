package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.subject.one.entity.Filter

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 上午10:57
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ModeContract {

    interface View : BaseView<Presenter> {
        fun dataLoaded(reportPage: List<String>, filter: Filter)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String)
        fun saveFilterSelected(display: String)
    }
}
