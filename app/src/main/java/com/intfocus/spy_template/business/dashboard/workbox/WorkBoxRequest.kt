package com.intfocus.spy_template.business.dashboard.workbox

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxRequest(var isSuccess: Boolean, var stateCode: Int) {
    var workBoxDatas: List<WorkBoxItem>? = null
}