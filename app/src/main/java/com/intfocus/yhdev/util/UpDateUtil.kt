package com.intfocus.yhdev.util

import android.app.DownloadManager
import android.content.Context
import android.net.Uri
import android.os.Environment
import com.daimajia.numberprogressbar.NumberProgressBar
import com.intfocus.yhdev.data.response.update.UpdateResult
import com.intfocus.yhdev.login.LauncherActivity
import com.intfocus.yhdev.login.listener.DownLoadProgressListener
import com.intfocus.yhdev.login.listener.OnCheckAssetsUpdateResultListener
import com.intfocus.yhdev.login.listener.OnUpdateResultListener
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.HttpService
import com.intfocus.yhdev.net.RetrofitUtil
import org.OpenUDID.OpenUDID_manager
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
    private var checkFirstSetupObservable: Subscription? = null

    fun checkUpdate(ctx: Context, currentVersionCode: Int, currentVersionName: String, listener: OnUpdateResultListener) {
        if (!HttpUtil.isConnected(ctx)) {
            listener.onFailure("请检查网络")
            return
        }
//        val data = Gson().fromJson<UpdateResult>(json, UpdateResult::class.java)
//        listener.onResultSuccess(data!!.data!!)
        RetrofitUtil.getHttpService(ctx)
                .getUpdateMsg("android", currentVersionCode, currentVersionName, OpenUDID_manager.getOpenUDID())
                .compose(RetrofitUtil.CommonOptions<UpdateResult>())
                .subscribe(object : CodeHandledSubscriber<UpdateResult>() {
                    override fun onBusinessNext(data: UpdateResult?) {
                        if (data!!.data!!.download_url != null)
                            listener.onResultSuccess(data!!.data!!)
                        else
                            listener.onFailure("下载链接异常")
                    }

                    override fun onCompleted() {

                    }

                    override fun onError(apiException: ApiException?) {

                    }
                })
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

    fun downAPKInBackground(ctx: LauncherActivity, download_path: String?, appName: String?) {
        val request = DownloadManager.Request(Uri.parse(download_path))
        //设置在什么网络情况下进行下载
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI)
        //设置通知栏标题
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        request.setTitle(appName + ".apk")
        request.setDescription("正在下载 永辉生意人")
        request.setAllowedOverRoaming(false)
        //设置文件存放目录
        request.setDestinationInExternalFilesDir(ctx, Environment.DIRECTORY_DOWNLOADS, appName + ".apk")
        var downManager = ctx.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        var id = downManager.enqueue(request)
    }

    fun downAPKInUI(ctx: LauncherActivity, download_path: String?, appName: String?, numberProgress: NumberProgressBar) {
        DownLoadApkAsyncTask(ctx,numberProgress, appName).execute(download_path)
//        downAPKObservable = Observable.just(download_path)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.newThread())
//                .map { downloadPath ->
//                    val response = Retrofit.Builder()
//                            .baseUrl("http://app-global.pgyer.com")
//                            .build()
//                            .create(HttpService::class.java).downloadFileWithDynamicUrlSync(downloadPath).execute()
//                    response
//                }
//                .map { response ->
//                    if (response?.body() != null) {
//                        val isWriteApkSuccess = FileUtil.writeResponseBodyToDisk(response.body(),
//                                Environment.getExternalStorageDirectory().path, appName + ".apk", object : DownLoadProgressListener {
//                            override fun updateProgress(percent: Long) {
////                progressBar!!.progress += (percent * 0.9).toInt()
//                            }
//                        })
//                        isWriteApkSuccess
//                    }
//                    false
//                }
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe { isWriteApkSuccess ->
//                    if (isWriteApkSuccess) {
//                        val intent = Intent(ACTION_VIEW)
//                        intent.setDataAndType(Uri.fromFile(File(Environment.getExternalStorageDirectory(), appName + ".apk")), "application/vnd.android.package-archive")
//                        ctx.startActivity(intent)
//                    } else {
//                        Toast.makeText(ctx, "更新失败", Toast.LENGTH_SHORT)
//                        ctx.finishIn2Minutes()
//                    }
//                }
    }

    fun checkFirstSetup(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()
        checkFirstSetupObservable = Observable.just("assets", "loading")
                .subscribeOn(Schedulers.io())
                .map { assetsName ->
                    FileUtil.makeSureFolderExist(String.format("%s/%s", FileUtil.basePath(ctx), K.kSharedDirName))
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
        if (checkAssetsUpdateObservable != null && !checkAssetsUpdateObservable!!.isUnsubscribed)
            checkAssetsUpdateObservable!!.unsubscribe()
    }

//    class DownLoadApkAsyncTask(val ctx: Context,private var numberProgress: NumberProgressBar, private val appName: String?) : AsyncTask<String, Int, String>() {
//        override fun onPreExecute() {
//            numberProgress.visibility = View.VISIBLE
//            super.onPreExecute()
//        }
//
//        override fun doInBackground(vararg params: String): String? {
//            var ips: InputStream? = null
//            var fops: FileOutputStream? = null
//            val filePath = Environment.getExternalStorageDirectory().path
//            try {
//                fops = FileOutputStream(File(filePath, appName + ".apk"))
//                val url = URL(params[0])
//                val conn = url.openConnection()
//                conn.connect()
//                ips = conn.getInputStream()
//                val fileLength = conn.contentLength
//                var len = 0
//                var total_length = 0
//                val data = ByteArray(4096)
//                while (true) {
//                    len = ips.read(data)
//                    if (len == -1) {
//                        break
//                    }
//                    total_length += len
//                    fops.write(data, 0, len)
//                    publishProgress((total_length / fileLength))
//                }
//
//            } catch (e: Exception) {
//
//            } finally {
//
//            }
//
//            return null
//        }
//
//
//        override fun onProgressUpdate(vararg values: Int?) {
//            numberProgress.progress = values[0]!!
//            super.onProgressUpdate(*values)
//        }
//
//        override fun onPostExecute(filePath: String) {
//            val intent = Intent(Intent.ACTION_VIEW)
//                        intent.setDataAndType(Uri.fromFile(File(filePath, appName + ".apk")), "application/vnd.android.package-archive")
//                        ctx.startActivity(intent)
//            super.onPostExecute(filePath)
//        }
//    }
}

