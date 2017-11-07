package com.intfocus.hx.general.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.hx.business.dashboard.report.mode.CategoryBean
import com.intfocus.hx.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class ReportListResult : BaseResult() {
    @SerializedName("data")
    var data: List<CategoryBean>? = null
}
