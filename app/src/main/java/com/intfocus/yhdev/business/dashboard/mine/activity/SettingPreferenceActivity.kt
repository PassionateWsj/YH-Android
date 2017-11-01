package com.intfocus.yhdev.business.dashboard.mine.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.intfocus.yhdev.R
import com.intfocus.yhdev.general.base.BaseActivity
import com.intfocus.yhdev.general.constant.ToastColor
import com.intfocus.yhdev.general.util.*
import kotlinx.android.synthetic.main.activity_setting_preference.*
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by liuruilin on 2017/3/28.
 */

class SettingPreferenceActivity : BaseActivity() {
    private var mSharedPreferences: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_preference)

        initSwitchPreference()
        initListener()
    }

    override fun onResume() {
        super.onResume()
        switch_screenLock!!.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
    }

    /*
     * Switch 状态初始化
     */
    private fun initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        switch_screenLock.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
        switch_report_copy.isChecked = mSharedPreferences!!.getBoolean("ReportCopy", false)
        switch_landscape_banner.isChecked = mSharedPreferences!!.getBoolean("Landscape", false)
    }

    private fun initListener() {
        switch_screenLock.setOnCheckedChangeListener(mSwitchScreenLockListener)
        switch_report_copy.setOnCheckedChangeListener(mSwitchReportCopyListener)
    }

    /*
     *  Switch ScreenLock 开关
     */
    private val mSwitchScreenLockListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        if (!buttonView.isPressed) {
            return@OnCheckedChangeListener
        }
        if (isChecked) {
            val intent = Intent(this@SettingPreferenceActivity, InitPassCodeActivity::class.java)
            startActivity(intent)
        } else {
            mSharedPreferences!!.edit().putBoolean("ScreenLock", isChecked).apply()
        }
    }

    /*
     *  Switch Report Copy 开关
     */
    private val mSwitchReportCopyListener = CompoundButton.OnCheckedChangeListener { _, isChecked -> mSharedPreferences!!.edit().putBoolean("ReportCopy", isChecked).apply() }

    /*
     * 清理缓存
     */
    fun clearUserCache(v: View) {
        val mProgressDialog = ProgressDialog.show(this@SettingPreferenceActivity, "稍等", "正在清理缓存...")
        if (!HttpUtil.isConnected(this))
            return

        val sharedPath = FileUtil.sharedPath(this)
        val cachePath = String.format("%s/%s", FileUtil.basePath(this), K.kCachedDirName)
        Observable.just(sharedPath)
                .subscribeOn(Schedulers.io())
                .map { path ->
                    val isClearSpSuccess = getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE).edit().clear().commit()
                    val isCleanSharedPathSuccess = FileUtil.deleteDirectory(path)
                    val isCleanCacheSuccess = FileUtil.deleteDirectory(cachePath)
                    isClearSpSuccess && isCleanSharedPathSuccess && isCleanCacheSuccess
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isClear ->
                    if (isClear) {
                        AssetsUpDateUtil.checkFirstSetup(this, object : OnCheckAssetsUpdateResultListener {
                            override fun onResultSuccess() {
                                AssetsUpDateUtil.checkAssetsUpdate(this@SettingPreferenceActivity, object : OnCheckAssetsUpdateResultListener {
                                    override fun onResultSuccess() {
                                        ToastUtils.show(applicationContext, "清除缓存成功", ToastColor.SUCCESS)
                                        mProgressDialog.dismiss()
                                    }

                                    override fun onFailure() {
                                        ToastUtils.show(applicationContext, "清除缓存失败，请重试")
                                        mProgressDialog.dismiss()
                                    }
                                })
                            }

                            override fun onFailure() {
                            }
                        })

                    } else {
                        mProgressDialog.dismiss()
                        ToastUtils.show(applicationContext, "清除缓存失败，请重试")
                    }
                }


    }
}
