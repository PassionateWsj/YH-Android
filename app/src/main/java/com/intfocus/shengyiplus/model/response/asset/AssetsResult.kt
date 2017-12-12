package com.intfocus.shengyiplus.model.response.asset

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/12.
 */
class AssetsResult : BaseResult() {
    @SerializedName("data")
    var data: AssetsMD5? = null
}
