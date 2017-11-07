package com.intfucos.yhdev.module.image

import com.intfucos.yhdev.collection.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class ImagePresenter(
        private var mModel: ImageModelImpl,
        private var mView: ImageModuleContract.View
) : ImageModuleContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun update(entity: ImageEntity, key: String) {
        mModel.insertDb(entity.toString(), key)
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object : LoadDataCallback<ImageEntity> {
            override fun onDataLoaded(data: ImageEntity) {
                mView.initModule(data)
            }

            override fun onDataNotAvailable(e: Throwable) {
            }
        })
    }
}