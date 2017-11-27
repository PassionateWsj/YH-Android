package com.intfocus.syp_template.subject.one.module.tables

import com.intfocus.syp_template.subject.one.entity.Tables

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

    override fun loadData(data: Tables) {
        mView.showData(data)
    }
}
