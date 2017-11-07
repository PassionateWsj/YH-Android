package com.intfucos.yhdev.collection.callback

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
interface LoadDataCallback<in T> {
    fun onDataLoaded(data: T)
    fun onDataNotAvailable(e: Throwable)
}