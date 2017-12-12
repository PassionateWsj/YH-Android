package com.intfocus.template.dashboard.mine.model;

import com.intfocus.template.model.entity.PushMsgBean;

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
    void onPushMessageDataResultSuccess(List<PushMsgBean> data);

    void onPushMessageDataResultFailure();
}