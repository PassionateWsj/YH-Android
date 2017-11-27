package com.intfocus.syp_template.subject.nine.module

import com.intfocus.syp_template.subject.nine.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
interface ModuleModel<T> {
    fun analyseData(params: String, callback: LoadDataCallback<T>)
    fun insertDb(value: String, key: String)
}
