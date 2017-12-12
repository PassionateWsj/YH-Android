package com.intfocus.template.subject.one

import android.content.Context
import android.graphics.Bitmap
import com.intfocus.template.base.BasePresenter
import com.intfocus.template.base.BaseView
import com.intfocus.template.model.response.mine_page.UserInfoResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 上午10:57
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface UserContract {

    interface View : BaseView<Presenter> {
        fun dataLoaded(data: UserInfoResult)
        fun logoutSuccess()
        fun showErrorMsg(errorMsg: String)
        fun showSuccessMsg(msg: String)
    }

    interface Presenter : BasePresenter {
        fun loadData(ctx: Context)
        fun uploadUserIcon(ctx: Context, bitmap: Bitmap, imgPath: String)
        fun logout(ctx: Context)
    }
}