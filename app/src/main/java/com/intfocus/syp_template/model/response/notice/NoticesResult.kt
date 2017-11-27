package com.intfocus.syp_template.model.response.notice

import com.intfocus.syp_template.model.response.BaseResult

/**
 * Created by CANC on 2017/8/1.
 */
class NoticesResult : BaseResult() {

    var total: Int = 0
    var curr_page: Int = 0
    var page_size: Int = 0
    var total_page: Int = 0
    var data: List<Notice> = listOf()
}
