package com.intfocus.shengyiplus.subject.nine

import android.content.Context
import com.intfocus.shengyiplus.model.callback.LoadDataCallback
import com.intfocus.shengyiplus.subject.nine.entity.CollectionEntity
import com.intfocus.shengyiplus.util.LogUtil

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
            override fun onSuccess(data: CollectionEntity) {
                mView.initRootView(data)
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
