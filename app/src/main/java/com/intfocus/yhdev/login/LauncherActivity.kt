package com.intfocus.yhdev.login

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import com.intfocus.yhdev.R
import com.intfocus.yhdev.data.response.assets.AssetsResult
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.RetrofitUtil
import com.intfocus.yhdev.screen_lock.ConfirmPassCodeActivity
import com.intfocus.yhdev.util.*


class LauncherActivity : Activity() {

    val ctx = this
    private lateinit var mSettingSP: SharedPreferences
    private lateinit var mUserSP: SharedPreferences
    private lateinit var mAssetsSP: SharedPreferences
    private lateinit var mAssetsSPEdit: SharedPreferences.Editor
    private lateinit var packageInfo: PackageInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_splash)

        initData()

        checkAssets()
    }

    private fun initData() {

        mSettingSP = getSharedPreferences("SettingPreference", Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        mAssetsSP = getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE)
        try {
            packageInfo = packageManager.getPackageInfo(packageName, 0)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        mAssetsSPEdit = mAssetsSP.edit()

        if (mSettingSP.getInt("Version", 0) != packageInfo!!.versionCode) {
            mUserSP.edit().clear().commit()
            mSettingSP.edit().clear().commit()
        }
    }
//
//    private fun initAnim() {
//        val animation = AlphaAnimation(1f, 1.0f)
//        animation.duration = 2000
//        animation.setAnimationListener(this)
//        logo.startAnimation(animation)
//    }

    private fun enter() {
        val packageInfo = this.packageManager.getPackageInfo(this.packageName, 0)
        when {
            mSettingSP!!.getBoolean("ScreenLock", false) -> {
                intent = Intent(this, ConfirmPassCodeActivity::class.java)
                intent.putExtra("is_from_login", true)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

                finish()
            }
            mSettingSP!!.getInt("Version", 0) != packageInfo.versionCode -> {
                intent = Intent(this, GuideActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

                finish()
            }
            else -> {
                intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK and Intent.FLAG_ACTIVITY_SINGLE_TOP)
                this.startActivity(intent)

                finish()
            }
        }
    }

//    private fun checkAssetsIsUpdate(ctx: Context) {
//        if (!HttpUtil.isConnected(ctx)) {
//            return
//        }
//        RetrofitUtil.getHttpService(ctx).assetsMD5
//                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
//                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
//                    override fun onError(apiException: ApiException?) {
//
//                    }
//
//                    override fun onCompleted() {
//                    }
//
//                    override fun onBusinessNext(data: AssetsResult?) {
//                        var assetsMD5s = data!!.data!!
//                        /*
//                         * assets_md5 : ca94578b33a0d620de4caad6bba41fbd
//                         * loading_md5 : 8bd5c6a91d38848d3160e6c8a462b852
//                         * fonts_md5 : 5901960c857600316c3d141401c3af08
//                         * icons_md5git  : 7afa625cca643d01a6b12d80a19d4756
//                         * images_md5 : 65266455bea40469dcb9f022f63ce769
//                         * javascripts_md5 : 9a072008dfd547026c5828cd65d3e973
//                         * stylesheets_md5 : 4b5b98d9ad460a67e0943805e2be17c9
//                         * advertisement_md5 : 0239802a086466ec31d566ca910da0c9
//                         */
//
//                        mAssetsSPEdit.putString("loading_md5", assetsMD5s.loading_md5).commit()
//                        mAssetsSPEdit.putString("fonts_md5", assetsMD5s.fonts_md5).commit()
//                        mAssetsSPEdit.putString("images_md5", assetsMD5s.images_md5).commit()
//                        mAssetsSPEdit.putString("icons_md5", assetsMD5s.icons_md5).commit()
//                        mAssetsSPEdit.putString("javascripts_md5", assetsMD5s.javascripts_md5).commit()
//                        mAssetsSPEdit.putString("stylesheets_md5", assetsMD5s.stylesheets_md5).commit()
//                        HttpUtil.checkAssetsUpdated(ctx)
//                    }
//                })
//        FileUtil.CacheCleanAsync(applicationContext, "new-install").execute()
//
//    }

    private fun checkAssets() {
        if (!HttpUtil.isConnected(ctx))
            return
        // todo 请求服务器获取最新 MD5
        // todo 与本地 sp 中 MD5 比对， 有更新则下载，删除已存在的 assets 文件夹，再进行解压
        // todo 解压完成 跳转

        LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
        AssetsUpDateUtil.checkAssetsUpdate(ctx, object : OnCheckAssetsUpdateResultListener {
            override fun onResultSuccess() {
                LogUtil.d("hjjzz", "onResultSuccess:::" + Thread.currentThread().name)
                enter()
            }

            override fun onFailure() {
            }

        })
//        Observable.just(FileUtil.sharedPath(ctx))
//                .subscribeOn(Schedulers.io())
//                .map { path ->
//                    val isClean = CacheCleanManager.cleanCustomCache(path)
//                    /*  基本目录结构mSettingSP.getInt("Version", 0)
//                    */
//                    makeSureFolder(ctx, K.kSharedDirName)
//                    makeSureFolder(ctx, K.kCachedDirName)
//                    isClean
//                }
//                .flatMap(Func1<Boolean, Observable<String>> { isClean ->
//                    if (isClean!!) {
//                        val arr = arrayOf(URLs.kAssets, URLs.kLoading, URLs.kFonts, URLs.kImages, URLs.kIcons, URLs.kStylesheets, URLs.kJavaScripts)
//                        return@Func1 Observable.from(arr)
//                    }
//                    null
//                })
//                .map(Func1<String, Boolean> { assetsName ->
//                    if (assetsName == null)
//                        return@Func1 false
//                    if (URLs.kAssets.equals(assetsName))
//                        FileUtil.checkAssets(ctx, assetsName, false)
//                    else
//                        FileUtil.checkAssets(ctx, assetsName, true)
//                })
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(object : Observer<Boolean> {
//                    override fun onError(p0: Throwable?) {
//                        ToastUtils.show(ctx, p0!!.message!!)
//                    }
//
//                    override fun onCompleted() {
//                        checkAssetsUpdate()
//                    }
//
//                    override fun onNext(isCheckSuccess: Boolean?) {
//                        if (!isCheckSuccess!!)
//                            this.onError(kotlin.Throwable("解压出错"))
//                    }
//                })

    }

    private fun makeSureFolder(ctx: Context, folderName: String) {
        val cachedPath = String.format("%s/%s", FileUtil.basePath(ctx), folderName)
        FileUtil.makeSureFolderExist(cachedPath)
    }

    private fun checkAssetsUpdate() {
        RetrofitUtil.getHttpService(ctx).assetsMD5
                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onCompleted() {
                        enter()
                    }

                    override fun onBusinessNext(data: AssetsResult?) {
                        var assetsMD5s = data!!.data!!

                        /*
                         * assets_md5 : ca94578b33a0d620de4caad6bba41fbd
                         * loading_md5 : 8bd5c6a91d38848d3160e6c8a462b852
                         * fonts_md5 : 5901960c857600316c3d141401c3af08
                         * icons_md5git  : 7afa625cca643d01a6b12d80a19d4756
                         * images_md5 : 65266455bea40469dcb9f022f63ce769
                         * javascripts_md5 : 9a072008dfd547026c5828cd65d3e973
                         * stylesheets_md5 : 4b5b98d9ad460a67e0943805e2be17c9
                         * advertisement_md5 : 0239802a086466ec31d566ca910da0c9
                         */

                        mAssetsSPEdit.putString("loading_md5", assetsMD5s.loading_md5).commit()
                        mAssetsSPEdit.putString("fonts_md5", assetsMD5s.fonts_md5).commit()
                        mAssetsSPEdit.putString("images_md5", assetsMD5s.images_md5).commit()
                        mAssetsSPEdit.putString("icons_md5", assetsMD5s.icons_md5).commit()
                        mAssetsSPEdit.putString("javascripts_md5", assetsMD5s.javascripts_md5).commit()
                        mAssetsSPEdit.putString("stylesheets_md5", assetsMD5s.stylesheets_md5).commit()
                        HttpUtil.checkAssetsUpdated(ctx)
                    }
                })
    }
}
