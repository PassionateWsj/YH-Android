package com.intfocus.yhdev.login.listener

import com.intfocus.yhdev.data.response.update.UpdateResult

/**
 * ****************************************************
 * author: jameswong
 * created on: 17/08/31 下午4:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface OnUpdateResultListener {
    fun onResultSuccess(data: UpdateResult.UpdateData)
    fun onFailure(msg: String)
}