package com.intfocus.hxtest.business.dashboard.kpi.bean

/**
 * Created by liuruilin on 2017/6/21.
 */
class MsgRequest(var isSuccess: Boolean, var state: Int, var msg: String) {
    var msgData: MsgResultData? = null
}
