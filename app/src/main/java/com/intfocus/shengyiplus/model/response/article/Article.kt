package com.intfocus.shengyiplus.model.response.article

import com.google.gson.annotations.SerializedName
import com.intfocus.shengyiplus.dashboard.mine.bean.InstituteDataBean
import com.intfocus.shengyiplus.model.response.BaseResult

/**
 * Created by CANC on 2017/7/31.
 */

class ArticleResult : BaseResult() {
    @SerializedName("data")
    var data: List<InstituteDataBean> = listOf()

    var current_page: Int = 0
    var page_size: Int = 0
    var total_count: Int = 0
    var total_page: Int = 0
}
