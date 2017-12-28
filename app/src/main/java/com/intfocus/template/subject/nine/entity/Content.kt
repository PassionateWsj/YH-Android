package com.intfocus.template.subject.nine.entity

import java.io.Serializable

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */

class Content : Serializable {
    /**
     * type : single_text
     * key : test_01
     * is_show : 1
     * is_list : 1
     * is_filter : 0
     * is_must : 1
     * data : {"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}
     */
    var type: String? = null
    var key: String? = null
    var is_show: Int? = null
    var is_list: Int? = null
    var is_filter: Int? = null
    var is_must: Int? = null
    var config: String? = null
    var value: String? = null

//    override fun toString(): String {
//        val contentJson = JSONObject()
//        contentJson.put("type", type)
//        contentJson.put("key", key)
//        contentJson.put("is_show", is_show)
//        contentJson.put("is_list", is_list)
//        contentJson.put("is_filter", is_filter)
//        contentJson.put("is_must", is_must)
//        contentJson.put("config", config)
//        contentJson.put("value", value)
//        return contentJson.toString()
//    }
}
