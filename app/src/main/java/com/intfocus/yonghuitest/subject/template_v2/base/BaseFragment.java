package com.intfocus.yonghuitest.subject.template_v2.base;

import android.app.Activity;
import android.content.Context;

import com.zbl.lib.baseframe.core.AbstractFragment;
import com.zbl.lib.baseframe.core.Subject;

public abstract class BaseFragment<Target extends Subject> extends AbstractFragment<Target> {
    public Activity act;
    public Context ctx;

    @Override
    public void onAttach(Context context) {
        ctx = context.getApplicationContext();
        act = (Activity) context;
        super.onAttach(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

/*    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        getActivity().overridePendingTransition(R.anim.activity_hold, R.anim.activity_fade);


    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        getActivity().overridePendingTransition(R.anim.activity_hold, R.anim.activity_fade);
    }*/
}
