package com.intfocus.yhdev.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yhdev.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yhdev.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */
class HomeMsgResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroupItem>? = null
}
