package com.intfocus.hx.general.data.response.filter

import com.google.gson.annotations.SerializedName
import com.intfocus.hx.general.data.response.BaseResult

/**
 * Created by CANC on 2017/8/3.
 */
class MenuResult : BaseResult() {
    @SerializedName("data")
    var data: List<Menu> = listOf()
}