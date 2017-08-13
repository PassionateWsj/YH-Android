package com.intfocus.yonghuitest.setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.screen_lock.InitPassCodeActivity;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.ToastUtils;

/**
 * Created by liuruilin on 2017/3/28.
 */

public class SettingPreferenceActivity extends BaseActivity {
    private Switch mScreenLockSwitch, mReportCopySwitch, mLandscapeBannerSwitch;
    private SharedPreferences mSharedPreferences;
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_preference);

        mContext = this;

        mScreenLockSwitch = (Switch) findViewById(R.id.switch_screenLock);
        mReportCopySwitch = (Switch) findViewById(R.id.switch_report_copy);
        mLandscapeBannerSwitch = (Switch) findViewById(R.id.switch_landscape_banner);

        mScreenLockSwitch.setOnCheckedChangeListener(mSwitchScreenLockListener);
        mReportCopySwitch.setOnCheckedChangeListener(mSwitchReportCopyListener);
        mLandscapeBannerSwitch.setOnCheckedChangeListener(mSwitchBannerListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initSwitchPreference();
    }

    /*
     * Switch 状态初始化
     */
    private void initSwitchPreference() {
        mSharedPreferences = getSharedPreferences("SettingPreference", MODE_PRIVATE);
        mScreenLockSwitch.setChecked(mSharedPreferences.getBoolean("ScreenLock", false));
        mReportCopySwitch.setChecked(mSharedPreferences.getBoolean("ReportCopy", false));
        mLandscapeBannerSwitch.setChecked(mSharedPreferences.getBoolean("Landscape", false));
    }

    /*
     *  Switch ScreenLock 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchScreenLockListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (!buttonView.isPressed()) {
                return;
            }
            if (isChecked) {
                Intent intent = new Intent(SettingPreferenceActivity.this, InitPassCodeActivity.class);
                startActivity(intent);
            } else {
                mSharedPreferences.edit().putBoolean("ScreenLock", isChecked).commit();
            }
        }
    };

    /*
     *  Switch LandScape Banner 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchBannerListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSharedPreferences.edit().putBoolean("Landscape", isChecked).commit();
        }
    };


    /*
     *  Switch Report Copy 开关
     */
    private final CompoundButton.OnCheckedChangeListener mSwitchReportCopyListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            mSharedPreferences.edit().putBoolean("ReportCopy", isChecked).commit();
        }
    };

    /*
     * 清理缓存
     */
    public void clearUserCache(View v) {
        new FileUtil.CacheCleanAsync(mAppContext, "cache-clean").execute();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                ToastUtils.INSTANCE.show(mContext, "缓存已清理", R.color.co1_syr);
            }
        }, 3000);
    }
}
