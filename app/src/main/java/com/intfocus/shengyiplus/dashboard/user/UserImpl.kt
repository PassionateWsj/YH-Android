package com.intfocus.shengyiplus.subject.one

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import com.intfocus.shengyiplus.ConfigConstants
import com.intfocus.shengyiplus.constant.Params
import com.intfocus.shengyiplus.general.net.ApiException
import com.intfocus.shengyiplus.general.net.CodeHandledSubscriber
import com.intfocus.shengyiplus.general.net.RetrofitUtil
import com.intfocus.shengyiplus.model.response.BaseResult
import com.intfocus.shengyiplus.model.response.mine_page.UserInfoResult
import com.intfocus.shengyiplus.util.*
import com.taobao.accs.utl.UtilityImpl
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import rx.Subscription
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc: 模板一 Model 层
 * ****************************************************
 */
class UserImpl : UserModel {

    companion object {
        private val TAG = "UserImpl"
        private var INSTANCE: UserImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): UserImpl {
            return INSTANCE ?: UserImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            INSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    override fun getData(ctx: Context, callBack: UserModel.LoadDataCallback) {
        val mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        RetrofitUtil.getHttpService(ctx).getUserInfo(mUserSP.getString(Params.USER_NUM, ""))
                .compose(RetrofitUtil.CommonOptions<UserInfoResult>())
                .subscribe(object : CodeHandledSubscriber<UserInfoResult>() {
                    override fun onBusinessNext(data: UserInfoResult?) {
                        data?.let { callBack.onDataLoaded(it) }
                    }

                    override fun onError(apiException: ApiException?) {
                        apiException?.let { callBack.onDataNotAvailable(it.displayMessage) }
                    }

                    override fun onCompleted() {
                    }
                })
    }

    @SuppressLint("SimpleDateFormat")
    override fun uploadUserIcon(ctx: Context, bitmap: Bitmap, imgPath: String, callBack: UserModel.ShowMsgCallback) {
        val mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        val format = SimpleDateFormat("yyyyMMddHHmmss")
        val date = Date(System.currentTimeMillis())
        File(imgPath).delete()
        val gravatarImgPath = FileUtil.dirPath(ctx, K.K_CONFIG_DIR_NAME, ConfigConstants.kAppCode + "_" + mUserSP.getString(Params.USER_NUM, "") + "_" + format.format(date) + ".jpg")
        FileUtil.saveImage(gravatarImgPath, bitmap)
        val requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(gravatarImgPath))
        val multiPartBody = MultipartBody.Part.createFormData("gravatar", mUserSP.getString(K.K_USER_ID, "0") + "icon", requestBody)
        RetrofitUtil.getHttpService(ctx).userIconUpload(mUserSP.getString(K.K_USER_DEVICE_ID, "0"), mUserSP.getString(Params.USER_NUM, "0"), multiPartBody)
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onBusinessNext(data: BaseResult?) {
                        callBack.showSuccessMsg("头像已上传")
                    }

                    override fun onError(apiException: ApiException?) {
                        apiException?.let { callBack.showErrorMsg(it.displayMessage) }

                    }

                    override fun onCompleted() {
                    }
                })
    }

    override fun logout(ctx: Context, callBack: UserModel.LogoutCallback) {
        val mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        // 判断有无网络
        if (!UtilityImpl.isNetworkConnected(ctx)) {
            callBack.logoutFailure("未连接网络, 无法退出")
            return
        }
        val mEditor = ctx.getSharedPreferences("SettingPreference", Context.MODE_PRIVATE).edit()
        mEditor.putBoolean("ScreenLock", false).apply()
        // 退出登录 POST 请求
        RetrofitUtil.getHttpService(ctx).userLogout(mUserSP.getString(K.K_USER_DEVICE_ID, "0"))
                .compose(RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                    override fun onBusinessNext(data: BaseResult?) {
                        if (data!!.code == "200") {
                            mUserSP.edit().putBoolean(Params.IS_LOGIN, false).apply()

                            ActionLogUtil.actionLog("退出登录")

                            modifiedUserConfig(ctx, false)
                            callBack.logoutSuccess()
                        } else {
                            callBack.logoutFailure(data.message!!)
                        }
                    }

                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException?) {
                        ToastUtils.show(ctx, apiException!!.message!!)
                    }

                })
    }

    fun modifiedUserConfig(ctx: Context, isLogin: Boolean) {
        try {
            val configJSON = JSONObject()
            configJSON.put("is_login", isLogin)
            val userConfigPath = String.format("%s/%s", FileUtil.basePath(ctx), K.K_USER_CONFIG_FILE_NAME)
            var userJSON = FileUtil.readConfigFile(userConfigPath)

            userJSON = ApiHelper.mergeJson(userJSON, configJSON)
            FileUtil.writeFile(userConfigPath, userJSON.toString())

            val settingsConfigPath = FileUtil.dirPath(ctx, K.K_CONFIG_DIR_NAME, K.K_SETTING_CONFIG_FILE_NAME)
            FileUtil.writeFile(settingsConfigPath, userJSON.toString())
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }
}
