package com.intfocus.template.ui;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * @author liuruilin
 * @date 2017/5/8
 */

public abstract class BaseFragment extends Fragment {
    public Context ctx;

    @Override
    public void onAttach(Context context) {
        ctx = context;
        super.onAttach(context);
    }

}
