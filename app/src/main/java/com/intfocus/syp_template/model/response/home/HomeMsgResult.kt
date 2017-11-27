package com.intfocus.syp_template.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.syp_template.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.syp_template.model.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */
class HomeMsgResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroupItem>? = null
}
