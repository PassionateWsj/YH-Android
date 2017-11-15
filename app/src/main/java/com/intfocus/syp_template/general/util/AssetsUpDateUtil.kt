package com.intfocus.syp_template.general.util

import android.content.Context
import com.daimajia.numberprogressbar.NumberProgressBar
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.data.response.asset.AssetsResult
import com.intfocus.syp_template.general.net.ApiException
import com.intfocus.syp_template.general.net.CodeHandledSubscriber
import com.intfocus.syp_template.general.net.HttpService
import com.intfocus.syp_template.general.net.RetrofitUtil
import retrofit2.Retrofit
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.File

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/08/29 上午10:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object AssetsUpDateUtil {
    private val TAG = "hjjzz"
    private var observable: Subscription? = null

    fun checkAssetsUpdate(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        checkAssetsUpdate(ctx, null, listener)
    }

    fun checkAssetsUpdate(ctx: Context, progressBar: NumberProgressBar?, listener: OnCheckAssetsUpdateResultListener) {

        val sharedPath = String.format("%s/%s", FileUtil.basePath(ctx), K.K_SHARED_DIR_NAME)
        LogUtil.d(TAG, "MainThread:::" + Thread.currentThread().name)
        // 获取静态资源 MD5
        RetrofitUtil.getHttpService(ctx).assetsMD5
                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onCompleted() {

                    }

                    override fun onBusinessNext(data: AssetsResult?) {
                        LogUtil.d(TAG, "getHttpService(ctx).assetsMD5:::" + Thread.currentThread().name)
                        val assetsMD5s = data!!.data!!
                        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
                        val mAssetsSPEdit = mAssetsSP.edit()
                        val assetsNameArr = listOf(URLs.kFonts, URLs.kImages,
                                URLs.kIcons, URLs.kStylesheets,
                                URLs.kJavaScripts, URLs.kAdvertisement)
                        val assetsMD5sMap = HashMap<String, String>()
                        assetsMD5sMap.put(URLs.kFonts + "_md5", assetsMD5s.fonts_md5!!)
                        assetsMD5sMap.put(URLs.kImages + "_md5", assetsMD5s.images_md5!!)
                        assetsMD5sMap.put(URLs.kIcons + "_md5", assetsMD5s.icons_md5!!)
                        assetsMD5sMap.put(URLs.kJavaScripts + "_md5", assetsMD5s.javascripts_md5!!)
                        assetsMD5sMap.put(URLs.kStylesheets + "_md5", assetsMD5s.stylesheets_md5!!)
                        assetsMD5sMap.put(URLs.kAdvertisement + "_md5", assetsMD5s.advertisement_md5!!)

                        if (progressBar != null){
                            progressBar.progress += 10
                        }

                        // RxJava 异步更新静态资源
                        observable = Observable.from(assetsNameArr)
                                // 转到 io 线程
                                .subscribeOn(Schedulers.io())
                                .map { assetName ->
                                    LogUtil.d(TAG, "unzip:::" + Thread.currentThread().name)
                                    // 判断更新的 MD5 值是否与本地存储的 MD5 值相等
                                    // 不相等：下载最新 zip
                                    if (!assetsMD5sMap[assetName + "_md5"].equals(mAssetsSP.getString(assetName + "_md5", ""))) {
                                        val fileUrl = K.K_DOWNLOAD_ASSETS_ZIP + "?api_token=d93c1a0dc03fe4ffad55a82febd1c94f&filename=" + assetName + ".zip"
                                        val response = Retrofit.Builder()
                                                .baseUrl(ConfigConstants.kBaseUrl)
                                                .build()
                                                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(fileUrl).execute()
                                        // 保存到指定目录
                                        val isWriteZipSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!, sharedPath, assetName + ".zip")
                                        if (isWriteZipSuccess) {
                                            // 解压 zip
                                            val isUnZipSuccess = FileUtil.unZipAssets(ctx, assetName)
                                            if (isUnZipSuccess) {
                                                return@map mAssetsSPEdit.putString(assetName + "_md5", assetsMD5sMap[assetName + "_md5"]).commit()
                                            }
                                        }
                                    } else {
                                        return@map true
                                    }
                                    return@map false
                                }
                                // 回到 UI 线程
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(object : Observer<Boolean> {
                                    override fun onError(p0: Throwable?) {
                                        listener.onFailure()
                                    }

                                    override fun onCompleted() {
                                        LogUtil.d(TAG, "unzip:onCompleted:::" + Thread.currentThread().name)
                                        if (progressBar != null){
                                            progressBar.progress += 10
                                        }
                                        listener.onResultSuccess()
                                    }

                                    override fun onNext(isCheckSuccess: Boolean?) {
                                        if (!isCheckSuccess!!) {
                                            this.onError(kotlin.Throwable("更新出错"))
                                        }
                                        if (progressBar != null) {
                                            progressBar.progress += 10
                                        }
                                    }
                                })
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

    fun checkFirstSetup(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        // 判断目标目录是否存在
        makeSureFolderExist(ctx, K.K_SHARED_DIR_NAME)
        makeSureFolderExist(ctx, K.K_CACHED_DIR_NAME)
        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        val mAssetsSPEdit = mAssetsSP.edit()
        Observable.just("assets", "loading")
                .subscribeOn(Schedulers.io())
                .map { assetsName ->
                    FileUtil.copyAssetFile(ctx, assetsName + ".zip", String.format("%s/%s/%s", FileUtil.basePath(ctx), K.K_SHARED_DIR_NAME, assetsName + ".zip"))
                    val isUnZipSuccess = FileUtil.unZipAssets(ctx, assetsName)
                    if (isUnZipSuccess) {
                        val mD5 = FileUtil.MD5(File(String.format("%s/%s/%s", FileUtil.basePath(ctx), K.K_SHARED_DIR_NAME, assetsName + ".zip")))
                        return@map mAssetsSPEdit.putString(assetsName + "_md5", mD5).commit()
                    }
                    return@map false
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

    private fun makeSureFolderExist(ctx: Context, folderName: String) {
        val cachedPath = String.format("%s/%s", FileUtil.basePath(ctx), folderName)
        FileUtil.makeSureFolderExist(cachedPath)
    }
}

interface OnCheckAssetsUpdateResultListener {
    fun onResultSuccess()
    fun onFailure()
}
