package com.intfocus.hx.business.dashboard.app.mode

import com.intfocus.hx.business.dashboard.report.mode.CategoryBean

/**
 * Created by liuruilin on 2017/6/18.
 */
class AppListPageRequest(var isSuccess: Boolean, var state: Int) {
    var categroy_list: List<CategoryBean>? = null
}