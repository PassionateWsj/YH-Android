package com.intfocus.yhdev.general.service

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.gson.Gson
import com.intfocus.yhdev.YHApplication.globalContext
import com.intfocus.yhdev.business.collection.entity.CollectionRequestBody
import com.intfocus.yhdev.business.login.bean.Device
import com.intfocus.yhdev.general.bean.Collection
import com.intfocus.yhdev.general.constant.Module.UPLOAD_IMAGES
import com.intfocus.yhdev.general.data.response.BaseResult
import com.intfocus.yhdev.general.gen.CollectionDao
import com.intfocus.yhdev.general.gen.SourceDao
import com.intfocus.yhdev.general.net.ApiException
import com.intfocus.yhdev.general.net.CodeHandledSubscriber
import com.intfocus.yhdev.general.net.RetrofitUtil
import com.intfocus.yhdev.general.util.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe
 */
class CollectionUploadService : IntentService("collection_upload") {
    private lateinit var collectionDao: CollectionDao
    private lateinit var collectionList: List<Collection>
    private lateinit var collection: Collection
    private lateinit var sourceDao: SourceDao
    private lateinit var uuid: String
    private lateinit var acquisitionId: String

    override fun onHandleIntent(p0: Intent?) {
        collectionDao = DaoUtil.getCollectionDao()
        collectionList = collectionDao.loadAll()
        sourceDao = DaoUtil.getSourceDao()
        collectionList
                .filter { 1 != it.status }
                .forEach {
                    collection = it
                    uuid = it.uuid
                    acquisitionId = uuid
                    if (1 != it.image_status) {
                        uploadImage()
                    } else {
                        generateDJson()
                    }
                }
    }

    private fun uploadImage() {
        var sourceQb = sourceDao.queryBuilder()
        var sourceList = sourceQb.where(sourceQb.and(SourceDao.Properties.Type.eq(UPLOAD_IMAGES), SourceDao.Properties.Uuid.eq(uuid))).list()

        if (sourceList.size <1) {
            collection.image_status = 1
            collectionDao.update(collection)

            generateDJson()
            return
        }

        for (source in sourceList) {
            var fileList: MutableList<File> = arrayListOf()

            (Utils.stringToList(source.value)).mapTo(fileList) { File(it) }

            val mOkHttpClient = OkHttpClient()

            val requestBody = MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("api_token", URLs.MD5(K.ANDROID_API_KEY + K.KUploadImage + K.ANDROID_API_KEY))
                    .addFormDataPart("module_name", "问题反馈")

            if (!fileList.isEmpty()) {
                for ((i, file) in fileList.withIndex()) {
                    requestBody.addFormDataPart("image" + i, file.name, RequestBody.create(MediaType.parse("image/*"), file))
                }
            }

            val request = Request.Builder()
                    .url(K.kBaseUrl + K.KUploadImage)
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

        collection.image_status = 1
        collectionDao.update(collection)

        generateDJson()
    }

    private fun generateDJson() {
        var moduleList = sourceDao.queryBuilder().where(SourceDao.Properties.Uuid.eq(uuid)).list()
        var dJson = JSONObject()

        for (module in moduleList) {
            dJson.put(module.key, module.value)
        }

        uploadDJSon(dJson)
    }

    private fun uploadDJSon(dJson: JSONObject) {
        var collectionRequestBody = CollectionRequestBody()
        var data = CollectionRequestBody.Data()

        data.user_num = globalContext.getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString("user_num", "")
        data.acquisition_id = acquisitionId
        data.content = dJson

        collectionRequestBody.data = data

        var result = RetrofitUtil.getHttpService(globalContext).submitCollection2(collectionRequestBody).execute()

        if (200 == result.code()) {
            collection.status = 1
            collection.dJson = Gson().toJson(dJson)
            collectionDao.update(collection)
        }
    }
}
