package com.intfocus.shengyiplus.model.response.filter

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by CANC on 2017/8/3.
 */
class MenuResult : BaseResult() {
    @SerializedName("data")
    var data: List<Menu> = listOf()
}
