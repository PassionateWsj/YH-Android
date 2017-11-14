package com.intfocus.yhdev.general.mode

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.intfocus.yhdev.business.dashboard.mine.bean.UserInfoRequest
import com.intfocus.yhdev.general.constant.ConfigConstants
import com.intfocus.yhdev.general.constant.ToastColor
import com.intfocus.yhdev.general.data.response.BaseResult
import com.intfocus.yhdev.general.data.response.mine_page.UserInfoResult
import com.intfocus.yhdev.general.net.ApiException
import com.intfocus.yhdev.general.net.CodeHandledSubscriber
import com.intfocus.yhdev.general.net.RetrofitUtil
import com.intfocus.yhdev.general.util.*
import com.intfocus.yhdev.general.util.K.K_USER_DEVICE_ID
import com.intfocus.yhdev.general.util.K.K_USER_ID
import com.zbl.lib.baseframe.core.AbstractMode
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.greenrobot.eventbus.EventBus
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by liuruilin on 2017/6/7.
 */
class UserInfoMode(var ctx: Context) : AbstractMode() {
    lateinit var urlString: String
    var result: String? = null
    var mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
    var gson = Gson()

    override fun requestData() {
        RetrofitUtil.getHttpService(ctx).getUserInfo(mUserSP.getString(URLs.kUserNum, ""))
                .compose(RetrofitUtil.CommonOptions<UserInfoResult>())
                .subscribe(object : CodeHandledSubscriber<UserInfoResult>() {
                    override fun onBusinessNext(data: UserInfoResult?) {
                        val result1 = UserInfoRequest(true, 200)
                        result1.userInfoBean = data!!.data
                        EventBus.getDefault().post(result1)
                    }

                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onCompleted() {
                    }
                })
    }

    fun uplodeUserIcon(bitmap: Bitmap, imgPath: String) {
        Thread(Runnable {
            var format = SimpleDateFormat("yyyyMMddHHmmss")
            var date = Date(System.currentTimeMillis())
            File(imgPath).delete()
            var gravatarImgPath = FileUtil.dirPath(ctx, K.K_CONFIG_DIR_NAME, ConfigConstants.kAppCode + "_" + mUserSP.getString(URLs.kUserNum, "") + "_" + format.format(date) + ".jpg")
            FileUtil.saveImage(gravatarImgPath, bitmap)
            var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(gravatarImgPath))
            var multiPartBody = MultipartBody.Part.createFormData("gravatar", mUserSP.getString(K_USER_ID, "0") + "icon", requestBody)
            RetrofitUtil.getHttpService(ctx).userIconUpload(mUserSP.getString(K_USER_DEVICE_ID, "0"), mUserSP.getString(URLs.kUserNum, "0"), multiPartBody)
                    .compose(RetrofitUtil.CommonOptions<BaseResult>())
                    .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                        override fun onBusinessNext(data: BaseResult?) {
                            ToastUtils.show(ctx, "头像已上传", ToastColor.SUCCESS)
                        }

                        override fun onError(apiException: ApiException?) {
                            ToastUtils.show(ctx, apiException!!.displayMessage)

                        }

                        override fun onCompleted() {
                        }
                    })
        }).start()
    }

    fun modifiedUserConfig(isLogin: Boolean) {
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
