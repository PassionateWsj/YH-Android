package com.intfocus.template.subject.one.module.tables

import com.intfocus.template.subject.one.entity.Tables
import com.intfocus.template.subject.one.entity.MDetailRootPageRequestResult
import org.jetbrains.annotations.NotNull

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:25
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface TableModel {

    interface TableContentLoadDataCallback {
        fun onDataLoaded(data: Tables)

        fun onDataNotAvailable(e: Throwable?)
    }

    interface TableRootLoadDataCallback {
        fun onDataLoaded(data: MDetailRootPageRequestResult)

        fun onDataNotAvailable(e: Throwable?)
    }

    fun getData(@NotNull dataJson: String, @NotNull callbackTableContent: TableContentLoadDataCallback)
    fun getRootData(@NotNull rootId: Int, @NotNull index: Int, @NotNull callbackTableRoot: TableRootLoadDataCallback)
}
