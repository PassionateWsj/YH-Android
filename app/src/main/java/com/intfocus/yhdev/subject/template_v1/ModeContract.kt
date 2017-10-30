package com.intfocus.yhdev.subject.template_v1

import android.content.Context
import com.intfocus.yhdev.base.BasePresenter
import com.intfocus.yhdev.base.BaseView
import com.intfocus.yhdev.subject.template_v1.entity.MererDetailEntity

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