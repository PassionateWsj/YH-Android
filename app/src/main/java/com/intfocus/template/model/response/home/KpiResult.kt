package com.intfocus.template.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.template.dashboard.kpi.bean.KpiGroup
import com.intfocus.template.model.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */

class KpiResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroup>? = null
}
