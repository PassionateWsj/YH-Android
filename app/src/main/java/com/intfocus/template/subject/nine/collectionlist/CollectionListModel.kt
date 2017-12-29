package com.intfocus.template.subject.nine.collectionlist

import android.content.Context
import com.intfocus.template.model.callback.LoadDataCallback

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 上午11:46
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface CollectionListModel<out T> {
    fun getData(ctx: Context, callback: LoadDataCallback<T>)
    fun upload(ctx: Context)
}