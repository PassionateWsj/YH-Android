package com.intfocus.syp_template.business.subject.template.one.table

import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity
import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult
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
        fun onDataLoaded(data: ModularTwo_UnitTableEntity)

        fun onDataNotAvailable(e: Throwable?)
    }

    interface TableRootLoadDataCallback {
        fun onDataLoaded(data: MDetailRootPageRequestResult)

        fun onDataNotAvailable(e: Throwable?)
    }

    fun getData(@NotNull dataJson: String, @NotNull callbackTableContent: TableContentLoadDataCallback)
    fun getRootData(@NotNull uuid: String, @NotNull index: Int, @NotNull callbackTableRoot: TableRootLoadDataCallback)
}