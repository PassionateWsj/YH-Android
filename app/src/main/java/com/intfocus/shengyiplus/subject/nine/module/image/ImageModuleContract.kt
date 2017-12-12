package com.intfocus.shengyiplus.subject.nine.module.image

import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
interface ImageModuleContract {
    interface View : BaseView<Presenter> {
        fun initModule(entity: ImageEntity)
    }

    interface Presenter : BasePresenter {
        fun loadData(mParam: String)
        fun update(imageEntity: ImageEntity, key: String)
    }
}
