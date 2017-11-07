package com.intfocus.hx.business.subject.template.one

import android.content.Context
import com.intfocus.hx.general.base.BasePresenter
import com.intfocus.hx.general.base.BaseView
import com.intfocus.hx.business.subject.templateone.entity.MererDetailEntity

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
        fun initRootView(entity: MererDetailEntity)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context, groupId: String, reportId: String)
    }
}