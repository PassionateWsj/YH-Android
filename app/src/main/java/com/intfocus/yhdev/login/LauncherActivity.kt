package com.intfocus.yhdev.login

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
import com.intfocus.yhdev.R
import com.intfocus.yhdev.screen_lock.ConfirmPassCodeActivity
import com.intfocus.yhdev.util.AssetsUpDateUtil
import com.intfocus.yhdev.util.HttpUtil
import com.intfocus.yhdev.util.LogUtil
import com.intfocus.yhdev.util.OnCheckAssetsUpdateResultListener
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class LauncherActivity : Activity(), Animation.AnimationListener {

    val ctx = this
    private lateinit var mSettingSP: SharedPreferences
    private lateinit var mUserSP: SharedPreferences
    private lateinit var mAssetsSP: SharedPreferences
    private lateinit var mAssetsSPEdit: SharedPreferences.Editor
    private lateinit var packageInfo: PackageInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        initData()
        initAnim()
    }

    private fun initData() {
        mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        mAssetsSP = getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        mAssetsSPEdit = mAssetsSP.edit()

        if (mSettingSP.getInt("Version", 0) != packageInfo.versionCode) {
            mUserSP.edit().clear().commit()
            mSettingSP.edit().clear().commit()
        }
    }

    private fun initAnim() {
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 1500
        animation.setAnimationListener(this)
        logo.startAnimation(animation)
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        number_progress_bar_splash.visibility  = View.VISIBLE
        tv_splash_status.visibility  = View.VISIBLE
        checkAssets()
    }

    override fun onAnimationStart(p0: Animation?) {
        tv_splash_status.text = "正在检测更新.."
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
                intent = Intent(this, GuideActivity::class.java)
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

    private fun checkAssets() {
        if (!HttpUtil.isConnected(ctx)) {
            tv_splash_status.text = "请检查网络"
            Timer().schedule(object : TimerTask() {
                override fun run() {
                    ctx.finish()
                }
            }, 2000)
            return
        }
        LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
        AssetsUpDateUtil.checkAssetsUpdate(ctx, number_progress_bar_splash, object : OnCheckAssetsUpdateResultListener {
            override fun onResultSuccess() {
                LogUtil.d("hjjzz", "onResultSuccess:::" + Thread.currentThread().name)
                tv_splash_status.text = "已是最新资源"
                enter()
            }

            override fun onFailure() {
                tv_splash_status.text = "更新失败"
                Timer().schedule(object : TimerTask() {
                    override fun run() {
                        ctx.finish()
                    }
                }, 2000)
            }
        })

    }
}
