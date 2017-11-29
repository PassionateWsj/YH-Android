package com.intfocus.template.dashboard.mine.widget

//import org.xutils.image.ImageOptions
import android.app.Activity.RESULT_CANCELED
import android.content.Context
import android.content.Context.MODE_PRIVATE
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
import com.intfocus.template.R
import com.intfocus.template.dashboard.mine.activity.*
import com.intfocus.template.dashboard.mine.bean.UserInfoRequest
import com.intfocus.template.login.LoginActivity
import com.intfocus.template.ui.BaseModeFragment
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.ACTION
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.IS_LOGIN
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.model.response.login.RegisterResult
import com.intfocus.template.model.response.mine_page.UserInfoResult
import com.intfocus.template.dashboard.UserInfoMode
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.subject.two.WebPageActivity
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.DisplayUtil
import com.intfocus.template.util.ImageUtil.*
import com.intfocus.template.util.K.K_USER_DEVICE_ID
import com.intfocus.template.util.ToastUtils
import com.taobao.accs.utl.UtilityImpl.isNetworkConnected
import com.zbl.lib.baseframe.core.Subject
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.fragment_user.*
import kotlinx.android.synthetic.main.item_mine_user_top.*
import kotlinx.android.synthetic.main.items_single_value.*
import kotlinx.android.synthetic.main.yh_custom_user.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONObject
import java.io.File

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserFragment : BaseModeFragment<UserInfoMode>() {

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

    override fun setSubject(): Subject {
        mUserInfoSP = ctx.getSharedPreferences("UserInfo", Context.MODE_PRIVATE)
        mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        return UserInfoMode(ctx)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        EventBus.getDefault().register(this)
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_user, container, false)
            model.requestData()
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mTypeFace = Typeface.createFromAsset(act.assets, "ALTGOT2N.TTF")
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
                                        val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context!!.resources, resource)
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
                showIconSelectPopWindow(this.context!!)
            }
        }
        rl_password_alter.setOnClickListener { startPassWordAlterActivity() }
        rl_issue.setOnClickListener { startIssueActivity() }
        rl_setting.setOnClickListener { startSettingActivity() }
        rl_favorite.setOnClickListener { startFavoriteActivity() }
        rl_message.setOnClickListener { startMessageActivity() }
        rl_logout.setOnClickListener { showLogoutPopupWindow(this.context!!) }
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun setData(result: UserInfoRequest) {
        if (result.isSuccess && result.userInfoBean != null) {
            val user = result.userInfoBean
            tv_user_name.text = user!!.user_name
            tv_login_number.text = user.login_duration
            tv_report_number.text = user.browse_report_count
            tv_mine_user_num_value.text = user.user_num
            tv_beyond_number.text = user.surpass_percentage.toString()
            tv_user_role.text = user.role_name
            tv_mine_user_group_value.text = user.group_name
//            x.image().bind(iv_user_icon, user.gravatar, imageOptions)
            Glide.with(ctx)
                    .load(user.gravatar)
                    .asBitmap()
                    .placeholder(R.drawable.face_default)
                    .error(R.drawable.face_default)
                    .override(DisplayUtil.dip2px(ctx, 60f), DisplayUtil.dip2px(ctx, 60f))
                    .into(object : BitmapImageViewTarget(iv_user_icon) {
                        override fun setResource(resource: Bitmap?) {
                            val circularBitmapDrawable = RoundedBitmapDrawableFactory.create(context!!.resources, resource)
                            circularBitmapDrawable.isCircular = true
                            iv_user_icon.setImageDrawable(circularBitmapDrawable)
                        }
                    })

        }
    }

    private fun startPassWordAlterActivity() {
        val intent = Intent(activity, AlterPasswordActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)

        val logParams = JSONObject()
        logParams.put(ACTION, "点击/个人信息/修改密码")
        ActionLogUtil.actionLog(ctx, logParams)
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
                            val intent = Intent(activity, WebPageActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(BANNER_NAME, "归属部门")
                            intent.putExtra(LINK, data.data)
                            intent.putExtra(OBJECT_ID, "-1")
                            intent.putExtra(OBJECT_TYPE, "-1")
                            intent.putExtra(TEMPLATE_ID, "-1")
                            startActivity(intent)
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
    private fun showLogoutPopupWindow(ctx: Context) {
        val contentView = LayoutInflater.from(ctx).inflate(R.layout.popup_logout, null)
        //设置弹出框的宽度和高度
        val popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(activity!!.toolBar, Gravity.BOTTOM, 0, 0)
        popupWindow.animationStyle = R.anim.popup_bottombar_in
        rl_logout_confirm = contentView.findViewById(R.id.rl_logout_confirm)
        rl_logout_confirm!!.setOnClickListener {
            // 取消
            popupWindow.dismiss()
            // 确认退出
            logout()
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
     * 退出登录
     */
    private fun logout() {
        // 判断有无网络
        if (!isNetworkConnected(ctx)) {
            ToastUtils.show(ctx, "未连接网络, 无法退出")
            return
        }
        val mEditor = act.getSharedPreferences("SettingPreference", MODE_PRIVATE).edit()
        mEditor.putBoolean("ScreenLock", false).apply()
        // 退出登录 POST 请求
        RetrofitUtil.getHttpService(ctx).userLogout(mUserSP.getString(K_USER_DEVICE_ID, "0"))
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onBusinessNext(data: BaseResult?) {
                        if (data!!.code == "200") {
                            mUserSP.edit().putBoolean(IS_LOGIN, false).apply()

                            val logParams = JSONObject()
                            logParams.put(ACTION, "退出登录")
                            ActionLogUtil.actionLog(ctx, logParams)

                            model.modifiedUserConfig(false)
                            val intent = Intent()
                            intent.setClass(activity, LoginActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(intent)
                            activity!!.finish()
                        } else {
                            ToastUtils.show(ctx, data.message!!)
                        }
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException?) {
                        ToastUtils.show(ctx, apiException!!.message!!)
                    }

                })
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
    private fun showIconSelectPopWindow(ctx: Context) {
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
            startActivityForResult(launchCamera(context), CODE_CAMERA_REQUEST)
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

        val logParams = JSONObject()
        logParams.put(ACTION, "点击/个人信息/设置头像")
        ActionLogUtil.actionLog(ctx, logParams)
    }

    /**
     * 提取保存裁剪之后的图片数据，并设置头像部分的View
     */
    private fun setImageToHeadView() {
        val imgPath = Environment.getExternalStorageDirectory().toString() + "/icon.jpg"
        val bitmap = BitmapFactory.decodeFile(imgPath)
        if (bitmap != null) {
            iv_user_icon.setImageBitmap(makeRoundCorner(bitmap))
            model.uplodeUserIcon(bitmap, imgPath)
        }
    }
}
