package com.intfocus.syp_template.collection.callback

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
interface LoadDataCallback<in T> {
    fun onSuccess(data: T)
    fun onError(e: Throwable)
    fun onComplete()
}
