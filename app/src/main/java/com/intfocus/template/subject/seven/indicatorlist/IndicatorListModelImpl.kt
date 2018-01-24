package com.intfocus.template.subject.seven.indicatorlist

import rx.Subscription

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:49
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class IndicatorListModelImpl: IndicatorListModel {

    /**
     * 默认只有一个页签
     */
    private val pageId = 0

    companion object {
        private val TAG = "IndicatorListModelImpl"
        private var INSTANCE: IndicatorListModelImpl? = null
        private var observable: Subscription? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): IndicatorListModelImpl {
            return INSTANCE ?: IndicatorListModelImpl()
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
    override fun getConcernedListByUser() {
    }

}