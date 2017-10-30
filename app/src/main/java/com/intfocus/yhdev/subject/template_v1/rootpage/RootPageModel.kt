package com.intfocus.yhdev.subject.template_v1.rootpage

import com.intfocus.yhdev.subject.template_v1.entity.msg.MDetailRootPageRequestResult
import org.jetbrains.annotations.NotNull

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 上午10:05
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface RootPageModel {

    interface LoadDataCallback {
        fun onDataLoaded(data: MDetailRootPageRequestResult)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(@NotNull mParam: String, @NotNull callback: LoadDataCallback)
}