package com.intfucos.yhdev.module.options

import com.alibaba.fastjson.JSONObject
import com.intfocus.syp_template.general.gen.SourceDao
import com.intfocus.syp_template.general.util.DaoUtil
import com.intfucos.yhdev.collection.CollectionModelImpl
import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.module.ModuleModel

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class OptionsModelImpl: ModuleModel<OptionsEntity> {
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
        var data = JSONObject.parseObject(params, OptionsEntity::class.java)
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
