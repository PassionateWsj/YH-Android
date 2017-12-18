package com.intfocus.template.util

import com.intfocus.template.SYPApplication
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/15 上午11:51
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
object LoadAssetsJsonUtil {
    /**
     * 加载本地 assets 文件夹内 json 测试数据的方法
     * @param assetsName 本地 Json 数据文件名
     * @return
     */
    fun getAssetsJsonData(assetsName: String): String {
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var sb: StringBuilder? = null
        try {
            inputStream = SYPApplication.globalContext.resources.assets.open(assetsName)
            reader = BufferedReader(InputStreamReader(inputStream!!))
            sb = StringBuilder()
            var line: String?
            while (true) {
                line = reader.readLine()
                if (line != null) sb.append(line + "\n") else break

            }
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                if (reader != null) {
                    reader.close()
                }
                if (inputStream != null) {
                    inputStream.close()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return sb!!.toString()
    }
}