package com.intfocus.shengyiplus.subject.nine.module.options

import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
interface OptionsModuleContract {
    interface View : BaseView<Presenter> {
        fun initModule(entity: OptionsEntity)
    }

    interface Presenter : BasePresenter {
        fun loadData(mParam: String)
        fun update(entity: OptionsEntity, key: String)
    }
}
