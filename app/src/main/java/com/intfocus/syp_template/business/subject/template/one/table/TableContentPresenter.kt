package com.intfocus.syp_template.business.subject.template.one.table

import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午4:04
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class TableContentPresenter(
        private val mModel: TableImpl,
        private val mView: TableContentContract.View
) : TableContentContract.Presenter {
    init {
        mView.presenter = this
    }

    override fun start() {

    }

    override fun loadData(dataJson: String) {
        mModel.getData(dataJson,  object : TableModel.TableContentLoadDataCallback {
            override fun onDataLoaded(data: ModularTwo_UnitTableEntity) {
                mView.showData(data)
            }

            override fun onDataNotAvailable(e: Throwable?) {

            }

        })
    }
}