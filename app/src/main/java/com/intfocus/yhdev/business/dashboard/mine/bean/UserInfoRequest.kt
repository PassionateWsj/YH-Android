package com.intfocus.yhdev.business.dashboard.mine.bean

import com.intfocus.yhdev.general.data.response.mine_page.UserInfo

/**
 * Created by liuruilin on 2017/6/7.
 */

class UserInfoRequest(var isSuccess: Boolean, var stateCode: Int) {
    var userInfoBean: UserInfo? = null
}
