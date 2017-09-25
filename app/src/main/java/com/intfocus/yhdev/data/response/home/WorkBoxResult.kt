package com.intfocus.yhdev.data.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.yhdev.dashboard.work_box.WorkBoxItem
import com.intfocus.yhdev.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class WorkBoxResult : BaseResult() {
    @SerializedName("data")
    var data: List<WorkBoxItem>? = null
}
