package com.intfocus.syp_template.subject.templateone.rootpage

import com.intfocus.syp_template.model.entity.Report
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

    fun getData(@NotNull uuid: String, @NotNull pageId: Int, @NotNull callback: LoadDataCallback)
}
