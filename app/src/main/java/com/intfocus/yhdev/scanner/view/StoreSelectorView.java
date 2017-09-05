package com.intfocus.yhdev.scanner.view;

import com.intfocus.yhdev.data.response.scanner.StoreItem;

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

public interface StoreSelectorView {
    void onResultFailure(Throwable e);
    void onResultSuccess(List<StoreItem> data);
}
