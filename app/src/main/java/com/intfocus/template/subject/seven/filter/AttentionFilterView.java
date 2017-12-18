package com.intfocus.template.subject.seven.filter;

import com.intfocus.template.model.response.scanner.StoreItem;

import java.util.List;

/**
 * ****************************************************
 *
 * @author JamesWong
 *         created on: 17/08/01 下午4:51
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */
public interface AttentionFilterView {
    /**
     * 读取搜索结果失败
     *
     * @param e 失败信息
     */
    void onResultFailure(Throwable e);

    /**
     * 读取搜索结果成功
     *
     * @param data 含关键字的门店
     */
    void onResultSuccess(List<StoreItem> data);
}
