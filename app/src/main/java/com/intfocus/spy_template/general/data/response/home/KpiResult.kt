package com.intfocus.spy_template.general.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.spy_template.business.dashboard.kpi.bean.KpiGroup
import com.intfocus.spy_template.general.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */

class KpiResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroup>? = null
}
