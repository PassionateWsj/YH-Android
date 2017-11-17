package com.intfocus.syp_template.module.options

import com.intfocus.syp_template.collection.callback.LoadDataCallback
import com.intfocus.syp_template.module.text.TextModuleContract
import com.intfocus.syp_template.module.text.TextEntity

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
        mModel.insertDb(entity.value, key)
    }

    override fun loadData(mParam: String) {
        mModel.analyseData(mParam, object : LoadDataCallback<OptionsEntity> {
            override fun onSuccess(data: OptionsEntity) {
                mView.initModule(data)
            }

            override fun onError(e: Throwable) {
            }

            override fun onComplete() {
            }
        })
    }
}
