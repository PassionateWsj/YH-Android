package com.intfocus.template.subject.seven

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.attention.Test1
import com.intfocus.template.model.response.attention.Test2

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface MyAttentionContract {
    interface View : BaseView<Presenter> {
        // 检查数据是否有更新
        fun onUpdateData(data: Test2)
    }

    interface Presenter : BasePresenter {
        fun loadData(user_num:String)
    }
}