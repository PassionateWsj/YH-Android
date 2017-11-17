package com.intfocus.syp_template.collection

import android.content.Context
import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView
import com.intfocus.syp_template.collection.entity.CollectionEntity

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
interface CollectionContract {
    interface View: BaseView<Presenter> {
        fun initRootView(entity: CollectionEntity)
    }

    interface Presenter: BasePresenter {
        fun loadData(reportId: String, templateId: String, groupId: String)
        fun submit(ctx: Context)
    }
}
