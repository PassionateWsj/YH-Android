package com.intfocus.template.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.template.dashboard.report.mode.CategoryBean
import com.intfocus.template.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class ReportListResult : BaseResult() {
    @SerializedName("data")
    var data: List<CategoryBean>? = null
}
