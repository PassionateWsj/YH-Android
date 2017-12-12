package com.intfocus.template.ui

import android.app.Dialog
import android.content.Context
import android.support.v4.app.Fragment
import com.intfocus.template.util.LoadingUtils

/**
 * @author liuruilin
 * @date 2017/5/8
 */
abstract class BaseFragment : Fragment() {
    lateinit var ctx: Context
    var loadingDialog: Dialog? = null

    override fun onAttach(context: Context) {
        ctx = context
        super.onAttach(context)
    }

    protected fun showDialog(context: Context?) {
        loadingDialog = LoadingUtils.createLoadingDialog(context, false)
        loadingDialog!!.show()
    }

    protected fun hideLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing) {
            loadingDialog!!.dismiss()
        }
    }
}
