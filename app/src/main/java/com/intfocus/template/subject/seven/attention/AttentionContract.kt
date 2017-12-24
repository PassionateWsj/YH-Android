package com.intfocus.template.subject.seven.attention

import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.attention.AttentionItem

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface AttentionContract {
    interface View : BaseView<Presenter> {
        /**
         * 读取搜索结果失败
         *
         * @param e 失败信息
         */
        fun onResultFailure(e: Throwable)

        /**
         * 读取搜索结果成功
         *
         * @param data 关键字
         */
        fun onResultSuccess(data: List<AttentionItem>)

        fun concernOrCancelConcernResult(isConcernSuccess: Boolean)

    }

    interface Presenter : BasePresenter {
        fun loadData(concerned: Boolean)
        fun loadData(keyWord: String, concerned: Boolean)
        fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String)
    }
}