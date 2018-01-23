package com.intfocus.template.subject.one

import android.content.Context
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.home.WorkBoxResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 上午10:57
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface WorkBoxContract {

    interface View : BaseView<Presenter> {
        fun dataLoaded(data: WorkBoxResult)
        fun loadDataFailure()
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context)
    }
}
