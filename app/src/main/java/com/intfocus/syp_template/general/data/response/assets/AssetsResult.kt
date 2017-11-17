package com.intfocus.syp_template.general.data.response.assets

import com.google.gson.annotations.SerializedName
import com.intfocus.syp_template.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/12.
 */
class AssetsResult : BaseResult() {
    @SerializedName("data")
    var data: AssetsMD5? = null
}
