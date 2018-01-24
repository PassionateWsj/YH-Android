package com.intfocus.template.subject.nine

import android.content.Context
import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.subject.nine.entity.CollectionEntity
import com.intfocus.template.util.LogUtil

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

    override fun loadData(ctx: Context,reportId: String, templateId: String, groupId: String) {
        mModel.getData(ctx,reportId, templateId, groupId, object : LoadDataCallback<CollectionEntity> {
            override fun onSuccess(data: CollectionEntity) {
                mView.initRootView(data)
            }

            override fun onError(e: Throwable) {
                LogUtil.d(this@CollectionPresenter, e.toString())
            }

            override fun onComplete() {
            }
        })
    }

    override fun submit(ctx: Context) {
        mModel.upload(ctx)
    }
}
