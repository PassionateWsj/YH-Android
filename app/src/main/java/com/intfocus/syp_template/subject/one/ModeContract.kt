package com.intfocus.syp_template.subject.one

import android.content.Context
import com.intfocus.syp_template.base.BasePresenter
import com.intfocus.syp_template.base.BaseView
import com.intfocus.syp_template.model.response.filter.MenuItem
import com.intfocus.syp_template.subject.one.entity.Filter

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
        fun initRootView(reportPage: List<String>, filter: Filter)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String)
        fun saveFilterSelected(display: String)
    }
}
