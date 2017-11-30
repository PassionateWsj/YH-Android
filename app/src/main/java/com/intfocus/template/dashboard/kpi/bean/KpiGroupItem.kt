package com.intfocus.template.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiGroupItem : Serializable {


    /**
     * id : 108
     * title : 本店销售额
     * dashboard_type : number3
     * report_title : 门店销售概况
     * target_url : /mobile/v2/group/%@/template/2/report/49
     * template_id : 2
     * obj_id : 49
     * obj_title : 门店销售概况
     * obj_link : /mobile/v2/group/%@/template/2/report/49
     * unit : 万元
     * memo1 : null
     * memo2 : null
     * data : {"high_light":{"percentage":0,"number":null,"compare":null,"arrow":0},"chart_data":[]}
     * group_name : 生意概况
     * is_stick : false
     */

    var title: String? = null
    var dashboard_type: String? = null
    var report_title: String? = null
    var target_url: String? = null
    var template_id: String? = null
    var obj_id: String? = null
    var obj_title: String? = null
    var obj_link: String? = null
    var unit: String? = null
    var memo1: String? = null
    var memo2: String? = null
    var data: KpiGroupItemData? = null
    var group_name: String? = null
    var is_stick: Boolean = false
    //新增msg信息
    var id: Int? = 0
    var content: String? = null
    var created_at: String? = null
}
