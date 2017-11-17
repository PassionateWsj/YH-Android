package com.intfocus.yhdev.module.text

import com.intfocus.yhdev.general.base.BasePresenter
import com.intfocus.yhdev.general.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
interface TextModuleContract {
    interface View: BaseView<Presenter>{
        fun initModule(entity: TextEntity)
    }

    interface Presenter: BasePresenter{
        fun loadData(mParam: String)
        fun update(entity: TextEntity, key: String)
    }
}
