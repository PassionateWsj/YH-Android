package com.intfocus.template.dashboard.mine.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.intfocus.template.R
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.util.CacheCleanManager
import kotlinx.android.synthetic.main.activity_setting_preference.*

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

    /**
     * Switch 状态初始化
     */
    private fun initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        switch_screenLock.isChecked = mSharedPreferences!!.getBoolean("ScreenLock", false)
        switch_keep_pwd.isChecked = mSharedPreferences!!.getBoolean("keep_pwd", false)
        switch_report_copy.isChecked = mSharedPreferences!!.getBoolean("ReportCopy", false)
        switch_landscape_banner.isChecked = mSharedPreferences!!.getBoolean("Landscape", false)
    }

    private fun initListener() {
        switch_screenLock.setOnCheckedChangeListener(mSwitchScreenLockListener)
        switch_keep_pwd.setOnCheckedChangeListener(mSwitchKeepPwdListener)
        switch_report_copy.setOnCheckedChangeListener(mSwitchReportCopyListener)
    }

    /**
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
    /**
     *  Switch Keep Password 开关
     */
    private val mSwitchKeepPwdListener = CompoundButton.OnCheckedChangeListener { _, isChecked -> mSharedPreferences!!.edit().putBoolean("keep_pwd", isChecked).apply() }

    /**
     *  Switch Report Copy 开关
     */
    private val mSwitchReportCopyListener = CompoundButton.OnCheckedChangeListener { _, isChecked -> mSharedPreferences!!.edit().putBoolean("ReportCopy", isChecked).apply() }

    /**
     *  清理缓存
     */
    fun clearUserCache(view: View) {
        CacheCleanManager.clearAppUserCache(this)
    }
}
