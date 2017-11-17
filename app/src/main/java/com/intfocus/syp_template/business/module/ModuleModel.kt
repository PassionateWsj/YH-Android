package com.intfocus.syp_template.module

import com.intfocus.syp_template.collection.callback.LoadDataCallback
import com.intfocus.syp_template.module.image.ImageEntity
import com.intfocus.syp_template.module.text.TextEntity

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
interface ModuleModel<T> {
    fun analyseData(params: String, callback: LoadDataCallback<T>)
    fun insertDb(value: String, key: String)
}
