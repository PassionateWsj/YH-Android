package com.intfocus.template.subject.seven.bean

import com.intfocus.template.model.response.BaseResult

/**
 * ****************************************************
 * author jameswong
 * created on: 18/02/07 上午11:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
data class ConcernItemsBean(var data: List<ConcernItem>? = null) : BaseResult() {
    data class ConcernItem(var obj_id: String? = null,
                           var obj_name: String? = null,
                           var rep_code: String? = null,
                           var obj_num: String? = null,
                           var concern_item_group_list: List<ConcernGroupBean.ConcernGroup>? = null)
}