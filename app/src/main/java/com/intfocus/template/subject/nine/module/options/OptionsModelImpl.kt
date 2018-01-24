package com.intfocus.template.subject.nine.module.options

import com.alibaba.fastjson.JSONObject
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.gen.SourceDao
import com.intfocus.template.subject.nine.CollectionModelImpl
import com.intfocus.template.subject.nine.module.ModuleModel

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class OptionsModelImpl : ModuleModel<OptionsEntity> {
    companion object {
        private var INSTANCE: OptionsModelImpl? = null
        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): OptionsModelImpl {
            return INSTANCE ?: OptionsModelImpl()
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

    override fun analyseData(params: String, callback: LoadDataCallback<OptionsEntity>) {
        val data = JSONObject.parseObject(params, OptionsEntity::class.java)
        callback.onSuccess(data)
    }

    override fun insertDb(value: String, key: String, listItemType: Int) {
        val sourceDao = DaoUtil.getDaoSession()!!.sourceDao
        val collectionQb = sourceDao.queryBuilder()
        val collection = collectionQb.where(collectionQb.and(SourceDao.Properties.Key.eq(key), SourceDao.Properties.Uuid.eq(CollectionModelImpl.uuid))).unique()
        if (null != collection) {
            collection.value = value
            sourceDao.update(collection)
            if (listItemType != 0) {
                CollectionModelImpl.getInstance().updateCollectionData(listItemType, value)
            }
        }
    }
}
