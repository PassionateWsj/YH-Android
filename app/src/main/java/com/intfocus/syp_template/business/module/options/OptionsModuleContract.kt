package com.intfocus.syp_template.module.options

import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
interface OptionsModuleContract {
    interface View: BaseView<Presenter>{
        fun initModule(entity: OptionsEntity)
    }

    interface Presenter: BasePresenter{
        fun loadData(mParam: String)
        fun update(entity: OptionsEntity, key: String)
    }
}
