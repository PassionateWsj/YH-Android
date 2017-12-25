package com.intfocus.template.subject.seven

import android.content.Context
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.subject.one.entity.Filter

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface MyConcernContract {
    interface View : BaseView<Presenter> {
        // 检查数据是否有更新
        fun onUpdateData(data: Test2, filter: Filter)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context, groupId: String, templateId: String, reportId: String)
        fun loadData(userNum: String)
        fun loadData(userNum: String, filterId: String)
    }
}