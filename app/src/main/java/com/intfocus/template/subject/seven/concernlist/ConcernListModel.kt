package com.intfocus.template.subject.seven.concernlist

import com.intfocus.template.subject.seven.bean.ConcernListBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface ConcernListModel {
    interface LoadDataCallback {
        fun onDataLoaded(dataList: List<ConcernListBean>)
        fun onDataNotAvailable(e: Throwable)
    }

    interface ConcernCallback {
        fun onConcernResult(isConcernSuccess: Boolean)
    }

    fun getData(keyWord: String, concerned: Boolean, reportId: String, callback: LoadDataCallback)
    fun concernOrCancelConcern(attentionItemId: String, attentionItemName: String, reportId: String, callback: ConcernCallback)
    fun updateConcernListData(reportId: String, callback: com.intfocus.template.model.callback.LoadDataCallback<Boolean>)
}