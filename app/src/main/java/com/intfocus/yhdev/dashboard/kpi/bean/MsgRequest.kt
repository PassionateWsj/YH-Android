package com.intfocus.yhdev.dashboard.kpi.bean

/**
 * Created by liuruilin on 2017/6/21.
 */
class MsgRequest(var isSuccess: Boolean, var state: Int, var msg: String) {
    var msgData: MsgResultData? = null
}