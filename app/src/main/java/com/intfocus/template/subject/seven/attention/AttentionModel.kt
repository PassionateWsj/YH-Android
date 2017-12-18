package com.intfocus.template.subject.seven.attention

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
        fun onDataLoaded()
        fun onDataNotAvailable(e: Throwable)
    }
    fun getData()
}