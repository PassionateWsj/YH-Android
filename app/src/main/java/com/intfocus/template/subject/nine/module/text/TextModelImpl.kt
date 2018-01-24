package com.intfocus.template.subject.nine.module.text

import com.alibaba.fastjson.JSONObject
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.gen.SourceDao
import com.intfocus.template.subject.nine.CollectionModelImpl
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
        val data = JSONObject.parseObject(params, TextEntity::class.java)
        callback.onSuccess(data)
    }

    override fun insertDb(value: String, key: String, listItemType: Int) {
        val sourceDao = DaoUtil.getDaoSession()!!.sourceDao
        val sourceQb = sourceDao.queryBuilder()
        val sourceItem = sourceQb.where(sourceQb.and(SourceDao.Properties.Key.eq(key), SourceDao.Properties.Uuid.eq(CollectionModelImpl.uuid))).unique()
        if (null != sourceItem) {
            sourceItem.value = value
            sourceDao.update(sourceItem)
            if (listItemType != 0) {
                CollectionModelImpl.getInstance().updateCollectionData(listItemType, value)
            }
        }
    }
}
