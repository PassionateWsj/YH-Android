package com.intfocus.yhdev.util

import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
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
    private lateinit var observable: Subscription

    fun checkUpdate(ctx: Context, currentVersionCode: String, listener: OnUpdateResultListener) {
        if (!HttpUtil.isConnected(ctx)) {
            listener.onFailure("请检查网络")
            return
        }
        RetrofitUtil.getHttpService(ctx)
                .getUpdateMsg(currentVersionCode, OpenUDID_manager.getOpenUDID())
                .compose(RetrofitUtil.CommonOptions<UpdateResult>())
                .subscribe(object : CodeHandledSubscriber<UpdateResult>() {
                    override fun onBusinessNext(data: UpdateResult?) {
                        listener.onResultSuccess(data!!.data!!)
                    }

                    override fun onCompleted() {

                    }

                    override fun onError(apiException: ApiException?) {

                    }

                })
    }

//    fun checkAssetsUpdate(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
//        if (!HttpUtil.isConnected(ctx)) {
//            listener.onFailure("请检查网络")
//            return
//        }
//        RetrofitUtil.getHttpService(ctx)
//                .getUpdateMsg(ctx.packageManager.getPackageInfo(ctx.packageName, 0).versionName, OpenUDID_manager.getOpenUDID())
//                .compose(RetrofitUtil.CommonOptions<UpdateResult>())
//                .subscribe(object : CodeHandledSubscriber<UpdateResult>() {
//                    override fun onBusinessNext(data: UpdateResult?) {
//                        checkAssetsUpdate(ctx, data!!.data!!.assets!!, null, listener)
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
//    }

    fun checkAssetsUpdate(ctx: Context, assetsList: List<UpdateResult.UpdateData.AssetsBean>, progressBar: NumberProgressBar?, listener: OnCheckAssetsUpdateResultListener) {
        var sharedPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kSharedDirName)
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()

        observable = Observable.from(assetsList)
                .subscribeOn(Schedulers.io())
                .map { assets ->
                    LogUtil.d("hjjzz", "unzip:::" + Thread.currentThread().name)
                    if ((assets.md5 + "_md5") == mAssetsSP.getString(assets.file_name + "_md5", "")) {
                        var fileUrl = K.kDownloadAssetsZip + "?api_token=d93c1a0dc03fe4ffad55a82febd1c94f&filename=" + assets.file_name + ".zip"
                        var response = Retrofit.Builder()
                                .baseUrl("")
                                .build()
                                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(K.kBaseUrl + fileUrl).execute()
                        var isWriteZipSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!, sharedPath, assets.file_name + ".zip", object : DownLoadProgressListener {
                            override fun updateProgress(percent: Long) {
                                if (progressBar != null)
                                    progressBar.progress += (percent * 0.1).toInt()
                            }
                        })
                        if (isWriteZipSuccess) {
                            var isUnZipSuccess = FileUtil.unZipAssets(ctx, assets.file_name)
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
                    }

                    override fun onNext(isCheckSuccess: Boolean?) {
                        if (!isCheckSuccess!!)
                            this.onError(kotlin.Throwable("解压出错"))
                    }
                })
    }

    /**
     * 取消订阅
     */
    fun unSubscribe() {
        if (observable != null && !observable.isUnsubscribed)
            observable.unsubscribe()
    }

    fun downAPK(ctx: LauncherActivity, download_path: String?, packageName: String?, progressBar: NumberProgressBar?) {
        var response = Retrofit.Builder()
                .baseUrl("")
                .build()
                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(download_path).execute()
        var isWriteApkSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!,
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
}
