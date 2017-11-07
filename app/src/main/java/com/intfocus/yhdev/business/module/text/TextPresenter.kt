package com.intfucos.yhdev.module.text

import com.intfucos.yhdev.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
class TextPresenter(
        private var mModel: TextModelImpl,
        private var mView: TextModuleContract.View
): TextModuleContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object: LoadDataCallback<TextEntity> {
            override fun onDataLoaded(data: TextEntity) {
                mView.initModule(data)
            }

            override fun onDataNotAvailable(e: Throwable) {

            }
        })
    }

    override fun update(entity: TextEntity, key: String) {
        mModel.insertDb(entity.toString(), key)
    }
}