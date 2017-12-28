package com.intfocus.template.subject.nine

import android.content.Context
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.subject.nine.entity.CollectionEntity

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
interface CollectionContract {
    interface View : BaseView<Presenter> {
        fun initRootView(entity: CollectionEntity)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context,reportId: String, templateId: String, groupId: String)
        fun submit(ctx: Context)
    }
}
