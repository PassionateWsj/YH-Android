package com.intfocus.template.ui

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import com.intfocus.template.SYPApplication.globalContext

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
open class BaseModuleFragment : Fragment() {
    var act: FragmentActivity? = activity
    var ctx: Context = globalContext
}
