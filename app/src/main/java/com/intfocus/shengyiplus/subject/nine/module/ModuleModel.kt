package com.intfocus.shengyiplus.subject.nine.module

import com.intfocus.shengyiplus.model.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
interface ModuleModel<T> {
    fun analyseData(params: String, callback: LoadDataCallback<T>)
    fun insertDb(value: String, key: String)
}
