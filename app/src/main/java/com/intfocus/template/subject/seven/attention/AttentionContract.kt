package com.intfocus.template.subject.seven.attention

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface AttentionContract {
    interface View : BaseView<Presenter> {
        // 检查数据是否有更新
        fun onUpdataData()

    }

    interface Presenter : BasePresenter {
        fun loadData()
    }
}