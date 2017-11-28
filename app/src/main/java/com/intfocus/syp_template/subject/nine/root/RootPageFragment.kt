package com.intfocus.syp_template.subject.nine.root

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.intfocus.syp_template.R
import com.intfocus.syp_template.ui.BaseModuleFragment
import com.intfocus.syp_template.subject.nine.entity.RootPageRequestResult
import com.intfocus.syp_template.constant.Params.ARG_PARAM
import com.intfocus.syp_template.constant.Params.SU_ROOT_ID
import com.intfocus.syp_template.util.LogUtil
import com.intfocus.syp_template.subject.nine.module.image.ImageFragment
import com.intfocus.syp_template.subject.nine.module.image.ImageModelImpl
import com.intfocus.syp_template.subject.nine.module.image.ImagePresenter
import com.intfocus.syp_template.subject.nine.module.options.DropOptionsFragment
import com.intfocus.syp_template.subject.nine.module.options.OptionsModelImpl
import com.intfocus.syp_template.subject.nine.module.options.OptionsPresenter
import com.intfocus.syp_template.subject.nine.module.text.MultiTextFragment
import com.intfocus.syp_template.subject.nine.module.text.SingleTextFragment
import com.intfocus.syp_template.subject.nine.module.text.TextModelImpl
import com.intfocus.syp_template.subject.nine.module.text.TextPresenter
import kotlinx.android.synthetic.main.fragment_root.*
import java.util.*

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
class RootPageFragment : BaseModuleFragment(), RootPageContract.View {
    private val TAG = "root_page"

    /** 最上层跟跟标签ID */
    private var suRootID: Int = 0

    /** 单个页签的数据 */
    private lateinit var mParam: String

    private var rootView: View? = null
    private var fm: FragmentManager? = null

    override lateinit var presenter: RootPageContract.Presenter

    companion object {
        fun newInstance(suRootID: Int, param: String): RootPageFragment {
            val fragment = RootPageFragment()
            val args = Bundle()
            args.putInt(SU_ROOT_ID, suRootID)
            args.putString(ARG_PARAM, param)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            suRootID = arguments!!.getInt(SU_ROOT_ID)
            mParam = arguments!!.getString(ARG_PARAM)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        fm = fragmentManager
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_root, container, false)
            presenter.loadData(mParam)
        }
        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        RootPageModelImpl.destroyInstance()
    }

    /**
     * 插入组件模块
     */
    override fun insertModule(result: RootPageRequestResult) {
        val random = Random()
        for (i in 0 until result.datas.size) {
            var fragment: Fragment? = null
            val entity = result.datas[i]
            when (entity.type) {
                "single_text" -> {
                    fragment = SingleTextFragment.newInstance(entity.config, entity.key)
                    TextPresenter(TextModelImpl.getInstance(), fragment)
                }

                "multi_text" -> {
                    fragment = MultiTextFragment.newInstance(entity.config, entity.key)
                    TextPresenter(TextModelImpl.getInstance(), fragment)
                }

                "drop_options" -> {
                    fragment = DropOptionsFragment.newInstance(entity.config, entity.key)
                    OptionsPresenter(OptionsModelImpl.getInstance(), fragment)
                }

                "upload_images" -> {
                    fragment = ImageFragment.newInstance(entity.config, entity.key)
                    ImagePresenter(ImageModelImpl.getInstance(), fragment)
                }

                else -> LogUtil.d(TAG, entity.type + ">>>>>> is unknown module!")
            }

            if (fragment != null) {
                val layout = FrameLayout(ctx)
                val params = AppBarLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
                layout.layoutParams = params
                val id = random.nextInt(Integer.MAX_VALUE)
                layout.id = id
                ll_root_container.addView(layout)
                val ft = fm!!.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ft.replace(layout.id, fragment)
                ft.commitNow()
            }
        }
    }
}
