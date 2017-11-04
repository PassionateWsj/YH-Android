package com.intfocus.spy_template.general.data.response.login

import com.google.gson.annotations.SerializedName
import com.intfocus.spy_template.general.data.response.BaseResult

/**
 * Created by liuruilin on 2017/8/29.
 */
class RegisterResult : BaseResult() {
    @SerializedName("data")
    var data: String? = null
}
