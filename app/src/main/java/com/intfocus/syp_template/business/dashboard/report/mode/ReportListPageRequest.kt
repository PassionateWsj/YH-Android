package com.intfocus.syp_template.business.dashboard.report.mode

/**
 * Created by liuruilin on 2017/6/16.
 */
class ReportListPageRequest(var isSuccess: Boolean, var state: Int) {
    var categroy_list: List<CategoryBean>? = null
}