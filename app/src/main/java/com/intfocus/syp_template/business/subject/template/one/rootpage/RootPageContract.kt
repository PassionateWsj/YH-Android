package com.intfocus.syp_template.business.subject.templateone.rootpage

import com.intfocus.syp_template.business.subject.template.one.entity.msg.MDetailRootPageRequestResult
import com.intfocus.syp_template.general.base.BasePresenter
import com.intfocus.syp_template.general.base.BaseView

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/24 下午3:03
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
interface RootPageContract {
    interface View : BaseView<Presenter> {
        // 展示数据
        fun showData(entity: MDetailRootPageRequestResult)
    }

    interface Presenter : BasePresenter {
        // 加载数据
        fun loadData(mParam: String)
    }
}
