package com.intfocus.shengyiplus.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class NotificationsResult : BaseResult() {
    @SerializedName("data")
    var data: List<Notifications>? = null
}
