package com.intfocus.template.dashboard.mine.view;

import com.intfocus.template.model.entity.PushMsgBean;

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

    void onResultSuccess(List<PushMsgBean> data);
}
