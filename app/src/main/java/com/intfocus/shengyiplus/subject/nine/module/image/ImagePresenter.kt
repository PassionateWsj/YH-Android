package com.intfocus.shengyiplus.subject.nine.module.image

import com.intfocus.shengyiplus.util.Utils
import com.intfocus.shengyiplus.model.callback.LoadDataCallback

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

    override fun update(imageEntity: ImageEntity, key: String) {
        mModel.insertDb(Utils.listToString(imageEntity.value), key)
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object : LoadDataCallback<ImageEntity> {
            override fun onSuccess(data: ImageEntity) {
                mView.initModule(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }
}
