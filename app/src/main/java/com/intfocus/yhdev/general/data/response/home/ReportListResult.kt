package com.intfocus.yhdev.general.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yhdev.business.dashboard.report.mode.CategoryBean
import com.intfocus.yhdev.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class ReportListResult : BaseResult() {
    @SerializedName("data")
    var data: List<CategoryBean>? = null
}
