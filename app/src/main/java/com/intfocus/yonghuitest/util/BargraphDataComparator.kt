package com.intfocus.yonghuitest.util

import com.intfocus.yonghuitest.subject.template_v1.entity.BargraphComparator

import java.util.Comparator

/**
 * Created by zbaoliang on 17-5-23.
 */
class BargraphDataComparator : Comparator<BargraphComparator> {

    override fun compare(obj1: BargraphComparator, obj2: BargraphComparator): Int {
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
