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
        var acquisition_id: String? = null
        var content: JSONObject? = null
        var user_num: String? = null
        var h1: String? = null
        var h2: String? = null
        var h3: String? = null
        var h4: String? = null
        var h5: String? = null
        var local_created_at: String? = null
        var local_updated_at: String? = null
    }
}