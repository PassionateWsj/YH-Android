package com.intfocus.syp_template.model.response.login

import com.google.gson.annotations.SerializedName
import com.intfocus.syp_template.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/29.
 */
class RegisterResult : BaseResult() {
    @SerializedName("data")
    var data: String? = null
}
