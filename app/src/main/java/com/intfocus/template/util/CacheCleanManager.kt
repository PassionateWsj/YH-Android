package com.intfocus.template.util

import android.app.ProgressDialog
import android.content.Context
import android.os.Environment
import android.view.View
import com.intfocus.template.constant.ToastColor
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

import java.io.File

/**
 * @author liuruilin
 * @date 2017/7/12
 */

object CacheCleanManager {


    /**
     * 清理缓存
     */
    fun clearAppUserCache(context: Context) {
        val mProgressDialog = ProgressDialog.show(context, "稍等", "正在清理缓存...")
        if (!HttpUtil.isConnected(context)) {
            mProgressDialog.dismiss()
            ToastUtils.show(context, "清除缓存失败，请检查网络")
            return
        }

        /*
         * 清除用户缓存
         */
        ApiHelper.clearUserSpace()

        val sharedPath = FileUtil.sharedPath(context)
        val cachePath = String.format("%s/%s", FileUtil.basePath(context), K.K_CACHED_DIR_NAME)
        Observable.just(sharedPath)
                .subscribeOn(Schedulers.io())
                .map { path ->
                    val isClearSpSuccess = context.getSharedPreferences("AssetsMD5", Context.MODE_PRIVATE).edit().clear().commit()
                    val isCleanSharedPathSuccess = FileUtil.deleteDirectory(path)
                    val isCleanCacheSuccess = FileUtil.deleteDirectory(cachePath)
                    isClearSpSuccess && isCleanSharedPathSuccess && isCleanCacheSuccess
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { isClear ->
                    if (isClear) {
                        AssetsUpDateUtil.checkFirstSetup(context, object : OnCheckAssetsUpdateResultListener {
                            override fun onResultSuccess() {
                                AssetsUpDateUtil.checkAssetsUpdate(context, object : OnCheckAssetsUpdateResultListener {
                                    override fun onResultSuccess() {
                                        ToastUtils.show(context, "清除缓存成功", ToastColor.SUCCESS)
                                        mProgressDialog.dismiss()
                                    }

                                    override fun onFailure(errorMsg: Throwable) {
                                        AssetsUpDateUtil.unSubscribe()
                                        ToastUtils.show(context, "清除缓存失败，请重试")
                                        mProgressDialog.dismiss()
                                    }
                                })
                            }

                            override fun onFailure(errorMsg: Throwable) {
                                mProgressDialog.dismiss()
                                ToastUtils.show(context, "清除缓存失败，请检查网络")
                            }
                        })
                    } else {
                        mProgressDialog.dismiss()
                        ToastUtils.show(context, "清除缓存失败，请检查网络")
                    }
                }


    }

    /**
     * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context
     */
    fun cleanInternalCache(context: Context) {
        deleteFilesByDirectory(context.cacheDir)
    }

    /**
     * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context
     */
    fun cleanDatabases(context: Context) {
        deleteFilesByDirectory(File("/data/data/"
                + context.packageName + "/databases"))
    }

    /**
     * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
     * context
     */
    fun cleanSharedPreference(context: Context) {
        deleteFilesByDirectory(File("/data/data/"
                + context.packageName + "/shared_prefs"))
    }

    /**
     * 按名字清除本应用数据库 * * @param context * @param dbName
     */
    fun cleanDatabaseByName(context: Context, dbName: String) {
        context.deleteDatabase(dbName)
    }

    /**
     * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context
     */
    fun cleanFiles(context: Context) {
        deleteFilesByDirectory(context.filesDir)
    }

    /**
     * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
     * context
     */
    fun cleanExternalCache(context: Context) {
        if (Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED) {
            deleteFilesByDirectory(context.externalCacheDir)
        }
    }

    /**
     * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除
     *
     * @param filePath
     */
    fun cleanCustomCache(filePath: String): Boolean {
        return deleteFilesByDirectory(File(filePath))
    }

    /**
     * 清除本应用所有的数据
     *
     * @param context
     */
    fun cleanApplicationData(context: Context) {
        cleanInternalCache(context)
        cleanExternalCache(context)
        cleanDatabases(context)
        cleanSharedPreference(context)
        cleanFiles(context)
    }

    /**
     * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory
     */
    private fun deleteFilesByDirectory(directory: File?): Boolean {
        if (directory != null && directory.exists() && directory.isDirectory) {
            for (item in directory.listFiles()) {
                if (!item.delete()) {
                    return false
                }
            }
        }
        return true
    }

    /**
     * 递归删除文件和文件夹
     *
     * @param directoryPath 要删除的根目录
     */
    fun deleteDirectory(directoryPath: String) {
        val file = File(directoryPath)

        if (file.isFile) {
            file.delete()
            return
        }
        if (file.isDirectory) {
            val childFile = file.listFiles()
            if (childFile == null || childFile.size == 0) {
                file.delete()
                return
            }
            for (f in childFile) {
                deleteDirectory(f.path)
            }
            file.delete()
        }
    }
}
