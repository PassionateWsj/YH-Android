package com.intfocus.shengyiplus.subject.one

import android.content.Context
import android.graphics.Bitmap
import com.intfocus.shengyiplus.model.response.mine_page.UserInfoResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:09
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface UserModel {
    interface LoadDataCallback {
        fun onDataLoaded(data: UserInfoResult)
        fun onDataNotAvailable(errorMsg: String)
    }

    interface LogoutCallback {
        fun logoutSuccess()
        fun logoutFailure(errorMsg: String)
    }
    interface ShowMsgCallback {
        fun showErrorMsg(errorMsg: String)
        fun showSuccessMsg(msg: String)
    }

    fun getData(ctx: Context, callBack: LoadDataCallback)
    fun uploadUserIcon(ctx: Context, bitmap: Bitmap, imgPath: String,callBack:ShowMsgCallback)
    fun logout(ctx: Context, callBack: LogoutCallback)
}
