package com.intfocus.template.subject.nine.entity

import com.alibaba.fastjson.annotation.JSONField
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
    @JSONField(name="is_show")
    var show: Int? = 0
    @JSONField(name="is_list")
    var list: Int? = 0
    @JSONField(name="is_filter")
    var filter: Int? = 0
    @JSONField(name="is_must")
    var must: Int? = 0
    var config: String? = null
    var value: String? = null

}
