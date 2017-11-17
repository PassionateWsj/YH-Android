package com.intfocus.yhdev.general.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yhdev.business.dashboard.kpi.bean.KpiGroupItem
import com.intfocus.yhdev.general.data.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */
class HomeMsgResult : BaseResult() {
    @SerializedName("data")
    var data: List<KpiGroupItem>? = null
}
