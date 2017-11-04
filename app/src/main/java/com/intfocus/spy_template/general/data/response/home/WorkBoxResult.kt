package com.intfocus.spy_template.general.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.spy_template.business.dashboard.workbox.WorkBoxItem
import com.intfocus.spy_template.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class WorkBoxResult : BaseResult() {
    @SerializedName("data")
    var data: List<WorkBoxItem>? = null
}
