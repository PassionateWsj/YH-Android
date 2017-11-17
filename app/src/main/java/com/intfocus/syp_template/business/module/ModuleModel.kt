package com.intfucos.yhdev.module

import com.intfucos.yhdev.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
interface ModuleModel<T> {
    fun analyseData(params: String, callback: LoadDataCallback<T>)
    fun insertDb(value: String, key: String)
}
