package com.intfocus.shengyiplus.subject.nine

import android.content.Context
import com.intfocus.shengyiplus.base.BasePresenter
import com.intfocus.shengyiplus.base.BaseView
import com.intfocus.shengyiplus.subject.nine.entity.CollectionEntity

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
        fun loadData(reportId: String, templateId: String, groupId: String)
        fun submit(ctx: Context)
    }
}
