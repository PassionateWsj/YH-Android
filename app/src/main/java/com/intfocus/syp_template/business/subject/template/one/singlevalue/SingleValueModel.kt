package com.intfocus.syp_template.business.subject.templateone.singlevalue

import com.intfocus.syp_template.business.subject.template.one.entity.MDRPUnitSingleValue
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
        fun onDataLoaded(data: MDRPUnitSingleValue)

        fun onDataNotAvailable(e: Throwable?)
    }
    fun getData(@NotNull uuid: String, @NotNull index: Int, @NotNull callback: LoadDataCallback)

}
