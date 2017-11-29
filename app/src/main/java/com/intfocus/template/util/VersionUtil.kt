package com.intfocus.template.util

import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import com.intfocus.template.constant.Params.DATA
import com.intfocus.template.constant.ToastColor
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

/**
 * @author liuruilin
 * @data 2017/11/26
 * @describe
 */
object VersionUtil {

    /**
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     * @param activity
     * @param isShowToast
     */
    fun checkPgyerVersionUpgrade(activity: Activity, isShowToast: Boolean) {
        PgyUpdateManager.register(activity, "com.intfocus.template.fileprovider", object : UpdateManagerListener() {
            override fun onUpdateAvailable(result: String?) {
                try {
                    val appBean = UpdateManagerListener.getAppBeanFromString(result)

                    if (result == null || result.isEmpty()) {
                        return
                    }

                    val packageInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
                    val currentVersionCode = packageInfo.versionCode
                    val response = JSONObject(result)
                    val message = response.getString("message")

                    val responseVersionJSON = response.getJSONObject(DATA)
                    val newVersionCode = responseVersionJSON.getInt("versionCode")

                    val newVersionName = responseVersionJSON.getString("versionName")

                    if (currentVersionCode >= newVersionCode) {
                        return
                    }

                    val pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(activity.applicationContext), K.K_PGYER_VERSION_CONFIG_FILE_NAME)
                    FileUtil.writeFile(pgyerVersionPath, result)

                    if (newVersionCode % 2 == 1) {
                        if (isShowToast) {
                            ToastUtils.show(activity, String.format("有发布测试版本%s(%s)", newVersionName, newVersionCode), ToastColor.SUCCESS)
                        }

                        return
                    } else if (HttpUtil.isWifi(activity.applicationContext) && newVersionCode % 10 == 8) {

                        UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL)

                        return
                    }
                    AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(if (message.isEmpty()) "无升级简介" else message)
                            .setPositiveButton(
                                    "确定"
                            ) { dialog, which -> UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL) }
                            .setNegativeButton("下一次"
                            ) { dialog, which -> dialog.dismiss() }
                            .setCancelable(false)
                            .show()

                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onNoUpdateAvailable() {
                if (isShowToast) {
                    ToastUtils.show(activity, "已是最新版本")
                }
            }
        })
    }
}