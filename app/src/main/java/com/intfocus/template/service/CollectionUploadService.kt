package com.intfocus.template.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.intfocus.template.BuildConfig
import com.intfocus.template.SYPApplication.globalContext
import com.intfocus.template.constant.Module.UPLOAD_IMAGES
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.model.gen.CollectionDao
import com.intfocus.template.model.gen.SourceDao
import com.intfocus.template.subject.nine.entity.CollectionRequestBody
import com.intfocus.template.util.K
import com.intfocus.template.util.URLs
import com.intfocus.template.util.Utils
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe 数据采集信息上传服务
 */
class CollectionUploadService : IntentService("collection_upload") {
    private lateinit var collectionDao: CollectionDao

    /*
     * 采集数据列表
     */
    private lateinit var collectionList: List<Collection>
    private lateinit var collection: Collection
    private lateinit var sourceDao: SourceDao

    /*
     * 当前采集报表的唯一标识
     */
    private lateinit var uuid: String
    private lateinit var reportId: String

    override fun onHandleIntent(p0: Intent?) {
        collectionDao = DaoUtil.getCollectionDao()
        collectionList = collectionDao.loadAll()
        sourceDao = DaoUtil.getSourceDao()
        collectionList
                .filter { 1 != it.status }
                .forEach {
                    collection = it
                    uuid = it.uuid
                    reportId = it.reportId
                    if (1 != it.imageStatus) {
                        uploadImage()
                    } else {
                        generateDJson()
                    }
                }
    }

    /**
     * 上传图片
     */
    private fun uploadImage() {
        var sourceQb = sourceDao.queryBuilder()
        var sourceList = sourceQb.where(sourceQb.and(SourceDao.Properties.Type.eq(UPLOAD_IMAGES), SourceDao.Properties.Uuid.eq(uuid))).list()


        /*
         * 如果采集数据中不包含图片, 直接生成 D_JSON
         */
        if (sourceList.size < 1) {
            collection.imageStatus = 1
            collectionDao.update(collection)

            generateDJson()
            return
        }

        for (source in sourceList) {
            var fileList: MutableList<File> = arrayListOf()

            if (source.value.isEmpty()) {
                collection.imageStatus = 1
                collectionDao.update(collection)

                generateDJson()
                return
            }

            (Utils.stringToList(source.value)).mapTo(fileList) { File(it) }

            val mOkHttpClient = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("api_token", URLs.MD5(K.ANDROID_API_KEY + K.API_IMAGE_UPLOAD + K.ANDROID_API_KEY))
                    .addFormDataPart("module_name", reportId)

            if (!fileList.isEmpty()) {
                for ((i, file) in fileList.withIndex()) {
                    if (null != file.isFile) {
                        requestBody.addFormDataPart("image" + i, file.name, RequestBody.create(MediaType.parse("image/*"), file))
                    }
                }
            }

            val request = Request.Builder()
                    .url(BuildConfig.BASE_URL + K.API_IMAGE_UPLOAD)
                    .post(requestBody.build())
                    .build()

            var result = mOkHttpClient.newCall(request).execute()

            if (!result.isSuccessful) {
                return
            }

            var responseData = result.body()!!.string()
            var valueArray = JSONObject(responseData)["data"] as JSONArray

            source.value = Gson().toJson(valueArray)
            sourceDao.update(source)
        }

        collection.imageStatus = 1
        collectionDao.update(collection)

        generateDJson()
    }


    /**
     * 生成需要上传给服务的 D_JSON (采集结果)
     */
    private fun generateDJson() {
        var moduleList = sourceDao.queryBuilder().where(SourceDao.Properties.Uuid.eq(uuid)).list()
        var dJson = JSONObject()

        for (module in moduleList) {
            dJson.put(module.key, module.value)
        }

        uploadDJSon(Gson().toJson(dJson))
    }

    /**
     * 上传 D_JSON 至服务器
     */
    private fun uploadDJSon(dJson: String) {
        var collectionRequestBody = CollectionRequestBody()
        var data = CollectionRequestBody.Data()

        data.user_num = globalContext.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString("user_num", "")
        data.report_id = reportId
        data.content = dJson

        collectionRequestBody.data = data

        var result = RetrofitUtil.getHttpService(globalContext).submitCollection(collectionRequestBody).execute()

        if (result.isSuccessful) {
            collection.status = 1
            collection.dJson = dJson
            collectionDao.update(collection)
        }
    }
}
