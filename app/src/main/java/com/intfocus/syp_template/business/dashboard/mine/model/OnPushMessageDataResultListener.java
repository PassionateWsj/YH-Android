package com.intfocus.syp_template.business.dashboard.mine.model;

import com.intfocus.syp_template.business.dashboard.mine.bean.PushMessageBean;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:58
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public interface OnPushMessageDataResultListener {
    void onPushMessageDataResultSuccess(List<PushMessageBean> data);

    void onPushMessageDataResultFailure();
}
