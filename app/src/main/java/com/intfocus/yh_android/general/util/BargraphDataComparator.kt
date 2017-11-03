package com.intfocus.yh_android.general.util

import java.util.*

/**
 * Created by zbaoliang on 17-5-23.
 */
class BargraphDataComparator : Comparator<com.intfocus.yh_android.business.subject.template.one.entity.BargraphComparator> {

    override fun compare(obj1: com.intfocus.yh_android.business.subject.template.one.entity.BargraphComparator, obj2: com.intfocus.yh_android.business.subject.template.one.entity.BargraphComparator): Int {
        val v1 = obj1.data.toFloat()
        val v2 = obj2.data.toFloat()
        return if (v1 > v2) {
            1
        } else if (v1 < v2) {
            -1
        } else {
            0
        }
    }
}
