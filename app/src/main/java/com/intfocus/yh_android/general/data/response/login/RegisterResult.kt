package com.intfocus.yh_android.general.data.response.login

import com.google.gson.annotations.SerializedName
import com.intfocus.yh_android.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/29.
 */
class RegisterResult : BaseResult() {
    @SerializedName("data")
    var data: String? = null
}
