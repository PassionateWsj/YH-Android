package com.intfocus.syp_template.model.response.home

import com.google.gson.annotations.SerializedName
import com.intfocus.syp_template.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class NotificationsResult : BaseResult() {
    @SerializedName("data")
    var data: List<Notifications>? = null
}
