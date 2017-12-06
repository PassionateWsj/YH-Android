package com.intfocus.template.dashboard

import android.content.Context
import android.graphics.Bitmap
import com.google.gson.Gson
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.response.BaseResult
import com.intfocus.template.util.ApiHelper
import com.intfocus.template.util.FileUtil
import com.intfocus.template.util.K
import com.intfocus.template.util.K.K_USER_DEVICE_ID
import com.intfocus.template.util.K.K_USER_ID
import com.intfocus.template.util.ToastUtils
import com.zbl.lib.baseframe.core.AbstractMode
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
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

    }

    fun uploadUserIcon(bitmap: Bitmap, imgPath: String) {
        Thread(Runnable {
            var format = SimpleDateFormat("yyyyMMddHHmmss")
            var date = Date(System.currentTimeMillis())
            File(imgPath).delete()
            var gravatarImgPath = FileUtil.dirPath(ctx, K.K_CONFIG_DIR_NAME, ConfigConstants.kAppCode + "_" + mUserSP.getString(USER_NUM, "") + "_" + format.format(date) + ".jpg")
            FileUtil.saveImage(gravatarImgPath, bitmap)
            var requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), File(gravatarImgPath))
            var multiPartBody = MultipartBody.Part.createFormData("gravatar", mUserSP.getString(K_USER_ID, "0") + "icon", requestBody)
            RetrofitUtil.getHttpService(ctx).userIconUpload(mUserSP.getString(K_USER_DEVICE_ID, "0"), mUserSP.getString(USER_NUM, "0"), multiPartBody)
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
