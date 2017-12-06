package com.intfocus.template.subject.nine.module.text

import com.alibaba.fastjson.JSONObject
import com.intfocus.template.model.gen.SourceDao
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.subject.nine.CollectionModelImpl
import com.intfocus.template.subject.nine.callback.LoadDataCallback
import com.intfocus.template.subject.nine.module.ModuleModel

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
class TextModelImpl : ModuleModel<TextEntity> {
    companion object {

        private var INSTANCE: TextModelImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): TextModelImpl {
            return INSTANCE ?: TextModelImpl()
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

    override fun analyseData(params: String, callback: LoadDataCallback<TextEntity>) {
        var data = JSONObject.parseObject(params, TextEntity::class.java)
        callback.onSuccess(data)
    }

    override fun insertDb(value: String, key: String) {
        var sourceDao = DaoUtil.getDaoSession()!!.sourceDao
        var collectionQb = sourceDao.queryBuilder()
        var collection = collectionQb.where(collectionQb.and(SourceDao.Properties.Key.eq(key), SourceDao.Properties.Uuid.eq(CollectionModelImpl.uuid))).unique()
        if (null != collection) {
            collection.value = value
            sourceDao.update(collection)
        }
    }
}
