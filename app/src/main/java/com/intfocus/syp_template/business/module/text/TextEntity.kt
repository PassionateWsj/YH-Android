package com.intfocus.syp_template.module.text

import org.json.JSONObject

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
class TextEntity {
    /**
     * element_type :
     * title : 问题反馈标题
     * sub_title :
     * hint : 问题简单描述
     * value :
     */
    var title: String = ""
    var value: String = ""
    var element_type: String = ""
    var sub_title: String = ""
    var hint: String = ""

    override fun toString(): String {
        var textJson = JSONObject()
        textJson.put("title", title)
        textJson.put("sub_title", sub_title)
        textJson.put("hint", hint)
        textJson.put("element_type", element_type)
        textJson.put("value", value)
        return textJson.toString()
    }
}
