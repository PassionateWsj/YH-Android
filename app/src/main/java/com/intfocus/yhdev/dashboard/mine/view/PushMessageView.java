package com.intfocus.yhdev.dashboard.mine.view;

import com.intfocus.yhdev.dashboard.mine.bean.PushMessageBean;

import java.util.List;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:51
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public interface PushMessageView {
    void onResultFailure();
    void onResultSuccess(List<PushMessageBean> data);
}
