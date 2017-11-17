package com.intfocus.syp_template.business.scanner.view;

import com.intfocus.syp_template.general.data.response.scanner.StoreItem;

import java.util.List;

/**
 * ****************************************************
 * @author JamesWong
 * created on: 17/08/01 下午4:51
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public interface StoreSelectorView {
    /**
     * 读取搜索结果失败
     * @param e 失败信息
     */
    void onResultFailure(Throwable e);

    /**
     * 读取搜索结果成功
     * @param data 含关键字的门店
     */
    void onResultSuccess(List<StoreItem> data);
}
