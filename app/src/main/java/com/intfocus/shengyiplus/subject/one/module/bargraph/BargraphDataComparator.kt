package com.intfocus.shengyiplus.subject.one.module.bargraph

import java.util.*

/**
 * Created by zbaoliang on 17-5-23.
 */
class BargraphDataComparator : Comparator<com.intfocus.shengyiplus.subject.one.entity.BargraphComparator> {
    override fun compare(obj1: com.intfocus.shengyiplus.subject.one.entity.BargraphComparator, obj2: com.intfocus.shengyiplus.subject.one.entity.BargraphComparator): Int {
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
