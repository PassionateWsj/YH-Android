package com.intfocus.template.subject.seven.indicatorlist

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
    interface OnConcernedListResult{
        fun onLoadDataSuccess()
        fun onLoadDataFailure(e:Throwable)
    }
    fun getConcernedListByUser()
}