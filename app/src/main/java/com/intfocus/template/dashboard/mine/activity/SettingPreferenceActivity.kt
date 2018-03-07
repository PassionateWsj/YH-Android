package com.intfocus.template.dashboard.mine.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import com.blankj.utilcode.util.BarUtils
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params.AUTO_LOGIN
import com.intfocus.template.constant.Params.KEEP_PWD
import com.intfocus.template.constant.Params.REVIEW_LAST_PAGE
import com.intfocus.template.constant.Params.SCREEN_LOCK
import com.intfocus.template.constant.Params.SETTING_PREFERENCE
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

        initShow()
        initSwitchPreference()
        initListener()
    }


    private fun initShow() {
        if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
            action_bar.post { BarUtils.addMarginTopEqualStatusBarHeight(action_bar) }
        }
        if (ConfigConstants.LOGIN_WITH_LAST_USER) {
            rl_auto_login.visibility = View.VISIBLE
        }
        if (ConfigConstants.REVIEW_LAST_PAGE) {
            rl_review_last_page.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        switch_screenLock!!.isChecked = mSharedPreferences!!.getBoolean(SCREEN_LOCK, false)
    }

    /**
     * Switch 状态初始化
     */
    private fun initSwitchPreference() {
        mSharedPreferences = getSharedPreferences(SETTING_PREFERENCE, Context.MODE_PRIVATE)
        switch_screenLock.isChecked = mSharedPreferences!!.getBoolean(SCREEN_LOCK, false)
        switch_keep_pwd.isChecked = mSharedPreferences!!.getBoolean(KEEP_PWD, false)
        switch_report_copy.isChecked = mSharedPreferences!!.getBoolean("ReportCopy", false)
        switch_landscape_banner.isChecked = mSharedPreferences!!.getBoolean("Landscape", false)
        switch_auto_login!!.isChecked = mSharedPreferences!!.getBoolean(AUTO_LOGIN, false)
        switch_review_last_page!!.isChecked = mSharedPreferences!!.getBoolean(REVIEW_LAST_PAGE, false)
    }

    private fun initListener() {
        switch_screenLock.setOnCheckedChangeListener(mSwitchChangeListener)
        switch_keep_pwd.setOnCheckedChangeListener(mSwitchChangeListener)
        switch_report_copy.setOnCheckedChangeListener(mSwitchChangeListener)
        switch_auto_login.setOnCheckedChangeListener(mSwitchChangeListener)
        switch_review_last_page.setOnCheckedChangeListener(mSwitchChangeListener)
    }

    /**
     *
     */
    private val mSwitchChangeListener = CompoundButton.OnCheckedChangeListener { buttonView, isChecked ->
        when (buttonView.id) {
            // Switch ScreenLock 开关
            R.id.switch_screenLock -> {
                if (!buttonView.isPressed) {
                    return@OnCheckedChangeListener
                }
                if (isChecked) {
                    val intent = Intent(this@SettingPreferenceActivity, InitPassCodeActivity::class.java)
                    startActivity(intent)
                } else {
                    mSharedPreferences!!.edit().putBoolean(SCREEN_LOCK, isChecked).apply()
                }
            }
            //  Switch Keep Password 开关
            R.id.switch_keep_pwd -> {
                mSharedPreferences!!.edit().putBoolean(KEEP_PWD, isChecked).apply()
            }
            //  Switch Report Copy 开关
            R.id.switch_report_copy -> {
                mSharedPreferences!!.edit().putBoolean("ReportCopy", isChecked).apply()
            }
            R.id.switch_auto_login -> {
                mSharedPreferences!!.edit().putBoolean(AUTO_LOGIN, isChecked).apply()
            }
            R.id.switch_review_last_page -> {
                mSharedPreferences!!.edit().putBoolean(REVIEW_LAST_PAGE, isChecked).apply()
            }

        }

    }

    /**
     *  清理缓存
     */
    fun clearUserCache(view: View) {
        CacheCleanManager.clearAppUserCache(this)
    }
}
