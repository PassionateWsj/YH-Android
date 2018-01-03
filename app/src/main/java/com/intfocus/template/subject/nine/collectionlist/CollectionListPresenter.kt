package com.intfocus.template.subject.nine.collectionlist

import com.intfocus.template.model.callback.LoadDataCallback
import com.intfocus.template.model.entity.Collection

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 上午11:44
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CollectionListPresenter(
        val mModel: CollectionListModelImpl,
        val mView:CollectionListContract.View
):CollectionListContract.Presenter {
    init {
        mView.presenter =this
    }
    override fun start() {
    }

    override fun loadData() {
        loadData("")
    }
    override fun loadData(keyWord:String) {
        mModel.getData(keyWord,object :LoadDataCallback<List<Collection>>{
            override fun onSuccess(data: List<Collection>) {
                mView.updateData(data)
            }

            override fun onError(e: Throwable) {

            }

            override fun onComplete() {
            }
        })
    }

}