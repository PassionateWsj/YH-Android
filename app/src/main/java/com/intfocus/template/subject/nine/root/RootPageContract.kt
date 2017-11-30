package com.intfocus.template.subject.nine.root


import com.intfocus.template.subject.nine.entity.RootPageRequestResult
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface RootPageContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun insertModule(result: RootPageRequestResult)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(mParam: String)
    }
}
