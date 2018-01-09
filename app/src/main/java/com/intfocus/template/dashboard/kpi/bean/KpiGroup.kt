package com.intfocus.template.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiGroup : Serializable {
    var dashboard_type: String? = null
    var group_name: String? = null
    var index: Int? = null
    var data: List<KpiGroupItem>? = null
}
