package com.intfocus.syp_template.business.subject.template.one.table

import com.alibaba.fastjson.JSON
import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity
import rx.Observable
import rx.Observer
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:35
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class TableImpl : TableModel {

    companion object {

        private var INSTANCE: TableImpl? = null

        /**
         * Returns the single instance of this class, creating it if necessary.
         */
        @JvmStatic
        fun getInstance(): TableImpl {
            return INSTANCE ?: TableImpl()
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

    override fun getData(dataJson: String, callback: TableModel.LoadDataCallback) {

        Observable.just(dataJson)
                .subscribeOn(Schedulers.io())
                .map { JSON.parseObject<ModularTwo_UnitTableEntity>(it, ModularTwo_UnitTableEntity::class.java) }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(object : Observer<ModularTwo_UnitTableEntity> {
                    override fun onNext(t: ModularTwo_UnitTableEntity?) {
                        t?.let { callback.onDataLoaded(t) }
                    }

                    override fun onError(e: Throwable?) {
                        callback.onDataNotAvailable(e)
                    }

                    override fun onCompleted() {
                    }
                })
    }
}