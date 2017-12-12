package com.intfocus.shengyiplus.model.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by liuruilin on 2017/8/11.
 */
class UserInfoResult : BaseResult() {
    @SerializedName("data")
    var data: UserInfo? = null
}
