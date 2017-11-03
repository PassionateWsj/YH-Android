package com.intfocus.yh_android.business.subject.templateone.entity

import java.io.Serializable
import java.util.*

/**
 * 仪表盘实体对象
 * Created by zbaoliang on 17-4-28.
 */
class MererDetailEntity : Serializable {
    var name: String? = null
    var data: ArrayList<PageData>? = null

    class PageData : Serializable {

        var parts: String? = null
        var title: String? = null
    }
}
