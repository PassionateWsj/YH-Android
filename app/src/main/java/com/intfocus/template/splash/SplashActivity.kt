package com.intfocus.template.splash

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import com.intfocus.template.BuildConfig
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.Params.APP_HOST
import com.intfocus.template.constant.Params.SETTING_PREFERENCE
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.dashboard.DashboardActivity
import com.intfocus.template.listener.NoDoubleClickListener
import com.intfocus.template.login.LoginActivity
import com.intfocus.template.util.*
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.toast.*
import java.util.*


class SplashActivity : Activity(), Animation.AnimationListener {
    val ctx = this
    private val permissionsArray = arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION)
    private val CODE_AUTHORITY_REQUEST = 0
    /**
     * 最短点击间隔时长 ms
     */
    private val MIN_CLICK_DELAY_TIME = 2000
    /**
     * 上一次点击的时间
     */
    private var lastClickTime: Long = 0
    private lateinit var mSettingSP: SharedPreferences
    private lateinit var mUserSP: SharedPreferences
    private lateinit var packageInfo: PackageInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)
        getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(APP_HOST, BuildConfig.BASE_URL)?.let { TempHost.setHost(it) }
    }

    override fun onStart() {
        super.onStart()
        getAuthority()
    }

    /**      
     * 获取权限 : 文件读写 (WRITE_EXTERNAL_STORAGE),读取设备信息 (READ_PHONE_STATE)
     */
    private fun getAuthority() {
        val permissionsList = permissionsArray.filter { ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED }
        if (!permissionsList.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsList.toTypedArray(), CODE_AUTHORITY_REQUEST)
        } else {
            initShow()
            initData()
            initListener()
            initAnim()
        }
    }

    /**
     * 权限获取反馈
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            CODE_AUTHORITY_REQUEST -> {
                var flag = false
                if (grantResults.isNotEmpty()) {
                    for (i in permissions.indices) {
                        if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {

                        } else {
                            flag = true
                        }
                    }
                }

                if (flag) {
                    setAlertDialog(this, "某些权限获取失败，是否到本应用的设置界面设置权限")
                } else {
                    initShow()
                    initData()
                    initListener()
                    initAnim()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun setAlertDialog(context: Context, message: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("温馨提示")
                .setMessage(message)
                .setPositiveButton("确认") { _, _ -> goToAppSetting() }
                .setNegativeButton("取消") { _, _ ->
                    // 返回DashboardActivity
                }
        builder.show()
    }

    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun initShow() {
        iv_splash_adv.visibility = if (ConfigConstants.SPLASH_ADV) {
            View.VISIBLE
        } else {
            View.INVISIBLE
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        mSettingSP = getSharedPreferences(SETTING_PREFERENCE, Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE)
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
    }

    /**
     * 初始化监听
     */
    private fun initListener() {
        rl_splash_container.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                confirmNetWorkToUpdateAssets(false)
            }
        })
        rl_splash_container.isClickable = false
    }

    private fun confirmNetWorkToUpdateAssets(isDefaultStart: Boolean) {
        confirmNetWorkToUpdateAssets(isDefaultStart, "")
    }

    private fun confirmNetWorkToUpdateAssets(isDefaultStart: Boolean, errorMsg: String) {
        if (HttpUtil.isConnected(ctx)) {
            if (isDefaultStart || rl_splash_container.isClickable) {
                LogUtil.d("hjjzzsb:::isClickable", "" + rl_splash_container.isClickable)
                LogUtil.d("hjjzzsb:::isDefaultStart", "" + isDefaultStart)
                rl_splash_container.isClickable = false
                tv_splash_status.text = "正在下载报表样式文件.."
                firstUnZipAssets()
            } else {
                showUpdateAssetsErrorDetail(errorMsg)
            }
        } else {
            ToastUtils.show(this, "网络不可用")
            LogUtil.d("hjjzzsb:::isClickable", "" + rl_splash_container.isClickable)
            LogUtil.d("hjjzzsb:::isDefaultStart", "" + isDefaultStart)
            rl_splash_container.isClickable = false
            tv_splash_status.text = "正在下载报表样式文件.."
            firstUnZipAssets()
//            showUpdateAssetsErrorDetail("请检查网络")
        }
    }

    private fun showUpdateAssetsErrorDetail(errorMsg: String) {
        toast_view.visibility = View.VISIBLE
        tv_splash_status.text = "点击屏幕重试"
        rl_splash_container.isClickable = true
        toast_text.text = errorMsg
        Timer().schedule(object : TimerTask() {
            override fun run() {
                runOnUiThread {
                    toast_view.visibility = View.GONE
                }
            }
        }, 2000)
    }

    /**
     * 初始化动画
     */
    private fun initAnim() {
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 1500
        animation.setAnimationListener(this)
        logo.startAnimation(animation)
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        if (ConfigConstants.UP_DATE_ASSETS) {
            number_progress_bar_splash.visibility = View.VISIBLE
            tv_splash_status.visibility = View.VISIBLE
            confirmNetWorkToUpdateAssets(true)
        } else {
            enter()
        }
    }

    override fun onAnimationStart(p0: Animation?) {
        if (ConfigConstants.UP_DATE_ASSETS) {
            tv_splash_status.text = "正在下载报表样式文件.."
        }
    }

    private fun enter() {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        when {
            mSettingSP.getBoolean("ScreenLock", false) -> {
                intent = Intent(this, ConfirmPassCodeActivity::class.java)
                intent.putExtra("is_from_login", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

                finish()
            }
            mSettingSP.getInt("Version", 0) != packageInfo.versionCode -> {
                intent = if (ConfigConstants.GUIDE_SHOW) {
                    Intent(this, GuideActivity::class.java)
                } else {
                    Intent(this, LoginActivity::class.java)
                }
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

                finish()
            }
            else -> {

                if (ConfigConstants.LOGIN_WITH_LAST_USER && mUserSP.getBoolean(Params.IS_LOGIN, false) && mSettingSP.getBoolean(Params.AUTO_LOGIN, false)) {
                    val pageLinkManagerSP = this@SplashActivity.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
                    val pageSaved = pageLinkManagerSP.getBoolean("pageSaved", false)
                    if (ConfigConstants.REVIEW_LAST_PAGE && pageSaved) {
                        val objTitle = pageLinkManagerSP.getString("objTitle", "")
                        val link = pageLinkManagerSP.getString("link", "")
                        val objectId = pageLinkManagerSP.getString("objectId", "-1")
                        val templateId = pageLinkManagerSP.getString("templateId", "-1")
                        val objectType = pageLinkManagerSP.getString("objectType", "-1")
                        PageLinkManage.pageLink(this@SplashActivity,
                                objTitle ?: "", link ?: "",
                                objectId ?: "-1", templateId ?: "-1", objectType ?: "-1")
                    } else {
                        intent = Intent(this, DashboardActivity::class.java)
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        this.startActivity(intent)
                    }
                } else {
                    intent = Intent(this, LoginActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                    this.startActivity(intent)
                }
                finish()
            }
        }
    }

    private fun firstUnZipAssets() {
        if (mSettingSP.getInt("Version", 0) == 0) {

            AssetsUpDateUtil.checkFirstSetup(ctx, object : OnCheckAssetsUpdateResultListener {
                override fun onResultSuccess() {
                    checkAssets()
                }

                override fun onFailure(errorMsg: Throwable) {
                    LogUtil.d("hjjzzsb", "更新失败:::" + errorMsg.message)
                    AssetsUpDateUtil.unSubscribe()
                    number_progress_bar_splash.progress = 0
                    tv_splash_status.text = "更新失败"
                    confirmNetWorkToUpdateAssets(false, errorMsg.message!!)
                }
            })
        } else {
            if (HttpUtil.isConnected(ctx)) {
                checkAssets()
            } else {
                number_progress_bar_splash.progress += 90
                tv_splash_status.text = "离线资源加载完成"
                enter()
            }
        }
    }

    private fun checkAssets() {
        LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
        AssetsUpDateUtil.checkAssetsUpdate(ctx, number_progress_bar_splash, object : OnCheckAssetsUpdateResultListener {
            override fun onResultSuccess() {
                LogUtil.d("hjjzz", "onResultSuccess:::" + Thread.currentThread().name)
                if (number_progress_bar_splash.progress == 80) {
                    number_progress_bar_splash.progress += 20
                    tv_splash_status.text = "已是最新资源"
                    enter()
                } else {
                    number_progress_bar_splash.progress = 0
                    tv_splash_status.text = "更新失败"
                }
            }

            override fun onFailure(errorMsg: Throwable) {
                LogUtil.d("hjjzzsb", "更新失败")
                AssetsUpDateUtil.unSubscribe()
                number_progress_bar_splash.progress = 0
                tv_splash_status.text = "更新失败"
                confirmNetWorkToUpdateAssets(false, errorMsg.message ?: "资源更新出错")
            }
        })
    }

    override fun onBackPressed() {
        val toast = Toast.makeText(ctx, "再按一次退出应用", Toast.LENGTH_SHORT)
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
            lastClickTime = currentTime
            toast.show()
            return
        }
        toast.cancel()
        finish()

    }

    override fun onDestroy() {
        if (!ConfigConstants.GUIDE_SHOW) {
            val packageInfo = packageManager.getPackageInfo(packageName, 0)
            mSettingSP.edit().putInt("Version", packageInfo.versionCode).apply()
        }
        AssetsUpDateUtil.unSubscribe()
        super.onDestroy()
    }
}
