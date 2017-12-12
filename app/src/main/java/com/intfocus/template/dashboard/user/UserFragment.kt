package com.intfocus.template.dashboard.user

import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.v4.content.FileProvider
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.BitmapImageViewTarget
import com.google.gson.Gson
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.dashboard.feedback.FeedbackActivity
import com.intfocus.template.dashboard.mine.activity.*
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.login.LoginActivity
import com.intfocus.template.model.response.login.RegisterResult
import com.intfocus.template.model.response.mine_page.UserInfoResult
import com.intfocus.template.subject.one.UserContract
import com.intfocus.template.subject.one.UserImpl
import com.intfocus.template.subject.one.UserPresenter
import com.intfocus.template.ui.BaseFragment
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.DisplayUtil
import com.intfocus.template.util.ImageUtil.*
import com.intfocus.template.util.PageLinkManage
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_mine_user_top.*
import kotlinx.android.synthetic.main.items_single_value.*
import kotlinx.android.synthetic.main.yh_custom_user.*
import java.io.File

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserFragment : BaseFragment(), UserContract.View {

    override lateinit var presenter: UserContract.Presenter

    lateinit var mUserInfoSP: SharedPreferences
    lateinit var mUserSP: SharedPreferences
    var rootView: View? = null
    var gson = Gson()
    var userNum: String? = null

    /* 请求识别码 */
    private val CODE_GALLERY_REQUEST = 0xa0
    private val CODE_CAMERA_REQUEST = 0xa1
    private val CODE_RESULT_REQUEST = 0xa2
    private var rl_logout_confirm: RelativeLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UserPresenter(UserImpl.getInstance(), this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user, container, false)
            presenter.loadData(ctx)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTypeFace = Typeface.createFromAsset(ctx.assets, "ALTGOT2N.TTF")
        tv_login_number.typeface = mTypeFace
        tv_report_number.typeface = mTypeFace
        tv_beyond_number.typeface = mTypeFace
        initView()
        initShow()
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    fun initView() {
        userNum = activity!!.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(USER_NUM, "")
        RetrofitUtil.getHttpService(ctx).getUserInfo(userNum)
                .compose(RetrofitUtil.CommonOptions<UserInfoResult>())
                .subscribe(object : CodeHandledSubscriber<UserInfoResult>() {
                    override fun onError(apiException: ApiException?) {
                        ToastUtils.show(ctx, apiException!!.displayMessage)
                    }

                    override fun onBusinessNext(mUserInfo: UserInfoResult?) {
                        tv_user_name.text = mUserInfo!!.data!!.user_name
                        tv_user_role.text = mUserInfo.data!!.role_name
                        tv_mine_user_num_value.text = mUserInfo.data!!.user_num
                        tv_mine_user_group_value.text = mUserInfo.data!!.group_name
                        Glide.with(ctx)
                                .load(mUserInfo.data!!.gravatar)
                                .asBitmap()
                                .placeholder(R.drawable.face_default)
                                .error(R.drawable.face_default)
                                .override(DisplayUtil.dip2px(ctx, 60f), DisplayUtil.dip2px(ctx, 60f))
                                .into(object : BitmapImageViewTarget(iv_user_icon) {
                                    override fun setResource(resource: Bitmap?) {
                                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.resources, resource)
                                        circularBitmapDrawable.isCircular = true
                                        iv_user_icon.setImageDrawable(circularBitmapDrawable)
                                    }
                                })
                    }

                    override fun onCompleted() {
                    }
                })

        iv_user_icon.setOnClickListener {
            if (ConfigConstants.HEAD_ICON_UPLOAD_SUPPORT) {
                showIconSelectPopWindow()
            }
        }
        rl_password_alter.setOnClickListener { startPassWordAlterActivity() }
        rl_issue.setOnClickListener { startIssueActivity() }
        rl_setting.setOnClickListener { startSettingActivity() }
        rl_favorite.setOnClickListener { startFavoriteActivity() }
        rl_message.setOnClickListener { startMessageActivity() }
        rl_logout.setOnClickListener { showLogoutPopupWindow() }
        rl_user_location.setOnClickListener {
            if (ConfigConstants.USER_GROUP_CONTENT) {
                startUserLocationPage()
            }
        }
    }

    private fun initShow() {
        setViewVisible(ll_single_value, ConfigConstants.USER_CUSTOM)
        setViewVisible(ll_yh_custom_user, ConfigConstants.USER_CUSTOM)
        setViewVisible(iv_mine_user_group_arrow, ConfigConstants.USER_GROUP_CONTENT)
    }

    private fun setViewVisible(view: View, show: Boolean) {
        if (show) {
            view.visibility = View.VISIBLE
        } else {
            view.visibility = View.GONE
        }
    }

    private fun refreshData() {
        RetrofitUtil.getHttpService(ctx).getUserInfo(userNum)
                .compose(RetrofitUtil.CommonOptions<UserInfoResult>())
                .subscribe(object : CodeHandledSubscriber<UserInfoResult>() {
                    override fun onError(apiException: ApiException?) {
                        ToastUtils.show(ctx, apiException!!.displayMessage)
                    }

                    override fun onBusinessNext(mUserInfo: UserInfoResult?) {
                        tv_login_number.text = mUserInfo!!.data!!.login_duration
                        tv_report_number.text = mUserInfo.data!!.browse_report_count
                        tv_beyond_number.text = mUserInfo.data!!.surpass_percentage.toString()
                    }

                    override fun onCompleted() {
                    }
                })
    }

    override fun dataLoaded(data: UserInfoResult) {
        val user = data.data
        tv_user_name.text = user!!.user_name
        tv_login_number.text = user.login_duration
        tv_report_number.text = user.browse_report_count
        tv_mine_user_num_value.text = user.user_num
        tv_beyond_number.text = user.surpass_percentage.toString()
        tv_user_role.text = user.role_name
        tv_mine_user_group_value.text = user.group_name
        Glide.with(ctx)
                .load(user.gravatar)
                .asBitmap()
                .placeholder(R.drawable.face_default)
                .error(R.drawable.face_default)
                .override(DisplayUtil.dip2px(ctx, 60f), DisplayUtil.dip2px(ctx, 60f))
                .into(object : BitmapImageViewTarget(iv_user_icon) {
                    override fun setResource(resource: Bitmap?) {
                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(ctx.resources, resource)
                        circularBitmapDrawable.isCircular = true
                        iv_user_icon.setImageDrawable(circularBitmapDrawable)
                    }
                })

    }

    private fun startPassWordAlterActivity() {
        val intent = Intent(ctx, AlterPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

        ActionLogUtil.actionLog("点击/个人信息/修改密码")
    }

    private fun startFavoriteActivity() {
        val intent = Intent(activity, FavoriteArticleActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun startMessageActivity() {
        val intent = Intent(activity, ShowPushMessageActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun startIssueActivity() {
        val intent = Intent(activity, FeedbackActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun startSettingActivity() {
        val intent = Intent(activity, SettingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun startUserLocationPage() {
        RetrofitUtil.getHttpService(ctx).getRegister("sypc_000103")
                .compose(RetrofitUtil.CommonOptions())
                .subscribe(object : CodeHandledSubscriber<RegisterResult>() {
                    override fun onError(apiException: ApiException) {
                        ToastUtils.show(ctx, "功能开发中")
                    }

                    override fun onBusinessNext(data: RegisterResult) {
                        if (data.data!!.contains("http")) {
                            PageLinkManage.pageLink(ctx!!, "归属部门", data.data!!)
                        } else {
                            ToastUtils.show(ctx, data.data!!)
                        }
                    }

                    override fun onCompleted() {}
                })
    }

    /**
     * 退出登录选择窗
     */
    private fun showLogoutPopupWindow() {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.popup_logout, null)
        //设置弹出框的宽度和高度
        val popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.animationStyle = R.style.anim_popup_bottombar
        popupWindow.showAtLocation(this.view, Gravity.BOTTOM, 0, contentView.height)

        rl_logout_confirm = contentView.findViewById(R.id.rl_logout_confirm)
        rl_logout_confirm!!.setOnClickListener {
            // 取消
            popupWindow.dismiss()
            // 确认退出
            presenter.logout(ctx)
        }
        contentView.findViewById<RelativeLayout>(R.id.rl_cancel).setOnClickListener {
            // 取消
            popupWindow.dismiss()
        }
        contentView.findViewById<RelativeLayout>(R.id.rl_popup_logout_background).setOnClickListener {
            // 点击背景半透明区域
            popupWindow.dismiss()
        }
    }

    /**
     * 退出登录成功
     */
    override fun logoutSuccess() {
        val intent = Intent(activity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        activity!!.finish()
    }

    /**
     * 吐司错误信息
     */
    override fun showErrorMsg(errorMsg: String) {
        ToastUtils.show(ctx, errorMsg)
    }

    /**
     * 吐司成功信息
     */
    override fun showSuccessMsg(msg: String) {
        ToastUtils.show(ctx, msg, ToastColor.SUCCESS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        // 用户没有选择图片，返回
        if (resultCode == RESULT_CANCELED) {
            ToastUtils.show(ctx, "取消")
            return
        }

        when (requestCode) {
            CODE_GALLERY_REQUEST -> {
                val cropIntent = launchSystemImageCrop(ctx, data!!.data)
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST)
            }
            CODE_CAMERA_REQUEST -> {
                val cropIntent: Intent
                val tempFile = File(Environment.getExternalStorageDirectory(), "icon.jpg")
                if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    val photoURI = FileProvider.getUriForFile(ctx,
                            "com.intfocus.template.fileprovider",
                            tempFile)
                    cropIntent = launchSystemImageCrop(ctx, photoURI)
                } else {
                    cropIntent = launchSystemImageCrop(ctx, Uri.fromFile(tempFile))
                }
                startActivityForResult(cropIntent, CODE_RESULT_REQUEST)
            }
            else -> if (data != null) {
                setImageToHeadView()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    /**
     * 显示头像选择菜单
     */
    private fun showIconSelectPopWindow() {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.popup_mine_icon_select, null)
        //设置弹出框的宽度和高度
        val popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        // todo  bug 验证最新bitmap 创建
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(activity!!.toolBar, Gravity.BOTTOM, 0, 0)

        contentView.findViewById<RelativeLayout>(R.id.rl_camera).setOnClickListener {
            // 打开相机
            startActivityForResult(launchCamera(ctx), CODE_CAMERA_REQUEST)
            popupWindow.dismiss()
        }
        contentView.findViewById<RelativeLayout>(R.id.rl_gallery).setOnClickListener {
            // 打开相册
            startActivityForResult(getGallery(), CODE_GALLERY_REQUEST)
            popupWindow.dismiss()
        }
        contentView.findViewById<RelativeLayout>(R.id.rl_cancel).setOnClickListener {
            // 取消
            popupWindow.dismiss()
        }
        contentView.findViewById<RelativeLayout>(R.id.rl_popup_icon_background).setOnClickListener {
            // 点击背景半透明区域
            popupWindow.dismiss()
        }
        ActionLogUtil.actionLog("点击/个人信息/设置头像")
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private fun setImageToHeadView() {
        val imgPath = Environment.getExternalStorageDirectory().toString() + "/icon.jpg"
        val bitmap = BitmapFactory.decodeFile(imgPath)
        if (bitmap != null) {
            iv_user_icon.setImageBitmap(makeRoundCorner(bitmap))
            presenter.uploadUserIcon(ctx, bitmap, imgPath)
        }
    }

}
