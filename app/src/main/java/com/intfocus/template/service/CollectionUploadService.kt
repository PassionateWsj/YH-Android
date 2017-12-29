package com.intfocus.template.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import com.google.gson.Gson
import com.intfocus.template.BuildConfig
import com.intfocus.template.SYPApplication.globalContext
import com.intfocus.template.constant.Module.UPLOAD_IMAGES
import com.intfocus.template.constant.Params.USER_BEAN
import com.intfocus.template.constant.Params.USER_NUM
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
    private lateinit var sourceDao: SourceDao

    override fun onHandleIntent(p0: Intent?) {
        collectionDao = DaoUtil.getCollectionDao()
        val collectionList = collectionDao.loadAll()
        sourceDao = DaoUtil.getSourceDao()
        collectionList
                .filter { 0 == it.status }
                .forEach {
                    if (1 != it.imageStatus) {
                        uploadImage(it)
                    } else {
                        generateDJson(it)
                    }
                }
    }

    /**
     * 上传图片
     */
    private fun uploadImage(collection: Collection) {
        val uuid = collection.uuid
        val reportId = collection.reportId
        val sourceQb = sourceDao.queryBuilder()
        val sourceList = sourceQb.where(sourceQb.and(SourceDao.Properties.Type.eq(UPLOAD_IMAGES), SourceDao.Properties.Uuid.eq(uuid))).list()


        // 如果采集数据中不包含图片, 直接生成 D_JSON
        if (sourceList.size < 1) {
            collection.imageStatus = 1
            collectionDao.update(collection)

            generateDJson(collection)
            return
        }

        for (source in sourceList) {
            val fileList: MutableList<File> = arrayListOf()

            if (source.value.isEmpty()) {
                collection.imageStatus = 1
                collectionDao.update(collection)

                generateDJson(collection)
                return
            }

            (Utils.stringToList(source.value)).mapTo(fileList) { File(it) }

            val mOkHttpClient = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("api_token", URLs.MD5(K.ANDROID_API_KEY + K.API_IMAGE_UPLOAD + K.ANDROID_API_KEY))
                    .addFormDataPart("module_name", reportId)

            if (!fileList.isEmpty()) {
                fileList.forEachIndexed { index, itemFile ->
                    if (itemFile.exists()) {
                        requestBody.addFormDataPart("image" + index, itemFile.name, RequestBody.create(MediaType.parse("image/*"), itemFile))
                    }
                }
            }

            val request = Request.Builder()
                    .url(BuildConfig.BASE_URL + K.API_IMAGE_UPLOAD)
                    .post(requestBody.build())
                    .build()

            val result = mOkHttpClient.newCall(request).execute()

            if (!result.isSuccessful) {
                return
            }

            val responseData = result.body()!!.string()
            val valueArray = JSONObject(responseData)["data"] as JSONArray

            source.value = Gson().toJson(valueArray)
            sourceDao.update(source)
        }

        collection.imageStatus = 1
        collectionDao.update(collection)

        generateDJson(collection)
    }

    /**
     * 生成需要上传给服务的 D_JSON (采集结果)
     */
    private fun generateDJson(collection: Collection) {
        val moduleList = sourceDao.queryBuilder().where(SourceDao.Properties.Uuid.eq(collection.uuid)).list()
        val dJson = JSONObject()

        for (module in moduleList) {
            dJson.put(module.key, module.value)
        }

        uploadDJSon(Gson().toJson(dJson), collection)
    }

    /**
     * 上传 D_JSON 至服务器
     */
    private fun uploadDJSon(dJson: String, collection: Collection) {
        val collectionRequestBody = CollectionRequestBody()
        val data = CollectionRequestBody.Data()

        data.user_num = getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE).getString(USER_NUM, "")
        data.report_id = collection.reportId
        data.content = dJson

        collectionRequestBody.data = data

        val result = RetrofitUtil.getHttpService(globalContext).submitCollection(collectionRequestBody).execute()

        if (result.isSuccessful) {
            collection.status = 1
            collection.dJson = dJson
            collectionDao.update(collection)
        }
    }
}
