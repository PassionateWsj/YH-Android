package com.intfocus.template.subject.seven.indicatorlist

import com.intfocus.template.subject.seven.bean.ConcernGroupBean
import com.intfocus.template.subject.seven.bean.ConcernItemsBean

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:49
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface IndicatorListModel {
    interface OnConcernedListResult {
        fun onLoadListDataSuccess(data: List<ConcernItemsBean.ConcernItem>)
        fun onLoadItemListDataSuccess(data: List<List<ConcernGroupBean.ConcernGroup>>)
        fun onLoadDataFailure(e: Throwable)
    }

    fun getConcernedListByUser(controlId: String, repCode: String, callback: OnConcernedListResult)
}