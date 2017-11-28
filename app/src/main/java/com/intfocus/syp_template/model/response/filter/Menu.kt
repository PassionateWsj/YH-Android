package com.intfocus.syp_template.model.response.filter

import com.intfocus.syp_template.model.response.address.Location

/**
 * Created by CANC on 2017/8/3.
 */
class Menu {
    var type: String? = null
    var current_location: Location? = null
    var data: ArrayList<MenuItem>? = null
}
