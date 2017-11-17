package com.intfocus.yhdev.business.subject.templateone.rootpage

import com.intfocus.yhdev.general.bean.Report
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
        fun onDataLoaded(reports: List<Report>)
        fun onDataNotAvailable(e: Throwable)
    }

    fun getData(@NotNull uuid: String, @NotNull page: Int, @NotNull callback: LoadDataCallback)
}
