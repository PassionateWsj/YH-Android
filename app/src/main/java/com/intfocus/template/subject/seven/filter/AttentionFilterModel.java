package com.intfocus.template.subject.seven.filter;

import android.content.Context;


/**
 * ****************************************************
 *
 * @author JamesWong
 *         created on: 17/08/22 上午10:00
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */

interface AttentionFilterModel {
    void loadData(Context mContext, String keyWord, OnAttentionFilterResultListener listener);
}
