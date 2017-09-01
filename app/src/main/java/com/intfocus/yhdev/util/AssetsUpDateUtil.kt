package com.intfocus.yhdev.util

import android.content.Context
import com.daimajia.numberprogressbar.NumberProgressBar
import com.intfocus.yhdev.data.response.assets.AssetsResult
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.HttpService
import com.intfocus.yhdev.net.RetrofitUtil
import retrofit2.Retrofit
import rx.Observable
import rx.Observer
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * author: jameswong
 * created on: 17/08/29 上午10:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object AssetsUpDateUtil {
    private lateinit var observable: Subscription

    fun checkAssetsUpdate(ctx: Context, listener: OnCheckAssetsUpdateResultListener) {
        checkAssetsUpdate(ctx, null, listener)
    }

    fun checkAssetsUpdate(ctx: Context, progressBar: NumberProgressBar?, listener: OnCheckAssetsUpdateResultListener) {
        var sharedPath = String.format("%s/%s", FileUtil.basePath(ctx), K.kSharedDirName)
        LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
        RetrofitUtil.getHttpService(ctx).assetsMD5
                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onCompleted() {

                    }

                    override fun onBusinessNext(data: AssetsResult?) {
                        LogUtil.d("hjjzz", "getHttpService(ctx).assetsMD5:::" + Thread.currentThread().name)
                        val assetsMD5s = data!!.data!!
                        val mAssetsSP = ctx.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
                        val mAssetsSPEdit = mAssetsSP.edit()
                        val assetsNameArr = listOf(
                                URLs.kAssets, URLs.kLoading, URLs.kImages,
                                URLs.kIcons, URLs.kJavaScripts,  URLs.kFonts, URLs.kStylesheets, URLs.kAdvertisement)
                        val assetsMD5sMap = HashMap<String, String>()
                        assetsMD5sMap.put(URLs.kAssets + "_md5", assetsMD5s.assets_md5!!)
                        assetsMD5sMap.put(URLs.kFonts + "_md5", assetsMD5s.fonts_md5!!)
                        assetsMD5sMap.put(URLs.kImages + "_md5", assetsMD5s.images_md5!!)
                        assetsMD5sMap.put(URLs.kIcons + "_md5", assetsMD5s.icons_md5!!)
                        assetsMD5sMap.put(URLs.kJavaScripts + "_md5", assetsMD5s.javascripts_md5!!)
                        assetsMD5sMap.put(URLs.kLoading + "_md5", assetsMD5s.loading_md5!!)
                        assetsMD5sMap.put(URLs.kStylesheets + "_md5", assetsMD5s.stylesheets_md5!!)
                        assetsMD5sMap.put(URLs.kAdvertisement + "_md5", assetsMD5s.advertisement_md5!!)

                        if (progressBar != null)
                            progressBar.progress += 10

                        observable = Observable.from(assetsNameArr)
                                .subscribeOn(Schedulers.io())
                                .map { assetName ->
                                    LogUtil.d("hjjzz", "unzip:::" + Thread.currentThread().name)
                                    if (!assetsMD5sMap[assetName + "_md5"].equals(mAssetsSP.getString(assetName + "_md5", ""))) {
                                        var fileUrl = K.kDownloadAssetsZip + "?api_token=d93c1a0dc03fe4ffad55a82febd1c94f&filename=" + assetName + ".zip"
                                        var response = Retrofit.Builder()
                                                .baseUrl(K.kBaseUrl)
                                                .build()
                                                .create(HttpService::class.java).downloadFileWithDynamicUrlSync(fileUrl).execute()
                                        var isWriteZipSuccess = FileUtil.writeResponseBodyToDisk(response!!.body()!!, sharedPath, assetName + ".zip")
                                        if (isWriteZipSuccess) {
                                            var isUnZipSuccess = FileUtil.unZipAssets(ctx, assetName)
                                            if (isUnZipSuccess) {
                                                mAssetsSPEdit.putString(assetName + "_md5", assetsMD5sMap[assetName + "_md5"]).commit()
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
                                        listener.onFailure()
                                    }

                                    override fun onCompleted() {
                                        LogUtil.d("hjjzz", "unzip:onCompleted:::" + Thread.currentThread().name)
                                        if (progressBar != null)
                                            progressBar.progress += 10
                                        listener.onResultSuccess()
                                    }

                                    override fun onNext(isCheckSuccess: Boolean?) {
                                        if (!isCheckSuccess!!)
                                            this.onError(kotlin.Throwable("解压出错"))
                                        if (progressBar != null)
                                            progressBar.progress += 10
                                    }
                                })
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
}

interface OnCheckAssetsUpdateResultListener {
    fun onResultSuccess()
    fun onFailure()
}