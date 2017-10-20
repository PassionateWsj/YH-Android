package com.intfocus.yhdev.subject.template_v1.singlevalue

import com.intfocus.yhdev.subject.template_v1.entity.MDRPUnitSingleValue
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

        fun onDataNotAvailable()
    }
    fun getData(@NotNull mParam:String,@NotNull callback: LoadDataCallback)

}