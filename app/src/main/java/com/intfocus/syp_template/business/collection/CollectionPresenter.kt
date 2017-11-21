package com.intfocus.syp_template.collection

import android.content.Context
import com.intfocus.syp_template.collection.callback.LoadDataCallback
import com.intfocus.syp_template.collection.entity.CollectionEntity
import com.intfocus.syp_template.general.util.LogUtil

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

    override fun loadData(reportId: String, templateId: String, groupId: String) {
        mModel.getData(reportId, templateId, groupId, object : LoadDataCallback<CollectionEntity> {
            override fun onSuccess(entity: CollectionEntity) {
                mView.initRootView(entity)
            }

            override fun onError(e: Throwable) {
                LogUtil.d("testlog", e.toString())
            }

            override fun onComplete() {
            }
        })
    }

    override fun submit(ctx: Context) {
        mModel.upload(ctx)
    }
}
