package com.intfucos.yhdev.collection.root

import android.util.Log
import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.collection.entity.RootPageRequestResult

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
class RootPagePresenter(
    private val mModel: RootPageModelImpl,
    private val mView: RootPageContract.View
): RootPageContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(mParam: String) {
        mModel.getData(mParam, object : LoadDataCallback<RootPageRequestResult> {
            override fun onSuccess(data: RootPageRequestResult) {
                mView.insertModule(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }
}