package com.intfucos.yhdev.module.options

import com.intfucos.yhdev.collection.callback.LoadDataCallback
import com.intfucos.yhdev.module.text.TextModuleContract
import com.intfucos.yhdev.module.text.TextEntity

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class OptionsPresenter(
        private var mModel: OptionsModelImpl,
        private var mView: OptionsModuleContract.View
): OptionsModuleContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun update(entity: OptionsEntity, key: String) {
        mModel.insertDb(entity.toString(), key)
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object : LoadDataCallback<OptionsEntity> {
            override fun onDataLoaded(data: OptionsEntity) {
                mView.initModule(data)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }
}