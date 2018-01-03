package com.intfocus.template.subject.nine.collectionlist

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.entity.Collection

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
interface CollectionListContract {
    interface View : BaseView<Presenter> {
        fun updateData(dataList:List<Collection>)
    }

    interface Presenter : BasePresenter {
        fun loadData()
        fun loadData(key:String)
    }
}
