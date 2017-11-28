package com.intfocus.syp_template.subject.one.module.tables.root

import com.intfocus.syp_template.subject.one.entity.MDetailRootPageRequestResult
import com.intfocus.syp_template.subject.one.module.tables.TableImpl
import com.intfocus.syp_template.subject.one.module.tables.TableModel

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

    override fun loadData(rootId: Int, index: Int) {
        mModel.getRootData(rootId, index, object : TableModel.TableRootLoadDataCallback {
            override fun onDataLoaded(data: MDetailRootPageRequestResult) {
                mView.dataLoaded(data)
            }

            override fun onDataNotAvailable(e: Throwable?) {
            }
        })
    }
}
