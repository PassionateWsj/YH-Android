package com.intfocus.template.subject.nine.entity

import java.io.Serializable
import java.util.*

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
class CollectionEntity : Serializable {
    var name: String? = null
    var data: ArrayList<PageData>? = null

    class PageData : Serializable {
        var title: String? = null
        var content: String? = null
    }
}
