package com.intfucos.yhdev.collection

import android.content.Context
import android.util.Log
import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.collection.entity.CollectionEntity

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/25 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CollectionPresenter(
        private val mModel: CollectionModelImpl,
        private val mView: CollectionContract.View
) : CollectionContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(id: String) {
        mModel.getData(id,object : LoadDataCallback<CollectionEntity> {
            override fun onDataLoaded(entity: CollectionEntity) {
                mView.initRootView(entity)
            }

            override fun onDataNotAvailable(e: Throwable) {
                Log.i("testlog", e.toString())
            }
        })
    }

    override fun submit(ctx: Context) {
        mModel.upload(ctx)
    }
}