package com.intfocus.template.subject.seven.filter;

import com.intfocus.template.model.response.scanner.StoreItem;

import java.util.List;

/**
 * ****************************************************
 *
 * @author JamesWong
 *         created on: 17/08/22 上午10:01
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */

public interface OnAttentionFilterResultListener {
    void onResultFailure(Throwable e);

    void onResultSuccess(List<StoreItem> data);
}
