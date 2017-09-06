package com.intfocus.yhdev.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.view.View
import com.daimajia.numberprogressbar.NumberProgressBar
import com.intfocus.yhdev.R
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL


/**
 * ****************************************************
 * author: jameswong
 * created on: 17/09/05 下午3:13
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class DownLoadApkAsyncTask(val ctx: Context, private var numberProgress: NumberProgressBar) : AsyncTask<String, Int, String>() {
    override fun onPreExecute() {
        numberProgress.visibility = View.VISIBLE
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String? {
        val url: URL
        val conn: HttpURLConnection
        var bis: BufferedInputStream? = null
        var fos: FileOutputStream? = null
        val filePath = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).path
        try {
            url = URL(params[0])
            conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "GET"
            conn.connectTimeout = 5000
            val fileLength = conn.contentLength
            bis = BufferedInputStream(conn.inputStream)
            val file = File(filePath, ctx.resources.getString(R.string.app_name) + ".apk.download")
            if (!file.exists()) {
                if (!file.parentFile.exists()) {
                    file.parentFile.mkdirs()
                }
                file.createNewFile()
            }
            file.delete()
            fos = FileOutputStream(file)
            val data = ByteArray(4 * 1024)
            var total: Long = 0
            var count: Int
            while (true) {
                count = bis.read(data)
                if (count == -1) {
                    break
                }
                total += count.toLong()
                publishProgress((total * 100 / fileLength).toInt())
                fos.write(data, 0, count)
                fos.flush()
            }
            fos.flush()

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (fos != null) {
                    fos.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

            try {
                if (bis != null) {
                    bis.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }

        }
        return filePath
    }

    override fun onProgressUpdate(vararg values: Int?) {
        numberProgress.progress = values[0]!!
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(filePath: String) {
        val file = File(filePath, ctx.resources.getString(R.string.app_name) + ".apk.download")
        if (file.exists()) {
            file.renameTo(File(filePath, ctx.resources.getString(R.string.app_name) + ".apk"))
        }
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(filePath, ctx.resources.getString(R.string.app_name) + ".apk")), "application/vnd.android.package-archive")
        ctx.startActivity(intent)
        val activity = ctx as Activity
        activity.finish()
        super.onPostExecute(filePath)
    }
}
