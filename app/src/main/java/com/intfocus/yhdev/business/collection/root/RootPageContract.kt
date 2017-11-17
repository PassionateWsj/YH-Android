package com.intfocus.yhdev.collection.root

import com.intfocus.yhdev.general.base.BasePresenter
import com.intfocus.yhdev.general.base.BaseView
import com.intfocus.yhdev.collection.entity.RootPageRequestResult

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
    interface View: BaseView<Presenter>{
        // 展示数据
        fun insertModule(result: RootPageRequestResult)
    }

    interface Presenter: BasePresenter {
        // 加载数据
        fun loadData(mParam: String)
    }
}
