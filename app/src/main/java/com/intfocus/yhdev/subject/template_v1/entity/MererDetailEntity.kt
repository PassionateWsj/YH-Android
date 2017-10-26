package com.intfocus.yhdev.subject.template_v1.entity

import java.io.Serializable
import java.util.ArrayList

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
