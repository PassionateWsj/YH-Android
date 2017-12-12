package com.intfocus.shengyiplus.subject.one

import android.content.Context
import android.graphics.Bitmap
import com.intfocus.shengyiplus.model.response.mine_page.UserInfoResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class UserPresenter(
        private val mModel: UserImpl,
        private val mView: UserContract.View
) : UserContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(ctx: Context) {
        mModel.getData(ctx, object : UserModel.LoadDataCallback {
            override fun onDataLoaded(data: UserInfoResult) {
                mView.dataLoaded(data)
            }

            override fun onDataNotAvailable(errorMsg: String) {
                mView.showErrorMsg(errorMsg)
            }
        })
    }

    override fun uploadUserIcon(ctx: Context, bitmap: Bitmap, imgPath: String) {
        mModel.uploadUserIcon(ctx, bitmap, imgPath,object :UserModel.ShowMsgCallback{
            override fun showErrorMsg(errorMsg: String) {
                mView.showErrorMsg(errorMsg)
            }

            override fun showSuccessMsg(msg: String) {
                mView.showSuccessMsg(msg)
            }
        })
    }

    override fun logout(ctx: Context) {
        mModel.logout(ctx, object : UserModel.LogoutCallback {
            override fun logoutSuccess() {
                mView.logoutSuccess()
            }

            override fun logoutFailure(errorMsg: String) {
                mView.showErrorMsg(errorMsg)
            }
        })
    }
}
