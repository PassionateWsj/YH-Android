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
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import com.intfocus.yhdev.R
import com.intfocus.yhdev.data.response.assets.AssetsResult
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.RetrofitUtil
import com.intfocus.yhdev.screen_lock.ConfirmPassCodeActivity
import com.intfocus.yhdev.util.FileUtil
import com.intfocus.yhdev.util.HttpUtil
import kotlinx.android.synthetic.main.activity_splash.*


class LauncherActivity : Activity(), Animation.AnimationListener {

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

        initAnim()
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
    }

    private fun initAnim() {
        val animation = AlphaAnimation(1f, 1.0f)
        animation.duration = 2000
        animation.setAnimationListener(this)
        logo.startAnimation(animation)
    }

    override fun onAnimationRepeat(p0: Animation?) {
    }

    override fun onAnimationEnd(p0: Animation?) {
        enter()
    }

    override fun onAnimationStart(p0: Animation?) {
        checkAssetsIsUpdate(ctx)
    }

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

    private fun checkAssetsIsUpdate(ctx: Context) {
        if (!HttpUtil.isConnected(ctx)) {
            return
        }
        RetrofitUtil.getHttpService(ctx).assetsMD5
                .compose(RetrofitUtil.CommonOptions<AssetsResult>())
                .subscribe(object : CodeHandledSubscriber<AssetsResult>() {
                    override fun onError(apiException: ApiException?) {

                    }

                    override fun onCompleted() {
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
        FileUtil.CacheCleanAsync(applicationContext, "new-install").execute()

    }

//    private fun checkAssetsIsUpdate(ctx: Context) {
//        if (!HttpUtil.isConnected(ctx)) {
//            return
//        }
//        if (mSettingSP.getInt("Version", 0) != packageInfo.versionCode) {
//            mUserSP.edit().clear().commit()
//            mSettingSP.edit().clear().commit()
//            // todo 删除旧资源文件夹
//            LogUtil.d("hjjzz", "MainThread:::" + Thread.currentThread().name)
//            Observable.just(FileUtil.sharedPath(ctx), FileUtil.userspace(ctx))
//                    .map { path-> CacheCleanManager.cleanCustomCache(path)}
//                    .map { isClean->
//                        if (isClean) {
//
//                        }}
//                    .subscribeOn(Schedulers.io())
//                    .subscribe(object : Observer<String> {
//                        override fun onNext(path: String?) {
//
//                        }
//
//                        override fun onError(p0: Throwable?) {
//                        }
//
//                        override fun onCompleted() {
////                            val sharedPath = FileUtil.sharedPath(ctx)
//
//                            /*
//             *  基本目录结构mSettingSP.getInt("Version", 0)
//             */
//                            makeSureFolder(ctx, K.kSharedDirName)
//                            makeSureFolder(ctx, K.kCachedDirName)
//
//                            /*
//             *  新安装、或升级后，把代码包中的静态资源重新拷贝覆盖一下
//             *  避免再从服务器下载更新，浪费用户流量
//             */
////                            copyAssetFiles(ctx, sharedPath)
//                            LogUtil.d("hjjzz", "checkAssetsThread:::" + Thread.currentThread().name)
//                            var arr = listOf(URLs.kAssets, URLs.kLoading, URLs.kFonts, URLs.kImages, URLs.kIcons, URLs.kStylesheets, URLs.kJavaScripts)
//                            Observable.from(arr)
//                                    .subscribeOn(AndroidSchedulers.mainThread())
//                                    .observeOn(Schedulers.io())
//                                    .subscribe {
//                                        object : Observer<String> {
//                                            override fun onNext(assetsName: String?) {
//                                                if (URLs.kAssets == assetsName)
//                                                    FileUtil.checkAssets(ctx, assetsName, false)
//                                                else
//                                                    FileUtil.checkAssets(ctx, assetsName, true)
//                                            }
//
//                                            override fun onError(e: Throwable?) {
//                                            }
//
//                                            override fun onCompleted() {
//
//                                            }
//                                        }
//
//
//                                    }
//                        }
//                    })
//
//            // todo 获取本地 zip md5 存储到 sp 中
//        }
//        // todo 请求服务器获取 最新静态资源 MD5
//
//    }

    fun makeSureFolder(ctx: Context, folderName: String) {
        val cachedPath = String.format("%s/%s", FileUtil.basePath(ctx), folderName)
        FileUtil.makeSureFolderExist(cachedPath)
    }
}
