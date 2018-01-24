package com.intfocus.template.subject.nine.root

import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.intfocus.template.R
import com.intfocus.template.constant.Params.ARG_PARAM
import com.intfocus.template.constant.Params.SU_ROOT_ID
import com.intfocus.template.subject.nine.entity.Content
import com.intfocus.template.subject.nine.module.image.ImageFragment
import com.intfocus.template.subject.nine.module.image.ImageModelImpl
import com.intfocus.template.subject.nine.module.image.ImagePresenter
import com.intfocus.template.subject.nine.module.options.DropOptionsFragment
import com.intfocus.template.subject.nine.module.options.OptionsModelImpl
import com.intfocus.template.subject.nine.module.options.OptionsPresenter
import com.intfocus.template.subject.nine.module.text.MultiTextFragment
import com.intfocus.template.subject.nine.module.text.SingleTextFragment
import com.intfocus.template.subject.nine.module.text.TextModelImpl
import com.intfocus.template.subject.nine.module.text.TextPresenter
import com.intfocus.template.ui.BaseModuleFragment
import com.intfocus.template.util.LogUtil
import kotlinx.android.synthetic.main.fragment_root.*
import java.util.*

/**
 * @author liuruilin
 * @data 2017/11/1
 * @describe
 */
class RootPageFragment : BaseModuleFragment() {
    private val TAG = "root_page"

    /** 最上层跟跟标签ID */
    private var suRootID: Int = 0

    /** 单个页签的数据 */
    private lateinit var mParam: ArrayList<Content>

    private var rootView: View? = null

    companion object {
        fun newInstance(suRootID: Int, parts: ArrayList<Content>): RootPageFragment {
            val fragment = RootPageFragment()
            val args = Bundle()
            args.putInt(SU_ROOT_ID, suRootID)
            args.putSerializable(ARG_PARAM, parts)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            suRootID = arguments!!.getInt(SU_ROOT_ID)
            mParam = arguments!!.getSerializable(ARG_PARAM) as ArrayList<Content>
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_root, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        generateModule()
    }

    /**
     * 插入组件模块
     */
    private fun generateModule() {
        val random = Random()
        for (i in 0 until mParam.size) {
            var fragment: Fragment? = null
            val entity = mParam[i]
            when (entity.type) {
                "single_text" -> {
                    fragment = SingleTextFragment.newInstance(entity.config, entity.key, entity.list ?: 0)
                    TextPresenter(TextModelImpl.getInstance(), fragment)
                }

                "multi_text" -> {
                    fragment = MultiTextFragment.newInstance(entity.config, entity.key, entity.list ?: 0)
                    TextPresenter(TextModelImpl.getInstance(), fragment)
                }

                "drop_options" -> {
                    fragment = DropOptionsFragment.newInstance(entity.config, entity.key, entity.list ?: 0)
                    OptionsPresenter(OptionsModelImpl.getInstance(), fragment)
                }

                "upload_images" -> {
                    fragment = ImageFragment.newInstance(entity.config, entity.key, entity.list ?: 0)
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
                val ft = childFragmentManager.beginTransaction()
                ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                ft.replace(layout.id, fragment)
                ft.commitNow()
            }
        }
    }
}
