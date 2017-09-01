package com.intfocus.yhdev.util

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Environment
import com.daimajia.numberprogressbar.NumberProgressBar
import com.google.gson.Gson
import com.intfocus.yhdev.data.response.update.UpdateResult
import com.intfocus.yhdev.login.LauncherActivity
import com.intfocus.yhdev.login.listener.DownLoadProgressListener
import com.intfocus.yhdev.login.listener.OnCheckAssetsUpdateResultListener
import com.intfocus.yhdev.login.listener.OnUpdateResultListener
import com.intfocus.yhdev.net.HttpService
import retrofit2.Retrofit
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File


/**
 * ****************************************************
 * author: jameswong
 * created on: 17/08/29 上午10:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object UpDateUtil {
    private var observable: Subscription? = null
    private var json = "{\n" +
            "  \"code\": 200,\n" +
            "  \"message\": \"successfully\",\n" +
            "  \"data\": {\n" +
            "    \"app_version\": \"1.0.1\",\n" +
            "    \"is_update\": \"1\", \n" +
            "    \"description\": \"版本说明版本说明版本说明版本说明版本说明版本说明版本说明版本说明版本说明\", \n" +
            "    \"download_path\": \"http://app-global.pgyer.com/9fddda1e5f82fd104d693be09cb19706.apk?e=1504231421&attname=app-release.apk&token=6fYeQ7_TVB5L0QSzosNFfw2HU8eJhAirMF5VxV9G:FCYRbUf690dQTNid4F2tWNTqv9g=&sign=a7b8f0c3b618ecc68ab738bd3f41e4fa&t=59a8bffd\", \n" +
            "    \"assets\": [\n" +
            "      {\n" +
            "        \"file_name\": \"assets\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": false  \n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"loading\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"javascripts\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"fonts\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"images\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"icons\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"stylesheets\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"file_name\": \"advertisement\",\n" +
            "        \"md5\": \"\",\n" +
            "        \"is_assets\": true\n" +
            "      }\n" +
            "    ]\n" +
            "  }\n" +
            "}"

    fun checkUpdate(ctx: Context, currentVersionCode: String, listener: OnUpdateResultListener) {
        if (!HttpUtil.isConnected(ctx)) {
            listener.onFailure("请检查网络")
            return
        }
        val data = Gson().fromJson<UpdateResult>(json, UpdateResult::class.java)
        listener.onResultSuccess(data!!.data!!)
//        RetrofitUtil.getHttpService(ctx)
//                .getUpdateMsg(currentVersionCode, OpenUDID_manager.getOpenUDID())
//                .compose(RetrofitUtil.CommonOptions<UpdateResult>())
//                .subscribe(object : CodeHandledSubscriber<UpdateResult>() {
//                    override fun onBusinessNext(data: UpdateResult?) {
//                        listener.onResultSuccess(data!!.data!!)
//                    }
//
//                    override fun onCompleted() {
//
//                    }
//
//                    override fun onError(apiException: ApiException?) {
//
//                    }
//
//                })
    }

    fun checkAssetsUpdate(ctx: Context, assetsList: List<UpdateResult.UpdateData.AssetsBean>, progressBar: NumberProgressBar?, listener: OnCheckAssetsUpdateResultListener, progressPercent: DownLoadProgressListener?) {
        val sharedPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kSharedDirName)
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()

        observable = Observable.from(assetsList)
                .subscribeOn(Schedulers.io())
                .map { assets ->
                    if ((assets.md5) != mAssetsSP.getString(assets.file_name + "_md5", "no_md5") && assets.file_name != "assets"&& assets.file_name != "loading") {
                        val fileUrl = K.kDownloadAssetsZip + "?api_token=d93c1a0dc03fe4ffad55a82febd1c94f&filename=" + assets.file_name + ".zip"
                        val response = Retrofit.Builder()
                                .baseUrl(K.kBaseUrl)
                                .build()
                                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(fileUrl).execute()
                        val isWriteZipSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!, sharedPath, assets.file_name + ".zip", progressPercent)
                        if (isWriteZipSuccess) {
                            val isUnZipSuccess = FileUtil.unZipAssets(ctx, assets.file_name)
                            if (isUnZipSuccess) {
                                mAssetsSPEdit.putString(assets.file_name + "_md5", assets.md5).commit()
                                true
                            }
                            false
                        }
                        false
                    }
                    true
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onError(p0: Throwable?) {
                        listener.onFailure("样式更新失败")
                    }

                    override fun onCompleted() {
                        LogUtil.d("hjjzz", "unzip:onCompleted:::" + Thread.currentThread().name)
                        listener.onResultSuccess()
                        if (progressBar != null) {
                            progressBar.progress += 10
                        }
                    }

                    override fun onNext(isCheckSuccess: Boolean?) {
                        if (!isCheckSuccess!!)
                            this.onError(kotlin.Throwable("解压出错"))
                        if (progressBar != null) {
                            progressBar.progress += 10
                        }
                    }
                })
    }

    /**
     * 取消订阅
     */
    fun unSubscribe() {
        if (observable != null && !observable!!.isUnsubscribed)
            observable!!.unsubscribe()
    }

    fun downAPK(ctx: LauncherActivity, download_path: String?, packageName: String?, progressBar: NumberProgressBar?) {
        val response = Retrofit.Builder()
                .baseUrl("")
                .build()
                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(download_path).execute()
        val isWriteApkSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!,
                Environment.getExternalStorageDirectory().path, packageName + ".apk", object : DownLoadProgressListener {
            override fun updateProgress(percent: Long) {
                progressBar!!.progress += (percent * 0.9).toInt()
            }
        })
        if (isWriteApkSuccess) {
            val intent = Intent(ACTION_VIEW)
            intent.setDataAndType(Uri.fromFile(File(Environment.getExternalStorageDirectory(), packageName + ".apk")), "application/vnd.android.package-archive")
            ctx.startActivity(intent)
        }
    }

    fun checkFirstSetup(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()
        Observable.just("assets", "loading")
                .subscribeOn(Schedulers.io())
                .map { assetsName ->
                    FileUtil.copyAssetFile(ctx, assetsName + ".zip", String.format("%s/%s/%s", FileUtil.basePath(ctx), K.kSharedDirName, assetsName + ".zip"))
                    var isUnZipSuccess = FileUtil.unZipAssets(ctx, assetsName)
                    if (isUnZipSuccess) {
                        val mD5 = FileUtil.MD5(File(String.format("%s/%s/%s", FileUtil.basePath(ctx), K.kSharedDirName, assetsName + ".zip")))
                        mAssetsSPEdit.putString(assetsName + "_md5", mD5).commit()
                        true
                    }
                    false
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<Boolean> {
                    override fun onError(p0: Throwable?) {

                    }

                    override fun onNext(p0: Boolean?) {
                    }

                    override fun onCompleted() {
                        listener.onResultSuccess()
                    }

                })
    }
}
