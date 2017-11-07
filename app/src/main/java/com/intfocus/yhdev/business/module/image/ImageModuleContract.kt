package com.intfucos.yhdev.module.image

import android.net.Uri
import com.intfocus.yhdev.general.base.BasePresenter
import com.intfocus.yhdev.general.base.BaseView

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
interface ImageModuleContract {
    interface View: BaseView<Presenter>{
        fun initModule(entity: ImageEntity)
    }

    interface Presenter: BasePresenter{
        fun loadData(mParam: String)
        fun update(imageEntity: ImageEntity, key: String)
    }
}