package com.intfucos.yhdev.collection

import android.content.Context
import com.intfucos.yhdev.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
interface CollectionModel<out T> {
    fun getData(reportId: String, templateID: String, groupId: String, callback: LoadDataCallback<T>)
    fun upload(ctx: Context)
}