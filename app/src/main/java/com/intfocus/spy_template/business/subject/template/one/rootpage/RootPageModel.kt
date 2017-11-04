package com.intfocus.spy_template.business.subject.templateone.rootpage

import com.intfocus.spy_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult
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
