package com.intfocus.spy_template.general.data.response.scanner

import com.google.gson.annotations.SerializedName
import com.intfocus.spy_template.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class StoreListResult : BaseResult() {
    @SerializedName("data")
    var data: List<StoreItem>? = null
}
