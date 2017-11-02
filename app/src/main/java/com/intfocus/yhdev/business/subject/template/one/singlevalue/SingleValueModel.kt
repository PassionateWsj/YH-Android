package com.intfocus.yhdev.business.subject.templateone.singlevalue

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
        fun onDataLoaded(data: com.intfocus.yhdev.business.subject.template.one.entity.MDRPUnitSingleValue)

        fun onDataNotAvailable(e: Throwable?)
    }
    fun getData(@NotNull mParam:String,@NotNull callback: LoadDataCallback)

}