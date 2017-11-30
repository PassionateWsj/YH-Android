package com.intfocus.template.ui

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.SYPApplication
import com.intfocus.template.listener.UMSharedListener
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.ImageUtil
import com.intfocus.template.util.K.API_COMMENT_MOBILE_PATH
import com.intfocus.template.util.LoadingUtils
import com.intfocus.template.util.PageLinkManage.pageLink
import com.intfocus.template.util.ToastUtils
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.item_action_bar.*

/**
 *
 * @author lijunjie
 * @date 16/1/14
 */
open class BaseActivity : AppCompatActivity() {
    lateinit var mApp: SYPApplication
    lateinit var mAppContext: Context
    lateinit var mUserSP: SharedPreferences
    lateinit var popupWindow: PopupWindow
    var loadingDialog: Dialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mApp = application as SYPApplication
        mAppContext = mApp.applicationContext
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    }

    override fun onResume() {
        super.onResume()
        if (supportActionBar != null) {
            supportActionBar!!.hide()
        }
    }

    protected fun showDialog(context: Context) {
        loadingDialog = LoadingUtils.createLoadingDialog(context, false)
        loadingDialog!!.show()
    }

    protected fun hideLoading() {
        if (loadingDialog != null && loadingDialog!!.isShowing()) {
            loadingDialog!!.dismiss()
        }
    }

    /**
     * 标题栏点击设置按钮显示下拉菜单
     */
    fun launchDropMenuActivity(url: String) {
        val contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v2, null)
        val copyLinkButton = contentView.findViewById<LinearLayout>(R.id.ll_copylink)
        copyLinkButton.visibility = if (url.startsWith("http")) View.VISIBLE else View.GONE

        //设置弹出框的宽度和高度
        popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        //进入退出的动画
        popupWindow.showAsDropDown(iv_banner_setting)
    }

    /**
     * 返回
     */
    open fun dismissActivity(v: View) {
        super.onBackPressed()
    }

    /**
     * 报表基础功能 -> 评论
     */
    fun comment(activity: Activity, objectId: String, objectType: String, objectTitle: String) {
        val link = String.format(API_COMMENT_MOBILE_PATH, ConfigConstants.kBaseUrl, "v2", objectId, objectType)
        pageLink(activity, objectTitle, link, objectId, "-1", objectType)
    }

    /**
     * 报表基础功能 -> 分享
     */
    fun share(activity: Activity, url: String) {
        if (url.toLowerCase().endsWith(".pdf")) {
            ToastUtils.show(activity, "暂不支持 PDF 分享")
            return
        }

        val bmpScreenShot = ImageUtil.takeScreenShot(activity)
        if (bmpScreenShot == null) {
            ToastUtils.show(activity, "截图失败")
        }

        val image = UMImage(activity, bmpScreenShot!!)
        ShareAction(activity)
                .withText("截图分享")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .withMedia(image)
                .setCallback(UMSharedListener())
                .open()

        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
        ActionLogUtil.actionLog("分享")
    }
}
