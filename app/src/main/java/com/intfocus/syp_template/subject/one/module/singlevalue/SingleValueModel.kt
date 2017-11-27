package com.intfocus.syp_template.subject.templateone.singlevalue

import com.intfocus.syp_template.subject.one.entity.SingleValue
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
interface SingleValueModel {

    interface LoadDataCallback {
        fun onDataLoaded(data: SingleValue)

        fun onDataNotAvailable(e: Throwable?)
    }
    fun getData(@NotNull rootId: Int, @NotNull index: Int, @NotNull callback: LoadDataCallback)

}
