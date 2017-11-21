package com.intfocus.syp_template.collection.entity

import com.intfocus.syp_template.collection.entity.Content
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
