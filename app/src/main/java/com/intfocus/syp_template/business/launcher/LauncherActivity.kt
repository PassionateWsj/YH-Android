package com.intfocus.syp_template.business.launcher

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import com.intfocus.syp_template.R
import com.intfocus.syp_template.business.login.LoginActivity
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.listen.NoDoubleClickListener
import com.intfocus.syp_template.general.util.AssetsUpDateUtil
import com.intfocus.syp_template.general.util.HttpUtil
import com.intfocus.syp_template.general.util.LogUtil
import com.intfocus.syp_template.general.util.OnCheckAssetsUpdateResultListener
import kotlinx.android.synthetic.main.activity_splash.*
import kotlinx.android.synthetic.main.toast.*
import java.util.*


class LauncherActivity : Activity(), Animation.AnimationListener {

    val ctx = this
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
    //    private lateinit var mAssetsSP: SharedPreferences
//    private lateinit var mAssetsSPEdit: SharedPreferences.Editor
    private lateinit var packageInfo: PackageInfo


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        initShow()
        initData()
        initListener()
        initAnim()
    }

    private fun initShow() {
        iv_splash_adv.visibility = if (ConfigConstants.SPLASH_ADV) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * 初始化数据
     */
    private fun initData() {
        mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
//        mAssetsSP = getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
//        mAssetsSPEdit = mAssetsSP.edit()

        if (mSettingSP.getInt("Version", 0) != packageInfo.versionCode) {
            mUserSP.edit().clear().apply()
            mSettingSP.edit().clear().apply()
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
        if (HttpUtil.isConnected(ctx) && (isDefaultStart || rl_splash_container.isClickable)) {
            LogUtil.d("sadfafsdafsd", "adfsafdsafdsdafssfdfa")
            LogUtil.d("sadfafsdafsd:::isClickable", "" + rl_splash_container.isClickable)
            LogUtil.d("sadfafsdafsd:::isDefaultStart", "" + isDefaultStart)
            rl_splash_container.isClickable = false
            tv_splash_status.text = "正在下载报表样式文件.."
            firstUnZipAssets()
        } else {
            rl_splash_container.isClickable = true
            toast_view.visibility = View.VISIBLE
            toast_text.text = "请检查网络"
            tv_splash_status.text = "点击屏幕重试"
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    runOnUiThread {
                        toast_view.visibility = View.GONE
                    }
                }
            }, 2000)
        }
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

                intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

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

                override fun onFailure() {
                }
            })
        } else {
            checkAssets()
        }
    }

    private fun checkAssets() {
        LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
        AssetsUpDateUtil.checkAssetsUpdate(ctx, number_progress_bar_splash, object : OnCheckAssetsUpdateResultListener {
            override fun onResultSuccess() {
                LogUtil.d("hjjzz", "onResultSuccess:::" + Thread.currentThread().name)
                tv_splash_status.text = "已是最新资源"
                enter()
            }

            override fun onFailure() {
                number_progress_bar_splash.progress = 0
                tv_splash_status.text = "更新失败"
                Thread.sleep(2000)
                confirmNetWorkToUpdateAssets(false)
            }
        })
    }

    override fun onBackPressed() {
        val toast = Toast.makeText(ctx, "再按一次退出生意人", Toast.LENGTH_SHORT)
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
        AssetsUpDateUtil.unSubscribe()
        super.onDestroy()
    }
}
