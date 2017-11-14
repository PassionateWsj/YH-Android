package com.intfocus.syp_template.business.dashboard.mine.bean

import com.intfocus.syp_template.general.data.response.mine_page.UserInfo

/**
 * Created by liuruilin on 2017/6/7.
 */

class UserInfoRequest(var isSuccess: Boolean, var stateCode: Int) {
    var userInfoBean: UserInfo? = null
}
