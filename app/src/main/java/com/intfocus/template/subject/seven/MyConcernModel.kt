package com.intfocus.template.subject.seven

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
interface MyConcernModel {
    interface LoadDataCallback {
        fun onDataLoaded(data: Test2,filter: Filter)
        fun onDataNotAvailable(e: Throwable)
    }
    fun getData(userNum: String,filterId: String, callback: LoadDataCallback)
}