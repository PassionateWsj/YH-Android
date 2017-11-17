package com.intfocus.yhdev.module.image

import com.alibaba.fastjson.JSONObject
import com.intfocus.yhdev.general.gen.SourceDao
import com.intfocus.yhdev.general.util.DaoUtil
import com.intfocus.yhdev.collection.CollectionModelImpl.Companion.uuid
import com.intfocus.yhdev.collection.callback.LoadDataCallback
import com.intfocus.yhdev.module.ModuleModel

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class ImageModelImpl : ModuleModel<ImageEntity> {

    companion object {
        private var INSTANCE: ImageModelImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): ImageModelImpl {
            return INSTANCE ?: ImageModelImpl()
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

    override fun analyseData(params: String, callback: LoadDataCallback<ImageEntity>) {
        var data = JSONObject.parseObject(params, ImageEntity::class.java)
        callback.onSuccess(data)
    }

    override fun insertDb(value: String, key: String) {
        var sourceDao = DaoUtil.getDaoSession()!!.sourceDao
        var collectionQb = sourceDao.queryBuilder()
        var collection = collectionQb.where(collectionQb.and(SourceDao.Properties.Key.eq(key), SourceDao.Properties.Uuid.eq(uuid))).unique()
        if (null != collection) {
            collection.value = value
            sourceDao.update(collection)
        }
    }
}
