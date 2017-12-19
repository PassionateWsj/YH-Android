package com.intfocus.template.subject.seven

import com.alibaba.fastjson.JSON
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.util.LoadAssetsJsonUtil
import rx.Subscription

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:28
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class MyAttentionModelImpl : MyAttentionModel {
    companion object {
        private val TAG = "MyAttentionModelImpl"
        private var INSTANCE: MyAttentionModelImpl? = null
        private var observable: Subscription? = null
        private var uuid: String = ""

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): MyAttentionModelImpl {
            return INSTANCE ?: MyAttentionModelImpl()
                    .apply { INSTANCE = this }
        }

        /**
         * Used to force [getInstance] to create a new instance
         * next time it's called.
         */
        @JvmStatic
        fun destroyInstance() {
            unSubscribe()
            INSTANCE = null
        }

        /**
         * 取消订阅
         */
        private fun unSubscribe() {
            observable?.unsubscribe() ?: return
        }
    }

    override fun getData(user_num: String,callback: MyAttentionModel.LoadDataCallback) {
        val assetsJsonData = LoadAssetsJsonUtil.getAssetsJsonData("template7_main_attention_data.json")
        val data = JSON.parseObject(assetsJsonData, Test2::class.java)
        callback.onDataLoaded(data,data.data.filter)
    }
}