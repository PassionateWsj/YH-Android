package com.intfocus.template.subject.seven

import com.intfocus.template.model.response.attention.Test2

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface MyAttentionModel {
    interface LoadDataCallback {
        fun onDataLoaded(data: Test2)
        fun onDataNotAvailable(e: Throwable)
    }
    fun getData(user_num: String,callback: LoadDataCallback)
}