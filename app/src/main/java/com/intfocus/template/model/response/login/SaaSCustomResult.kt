package com.intfocus.template.model.response.login

import com.google.gson.annotations.SerializedName
import com.intfocus.template.model.response.BaseResult

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/17 上午11:52
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class SaaSCustomResult : BaseResult() {
    @SerializedName("data")
    var data: List<SaaSCustomItem> = listOf()

    class SaaSCustomItem {
        var user_num: String? = null
        var app_name: String? = null
        var app_ip: String? = null
        var user_name: String? = null
        var app_id: String? = null
        var datasource_id: String? = null
    }
}