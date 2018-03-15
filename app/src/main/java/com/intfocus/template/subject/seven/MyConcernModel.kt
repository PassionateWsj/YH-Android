package com.intfocus.template.subject.seven

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
interface MyConcernModel<out T> {
    fun getData(reportId: String, objNum: String, callback: com.intfocus.template.model.callback.LoadDataCallback<T>)
    fun getFilterData(repCode: String, reportId: String, callback: com.intfocus.template.model.callback.LoadDataCallback<Filter>)
}