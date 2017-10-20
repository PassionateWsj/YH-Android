package com.intfocus.yhdev.subject.template_v1.contract

import com.intfocus.yhdev.base.BasePresenter
import com.intfocus.yhdev.base.BaseView

/**
 * ****************************************************
 * author jameswong
 * created on: 17/10/20 下午3:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface SingleValueContract {
    interface View : BaseView<Presenter> {

    }

    interface Presenter : BasePresenter {
        fun loadData(mParam: String)
    }
}