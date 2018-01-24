package com.intfocus.template.subject.nine.entity

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe
 */
class CollectionRequestBody {
    var data: Data? = null

    class Data {
        var uuid: String = ""
        var report_id: String = ""
        var content: String = ""
        var template_id: String = "9"
        var user_num: String = ""
        var h1: String = ""
        var h2: String = ""
        var h3: String = ""
        var h4: String = ""
        var h5: String = ""
        var created_at: String = ""
        var updated_at: String = ""
    }
}
