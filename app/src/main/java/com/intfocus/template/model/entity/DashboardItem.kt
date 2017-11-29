package com.intfocus.template.model.entity

/**
 * Created by liuruilin on 2017/8/1.
 */
class DashboardItem {
    var obj_link: String? = null
    var obj_title: String? = null
    var obj_id: String? = null
    var template_id: String? = null
    var objectType: String? = null
    var paramsMappingBean: HashMap<String,String>? = null

    constructor()
    constructor(obj_link: String, obj_title: String, obj_id: String, template_id: String, objectType: String) : this() {
        this.obj_link = obj_link
        this.obj_title = obj_title
        this.obj_id = obj_id
        this.template_id = template_id
        this.objectType = objectType
    }

    constructor(obj_link: String, obj_title: String, obj_id: String, template_id: String, objectType: String, paramsMappingBean: HashMap<String,String>) : this() {
        this.obj_link = obj_link
        this.obj_title = obj_title
        this.obj_id = obj_id
        this.template_id = template_id
        this.objectType = objectType
        this.paramsMappingBean = paramsMappingBean
    }

}
