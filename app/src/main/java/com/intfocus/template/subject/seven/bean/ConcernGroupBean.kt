package com.intfocus.template.subject.seven.bean

import com.intfocus.template.model.response.BaseResult

/**
 * ****************************************************
 * author jameswong
 * created on: 18/02/07 上午10:23
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
data class ConcernGroupBean(var data: List<ConcernGroup>? = null) : BaseResult() {

    /**
     * real_time : true
     * real_time_api :
     * state_color : 0
     * state_rate : 10%
     * main_data_name : 差异
     * main_data_data : 3421231
     * main_data_format : int
     * main_data_percentage : 1
     * sub_data_data : 3021231232
     * sub_data_format : int
     * sub_data_percentage : 1
     */
    data class ConcernGroup(
            var real_time: Boolean? = null,
            var real_time_api: String? = null,
            var state_color: Int? = null,
            var state_rate: String? = null,
            var main_data_name: String? = null,
            var main_data_data: Int? = null,
            var main_data_format: String? = null,
            var main_data_percentage: Int? = null,
            var sub_data_data: Long? = null,
            var sub_data_format: String? = null,
            var sub_data_percentage: Int? = null
    )
}