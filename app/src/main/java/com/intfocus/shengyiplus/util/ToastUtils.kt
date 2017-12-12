package com.intfocus.shengyiplus.util

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.constant.ToastColor

/**
 * Created by CANC on 2017/7/31.
 */
object ToastUtils {
    private var mToast: Toast? = null

    fun show(context: Context, message: String) {
        show(context, message, R.color.co11_syr)
    }

    fun show(context: Context, message: String, toastColor: ToastColor = ToastColor.SUCCESS) {
        if (ToastColor.SUCCESS == toastColor) {
            show(context, message, R.color.co1_syr)
        } else {
            show(context, message, R.color.co11_syr)
        }
    }

    fun show(context: Context, message: String, colorId: Int = 0) {
        if (mToast == null) {
            val view = LinearLayout(context)
            LayoutInflater.from(context).inflate(R.layout.toast, view)

            mToast = Toast(context)
            mToast!!.setGravity(Gravity.TOP or Gravity.FILL_HORIZONTAL, 0, 0)
            mToast!!.duration = Toast.LENGTH_SHORT
            mToast!!.view = view
        }
        val textView = mToast!!.view.findViewById<TextView>(R.id.toast_text)
        if (colorId != 0) {
            textView.setBackgroundColor(ContextCompat.getColor(context, colorId))
        }
        textView.text = message
        mToast!!.show()
    }

    fun showDefault(ctx: Context, info: String) {
        cancel()
        mToast = Toast.makeText(ctx, info, Toast.LENGTH_SHORT)
        mToast!!.show()
    }

    fun cancel() {
        if (mToast != null) {
            mToast!!.cancel()
        }
    }
}
