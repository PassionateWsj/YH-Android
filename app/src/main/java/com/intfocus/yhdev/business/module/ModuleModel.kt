package com.intfocus.yhdev.module

import com.intfocus.yhdev.collection.callback.LoadDataCallback
import com.intfocus.yhdev.module.image.ImageEntity
import com.intfocus.yhdev.module.text.TextEntity

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
interface ModuleModel<T> {
    fun analyseData(params: String, callback: LoadDataCallback<T>)
    fun insertDb(value: String, key: String)
}
