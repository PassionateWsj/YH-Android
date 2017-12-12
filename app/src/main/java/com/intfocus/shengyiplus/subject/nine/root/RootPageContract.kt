package com.intfocus.shengyiplus.subject.nine.root


import com.intfocus.shengyiplus.subject.nine.entity.RootPageRequestResult
import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView

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
