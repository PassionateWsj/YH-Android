package com.intfocus.syp_template.business.subject.template.one.table

import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult
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
interface TableRootContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun dataLoaded(data: MDetailRootPageRequestResult)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(rootId: Int, index:Int)
    }
}
