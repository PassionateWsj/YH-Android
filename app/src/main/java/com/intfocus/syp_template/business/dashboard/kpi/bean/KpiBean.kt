package com.intfocus.syp_template.business.dashboard.kpi.bean

import java.io.Serializable

/**
 * Created by liuruilin on 2017/6/21.
 */
class KpiBean : Serializable {
    var index: Int? = 0
    var group_name: String? = null
    var data: List<KpiGroupItem>? = null
}
