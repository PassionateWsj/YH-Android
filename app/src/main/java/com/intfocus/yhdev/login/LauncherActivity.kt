package com.intfocus.yhdev.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Toast
import com.intfocus.yhdev.R
import com.intfocus.yhdev.data.response.update.UpdateResult
import com.intfocus.yhdev.login.listener.DownLoadProgressListener
import com.intfocus.yhdev.login.listener.OnCheckAssetsUpdateResultListener
import com.intfocus.yhdev.login.listener.OnUpdateResultListener
import com.intfocus.yhdev.screen_lock.ConfirmPassCodeActivity
import com.intfocus.yhdev.util.LogUtil
import com.intfocus.yhdev.util.UpDateUtil
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class LauncherActivity : AppCompatActivity(), Animation.AnimationListener {
    val ctx = this
    /**
     * 最短点击间隔时长 ms
     */
    private val MIN_CLICK_DELAY_TIME = 2000
    /**
     * 上一次点击的时间
     */
    private var lastClickTime: Long = 0
    private lateinit var toast: Toast
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

        toast = Toast.makeText(ctx, "再按一次退出生意人", Toast.LENGTH_SHORT)
    }

    private fun initAnim() {
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 1500
        animation.setAnimationListener(this)
        logo.startAnimation(animation)
    }

    /**
     * 检测更新
     */
    private fun checkUpdate() {
        UpDateUtil.checkUpdate(ctx, packageInfo.versionCode, packageInfo.versionName, object : OnUpdateResultListener {
            override fun onResultSuccess(data: UpdateResult.UpdateData) {
                when (data.upgrade_level) {
                    -1 -> {
                        number_progress_bar_splash.visibility = View.VISIBLE
                        tv_splash_status.visibility = View.VISIBLE
                        tv_splash_status.text = "已是最新版本"
                        unZipAssetsFromLocal(data.assets)
                    }
                    1 -> {
                        number_progress_bar_splash.visibility = View.VISIBLE
                        tv_splash_status.visibility = View.VISIBLE
                        tv_splash_status.text = "未发现新版本"
                        unZipAssetsFromLocal(data.assets)
                    }
                    2, 3 -> {
                        tv_splash_status.visibility = View.VISIBLE
                        tv_splash_status.text = "发现新版本"
                        showUpDateDialog(data)
                    }
                    else -> {
                        tv_splash_status.visibility = View.VISIBLE
                        tv_splash_status.text = "更新失败"
                        finishIn2Minutes()
                    }
                }
            }

            override fun onFailure(msg: String) {
                number_progress_bar_splash.visibility = View.VISIBLE
                tv_splash_status.visibility = View.VISIBLE
                tv_splash_status.text = msg
                finishIn2Minutes()
            }

        })
    }

    private fun showUpDateDialog(data: UpdateResult.UpdateData) {
        val dialog = AlertDialog.Builder(ctx)
                .setTitle("发现新版本 " + data.version)
                .setMessage(data.description)
                .setPositiveButton("更新") { dialog, _ ->
                    if (data.upgrade_level == 2) {
                        UpDateUtil.downAPKInBackground(ctx, data.download_url, ctx.resources.getString(R.string.app_name))
//                        dialog.dismiss()
                        tv_splash_status.text = "已在后台下载新版本应用.."
                        LogUtil.d("hjjzz", "mainThread:::" + Thread.currentThread().name)
                        Handler().postDelayed({
                            unZipAssetsFromLocal(data.assets)
                        }, 2000)
                    } else {
                        number_progress_bar_splash.visibility = View.VISIBLE
                        UpDateUtil.downAPKInUI(ctx, data.download_url, ctx.resources.getString(R.string.app_name), number_progress_bar_splash)
                        dialog.dismiss()
                    }
                }
        if (data.upgrade_level == 2)
            dialog.setNegativeButton("暂不更新") { dialog, _ ->
                number_progress_bar_splash.visibility = View.VISIBLE
                unZipAssetsFromLocal(data.assets)
//                dialog.dismiss()
            }
        dialog.setCancelable(false)
        dialog.create().show()
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        checkUpdate()
    }

    override fun onAnimationStart(p0: Animation?) {

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
                val mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
                val packageInfo = packageManager.getPackageInfo(packageName, 0)
                mSharedPreferences!!.edit().putInt("Version", packageInfo.versionCode).commit()

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

    private fun unZipAssetsFromLocal(assets: List<UpdateResult.UpdateData.AssetsBean>?) {
        number_progress_bar_splash.progress = 10
        tv_splash_status.text = "正在检测样式更新.."
        if (mSettingSP.getInt("Version", 0) == 0) {
            UpDateUtil.checkFirstSetup(ctx, object : OnCheckAssetsUpdateResultListener {
                override fun onResultSuccess() {
                    checkAssets(assets)
                }

                override fun onFailure(msg: String) {
                }
            })
        } else {
            checkAssets(assets)
        }
    }

    /**
     * 检测静态资源更新
     */
    private fun checkAssets(assets: List<UpdateResult.UpdateData.AssetsBean>?) {
        UpDateUtil.checkAssetsUpdate(ctx, assets!!, number_progress_bar_splash, object : OnCheckAssetsUpdateResultListener {
            override fun onResultSuccess() {
                tv_splash_status.text = "已是最新资源"
                enter()
            }

            override fun onFailure(msg: String) {
                tv_splash_status.text = msg
                finishIn2Minutes()
            }
        }, object : DownLoadProgressListener {
            override fun updateProgress(percent: Long) {
                if (number_progress_bar_splash != null)
                    number_progress_bar_splash.progress += (percent * 0.1).toInt()
            }
        })
    }

    fun finishIn2Minutes() {
        Timer().schedule(object : TimerTask() {
            override fun run() {
                ctx.finish()
            }
        }, 2000)
    }

    override fun onBackPressed() {
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
        UpDateUtil.unSubscribe()
        super.onDestroy()
    }
}
