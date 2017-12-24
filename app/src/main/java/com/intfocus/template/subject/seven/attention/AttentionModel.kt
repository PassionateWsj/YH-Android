package com.intfocus.template.subject.seven.attention

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
interface AttentionModel {
    interface LoadDataCallback {
        fun onDataLoaded(dataList: List<AttentionItem>)
        fun onDataNotAvailable(e: Throwable)
    }

    interface ConcernCallback {
        fun onConcernResult(isConcernSuccess: Boolean)
    }

    fun getData(keyWord: String, concerned: Boolean, callback: LoadDataCallback)
    fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String, callback: ConcernCallback)
//    fun cancelConcern(attentionItemId:String, attentionItemName:String,callback: ConcernCallback)
}