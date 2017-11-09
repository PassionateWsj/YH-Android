package com.intfocus.yhdev.business.collection.entity

import com.google.gson.annotations.SerializedName
import org.json.JSONObject

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe
 */
class CollectionRequestBody {
    var data: Data? = null

    class Data {
        var acquisition_id: String = ""
        var content: JSONObject? = null
        var user_num: String = ""
        var h1: String = ""
        var h2: String = ""
        var h3: String = ""
        var h4: String = ""
        var h5: String = ""
        var local_created_at: String = ""
        var local_updated_at: String = ""
    }
}
