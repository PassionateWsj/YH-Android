package com.intfocus.yh_android.subject.template_v2;

import android.os.Bundle;

import com.intfocus.yh_android.R;
import com.intfocus.yh_android.base.BaseModeActivity;
import com.zbl.lib.baseframe.core.inject.Main;

@Main
public class MainModeActivity extends BaseModeActivity {

    @Override
    public int setLayoutRes() {
        return R.layout.activity_main;
    }

    @Override
    public void onCreateFinish(Bundle bundle) {

    }
}
