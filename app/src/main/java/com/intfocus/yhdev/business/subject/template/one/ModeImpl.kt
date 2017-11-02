package com.intfocus.yhdev.business.subject.template.one

import android.content.Context
import android.util.Log
import com.alibaba.fastjson.JSONReader
import com.intfocus.yhdev.business.subject.templateone.entity.MererDetailEntity
import com.intfocus.yhdev.general.util.ApiHelper
import com.intfocus.yhdev.general.util.FileUtil
import com.intfocus.yhdev.general.util.K
import com.zbl.lib.baseframe.utils.TimeUtil
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.io.*
import java.util.*

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class ModeImpl : ModeModel {

    companion object {
        private val TAG = "ModeImpl"

        private var INSTANCE: ModeImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ModeImpl {
            return INSTANCE ?: ModeImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            INSTANCE = null
        }
    }

    override fun getData(ctx: Context, groupId: String, reportId: String, callback: ModeModel.LoadDataCallback) {
        val jsonFileName = String.format("group_%s_template_%s_report_%s.json", groupId, "1", reportId)

        Observable.just(jsonFileName)
                .subscribeOn(Schedulers.io())
                .map {
                    val response: String?
                    val jsonFilePath = FileUtil.dirPath(ctx, K.kCachedDirName, it)
                    val dataState = ApiHelper.reportJsonData(ctx, groupId, "1", reportId)
                    if (dataState || File(jsonFilePath).exists()) {
                        response = FileUtil.readFile(jsonFilePath)
                    } else {
                        throw Throwable("获取数据失败")
                    }
//                    response = getJsonData(ctx)
                    Log.i(TAG, "analysisDataStartTime:" + TimeUtil.getNowTime())
                    val stringReader = StringReader(response)
                    Log.i(TAG, "analysisDataReaderTime1:" + TimeUtil.getNowTime())
                    val reader = JSONReader(stringReader)
                    reader.startArray()
                    reader.startObject()
                    val entity = MererDetailEntity()
                    entity.data = ArrayList()
                    Log.i(TAG, "analysisDataReaderTime2:" + TimeUtil.getNowTime())

                    while (reader.hasNext()) {
                        val key = reader.readString()
                        when (key) {
                            "name" -> {
                                entity.name = reader.readObject().toString()
                                Log.i(TAG, "name:" + TimeUtil.getNowTime())
                            }

                            "data" -> {
                                Log.i(TAG, "dataStart:" + TimeUtil.getNowTime())
                                reader.startArray()

                                while (reader.hasNext()) {
                                    reader.startObject()
                                    val data = MererDetailEntity.PageData()
                                    while (reader.hasNext()) {
                                        val dataKey = reader.readString()
                                        when (dataKey) {
                                            "parts" -> data.parts = reader.readObject().toString()

                                            "title" -> data.title = reader.readObject().toString()

                                        }
                                    }
                                    reader.endObject()
                                    entity.data!!.add(data)
                                }
                                reader.endArray()
                                Log.i(TAG, "dataEnd:" + TimeUtil.getNowTime())
                            }
                        }
                    }
                    reader.endObject()
                    reader.endArray()
                    Log.i(TAG, "analysisDataEndTime:" + TimeUtil.getNowTime())
                    entity
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Subscriber<MererDetailEntity>() {
                    override fun onCompleted() {
                    }

                    override fun onNext(t: MererDetailEntity?) {
//                        t ?:callback.onDataLoaded(t)
                        t?.let { callback.onDataLoaded(it) }

                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e!!)
                    }

                })


    }

    /**
     * 加载 模板一 本地 json 测试数据的方法
     * @param context
     * @return
     */
    private fun getJsonData(context: Context): String {
        var inputStream: InputStream? = null
        var reader: BufferedReader? = null
        var sb: StringBuilder? = null
        try {
            inputStream = context.resources.assets.open("kpi_detaldata.json")
            //            is = context.getResources().getAssets().open("temple-v1.json");
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