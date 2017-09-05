package com.intfocus.yhdev.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.AsyncTask
import android.os.Environment
import android.view.View
import com.daimajia.numberprogressbar.NumberProgressBar
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
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
class DownLoadApkAsyncTask(val ctx: Context, private var numberProgress: NumberProgressBar, private val appName: String?) : AsyncTask<String, Int, String>() {
    override fun onPreExecute() {
        numberProgress.visibility = View.VISIBLE
        super.onPreExecute()
    }

    override fun doInBackground(vararg params: String): String? {
        var ips: InputStream? = null
        var fops: FileOutputStream? = null
        val filePath = Environment.getExternalStorageDirectory().path
        try {
            fops = FileOutputStream(File(filePath, appName + ".apk"))
            val url = URL(params[0])
            val conn = url.openConnection()
            conn.connect()
            ips = conn.getInputStream()
            val fileLength = conn.contentLength
            var len = 0
            var total_length = 0
            val data = ByteArray(4096)
            while (true) {
                len = ips.read(data)
                if (len == -1) {
                    break
                }
                total_length += len
                fops.write(data, 0, len)
                publishProgress((total_length / fileLength))
            }

        } catch (e: Exception) {

        } finally {

        }

        return filePath
    }

    override fun onProgressUpdate(vararg values: Int?) {
        numberProgress.progress = values[0]!!
        super.onProgressUpdate(*values)
    }

    override fun onPostExecute(filePath: String) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.setDataAndType(Uri.fromFile(File(filePath, appName + ".apk")), "application/vnd.android.package-archive")
        ctx.startActivity(intent)
        super.onPostExecute(filePath)
    }
}