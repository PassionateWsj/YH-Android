package com.intfocus.template.subject.nine.module.text

import com.intfocus.template.subject.nine.callback.LoadDataCallback

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
class TextPresenter(
        private var mModel: TextModelImpl,
        private var mView: TextModuleContract.View
) : TextModuleContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object : LoadDataCallback<TextEntity> {
            override fun onSuccess(data: TextEntity) {
                mView.initModule(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }

    override fun update(entity: TextEntity, key: String) {
        mModel.insertDb(entity.value, key)
    }
}
