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
data class ConcernComponentBean(var data: List<ConcernComponent>? = null) : BaseResult() {
    data class ConcernComponent(var name: String? = null, var control_id: String? = null, var type: String? = null, var rep_code: String? = null)
}