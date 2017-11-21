package com.intfocus.syp_template.business.subject.template.one.table

import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午4:04
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class TableRootPresenter(
        private val mModel: TableImpl,
        private val mView: TableRootContract.View
) : TableRootContract.Presenter {

    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(uuid: String, index: Int) {
        mModel.getRootData(uuid,index,object :TableModel.TableRootLoadDataCallback{
            override fun onDataLoaded(data: MDetailRootPageRequestResult) {
                mView.dataLoaded(data)
            }

            override fun onDataNotAvailable(e: Throwable?) {
            }
        })
    }
}