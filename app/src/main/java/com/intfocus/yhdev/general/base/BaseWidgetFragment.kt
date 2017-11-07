package com.intfucos.yhdev.base

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.intfocus.yhdev.YHApplication.globalContext

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
open class BaseWidgetFragment: Fragment() {
    var act: FragmentActivity? = activity
    var ctx: Context = globalContext
}