package com.intfocus.yhdev.util

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.os.Environment
import android.widget.Toast
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
    private var checkAssetsUpdateObservable: Subscription? = null
    private var downAPKObservable: Subscription? = null
    private var checkFirstSetupObservable: Subscription? = null
    private var json = "{\n" +
            "    \"data\": {\n" +
            "        \"title\": \"1\",\n" +
            "        \"version\": \"1.0.8\",\n" +
            "        \"build\": 2,\n" +
            "        \"download_url\": \"https://www.pgyer.com/apiv1/app/install?aId=d4eaa112b6713a256b1442dd9c078c2d&_api_key=45be6d228e747137bd192c4c47d4f64a\",\n" +
            "        \"upgrade_level\": 1,\n" +
            "        \"description\": \"1\",\n" +
            "        \"assets\": [\n" +
            "            {\n" +
            "                \"file_name\": \"assets\",\n" +
            "                \"md5\": \"e4acc7fbd00fc107c756eebaa365ac00\",\n" +
            "                \"is_assets\": false\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"loading\",\n" +
            "                \"md5\": \"8bd5c6a91d38848d3160e6c8a462b852\",\n" +
            "                \"is_assets\": false\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"fonts\",\n" +
            "                \"md5\": \"5901960c857600316c3d141401c3af08\",\n" +
            "                \"is_assets\": true\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"icons\",\n" +
            "                \"md5\": \"7afa625cca643d01a6b12d80a19d4756\",\n" +
            "                \"is_assets\": true\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"images\",\n" +
            "                \"md5\": \"65266455bea40469dcb9f022f63ce769\",\n" +
            "                \"is_assets\": true\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"javascripts\",\n" +
            "                \"md5\": \"e55b643bbde61075119fb25ffc9c8b5d\",\n" +
            "                \"is_assets\": true\n" +
            "            },\n" +
            "            {\n" +
            "                \"file_name\": \"stylesheets\",\n" +
            "                \"md5\": \"923b05c441a8cef0daf32ed392aee633\",\n" +
            "                \"is_assets\": true\n" +
            "            }\n" +
            "        ]\n" +
            "    },\n" +
            "    \"code\": 200,\n" +
            "    \"message\": \"default successfully\"\n" +
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

        checkAssetsUpdateObservable = Observable.from(assetsList)
                .subscribeOn(Schedulers.io())
                .map { assets ->
                    if ((assets.md5) != mAssetsSP.getString(assets.file_name + "_md5", "no_md5") && assets.isIs_assets) {
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

    fun downAPK(ctx: LauncherActivity, download_path: String?, packageName: String?, progressBar: NumberProgressBar?) {
        downAPKObservable = Observable.just(download_path)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.newThread())
                .map { downloadPath ->
                    val response = Retrofit.Builder()
                            .baseUrl("http://app-global.pgyer.com")
                            .build()
                            .create(HttpService::class.java).downloadFileWithDynamicUrlSync(downloadPath).execute()
                    response
                }
                .map { response ->
                    if (response?.body() != null) {
                        val isWriteApkSuccess = FileUtil.writeResponseBodyToDisk(response.body(),
                                Environment.getExternalStorageDirectory().path, packageName + ".apk", object : DownLoadProgressListener {
                            override fun updateProgress(percent: Long) {
//                progressBar!!.progress += (percent * 0.9).toInt()
                            }
                        })
                        isWriteApkSuccess
                    }
                    false
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isWriteApkSuccess ->
                    if (isWriteApkSuccess) {
                        val intent = Intent(ACTION_VIEW)
                        intent.setDataAndType(Uri.fromFile(File(Environment.getExternalStorageDirectory(), packageName + ".apk")), "application/vnd.android.package-archive")
                        ctx.startActivity(intent)
                    } else {
                        Toast.makeText(ctx, "更新失败", Toast.LENGTH_SHORT)
                        ctx.finishIn2Minutes()
                    }
                }

    }

    fun checkFirstSetup(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()
        checkFirstSetupObservable = Observable.just("assets", "loading")
                .subscribeOn(Schedulers.io())
                .map { assetsName ->
//                    FileUtil.makeSureFolderExist(String.format("%s/%s", FileUtil.basePath(ctx), K.kSharedDirName))
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


    /**
     * 取消订阅
     */
    fun unSubscribe() {
        if (checkFirstSetupObservable != null && !checkFirstSetupObservable!!.isUnsubscribed)
            checkFirstSetupObservable!!.unsubscribe()
        if (downAPKObservable != null && !downAPKObservable!!.isUnsubscribed)
            downAPKObservable!!.unsubscribe()
        if (checkAssetsUpdateObservable != null && !checkAssetsUpdateObservable!!.isUnsubscribed)
            checkAssetsUpdateObservable!!.unsubscribe()
    }
}
