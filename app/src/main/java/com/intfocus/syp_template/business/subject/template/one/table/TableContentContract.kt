package com.intfocus.syp_template.business.subject.template.one.table

import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity
import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/20 下午3:11
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface TableContentContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(data: ModularTwo_UnitTableEntity)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(dataJson: String)
    }
}