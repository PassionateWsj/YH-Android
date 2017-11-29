package com.intfocus.template.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.template.dashboard.workbox.WorkBoxItem
import com.intfocus.template.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class WorkBoxResult : BaseResult() {
    @SerializedName("data")
    var data: List<WorkBoxItem>? = null
}
