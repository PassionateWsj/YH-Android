package com.intfocus.yh_android.data.response.filter

import com.google.gson.annotations.SerializedName
import com.intfocus.yh_android.data.response.BaseResult

/**
 * Created by CANC on 2017/8/3.
 */
class MenuResult : BaseResult() {
    @SerializedName("data")
    var data: List<Menu> = listOf()
}
