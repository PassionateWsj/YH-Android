package com.intfocus.template.subject.one

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.intfocus.template.R
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.filter.FilterDialogFragment
import com.intfocus.template.listener.UMSharedListener
import com.intfocus.template.model.response.filter.MenuItem
import com.intfocus.template.subject.one.entity.EventRefreshTableRect
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.one.rootpage.RootPageFragment
import com.intfocus.template.subject.templateone.rootpage.RootPageImpl
import com.intfocus.template.subject.templateone.rootpage.RootPagePresenter
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.ui.view.RootScrollView
import com.intfocus.template.util.*
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import com.zbl.lib.baseframe.core.ActManager
import kotlinx.android.synthetic.main.actvity_meter_detal.*
import kotlinx.android.synthetic.main.item_action_bar.*
import org.greenrobot.eventbus.EventBus
import org.json.JSONObject
import java.util.*

/**
 * 模块一页面
 */
class NativeReportActivity : BaseActivity(), ModeContract.View, FilterDialogFragment.FilterListener {
    private var uuid: String? = null

    private lateinit var rootScrollView: RootScrollView

    lateinit var suspendContainer: FrameLayout
    lateinit var actionbar: RelativeLayout
    lateinit var mLlFilter: LinearLayout
    private var toFragment: Fragment? = null
    private var currentFtName: String? = null
    private var filterDisplay: String = ""

    private var mFragmentManager: FragmentManager? = null
    private var mFragmentTransaction: FragmentTransaction? = null

    /**
     * 当前的Fragment
     */
    private var mCurrentFragment: Fragment? = null

    private var groupId: String = ""
    private var reportId: String = ""
    private var templateId: String = ""
    private var objectType: String = ""
    private var bannerName: String = ""

    private var mTlTitleContainer: RelativeLayout? = null

    private var radioGroup: RadioGroup? = null

    /**
     * 地址选择
     */
    private var filterDataList: ArrayList<MenuItem> = arrayListOf()

    /**
     * 数据实体
     */
    override lateinit var presenter: ModeContract.Presenter
    private var reportPages: MutableList<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.actvity_meter_detal)
        showDialog(this)
        mFragmentManager = supportFragmentManager
        mTlTitleContainer = findViewById(R.id.rl_mdetal_title_container)
        suspendContainer = findViewById(R.id.fl_mdetal_top_suspend_container)
        rootScrollView = findViewById(R.id.rootScrollView)
        ModePresenter(ModeImpl.getInstance(), this)
        init()
        initListener()
    }

    override fun onBackPressed() {
        PageLinkManage.pageBackIntent(this)
        ModeImpl.destroyInstance()
        finish()
    }

    private fun init() {
        val intent = intent
        groupId = intent.getStringExtra(GROUP_ID)
        reportId = intent.getStringExtra(OBJECT_ID)
        objectType = intent.getStringExtra(OBJECT_TYPE)
        bannerName = intent.getStringExtra(BANNER_NAME)
        templateId = intent.getStringExtra(TEMPLATE_ID)
        mLlFilter = findViewById(R.id.ll_filter)
        tv_banner_title.text = bannerName
        actionbar = rl_action_bar
        iv_banner_setting.setOnClickListener{launchDropMenuActivity("")}

        uuid = reportId + templateId + groupId
        presenter.loadData(this, groupId, templateId, reportId)
    }

    private fun initListener() {
        rootScrollView.setOnScrollListener { EventBus.getDefault().post(EventRefreshTableRect(lastCheckId)) }
    }

    override fun dataLoaded(reportPage: List<String>, filter: Filter) {
        if (null != filter.data) {
            initFilter(filter)
        }

        if (reportPages == null) {
            reportPages = ArrayList()
        } else {
            reportPages!!.clear()
        }
        reportPages!!.addAll(reportPage)
        val dataSize = reportPages!!.size
        // 多个根页签
        if (dataSize > 1) {
            hs_page_btn.visibility = View.VISIBLE
            mTlTitleContainer!!.visibility = View.VISIBLE
            val scrollTitle = LayoutInflater.from(this)
                    .inflate(R.layout.item_mdetal_scroll_title, null)
            mTlTitleContainer!!.addView(scrollTitle)
            radioGroup = scrollTitle.findViewById(R.id.radioGroup)

            for (i in 0 until dataSize) {
                val rbtn = RadioButton(this)
                val paramsRb = RadioGroup.LayoutParams(
                        RadioGroup.LayoutParams.WRAP_CONTENT,
                        DisplayUtil.dip2px(this, 25f))
                paramsRb.setMargins(50, 0, 0, 0)

                rbtn.tag = i
                rbtn.setPadding(DisplayUtil.dip2px(this, 15f), 0, DisplayUtil.dip2px(this, 15f), 0)
                rbtn.buttonDrawable = null
                rbtn.setBackgroundResource(R.drawable.selector_mdetal_act_rbtn)
                rbtn.setTextSize(TypedValue.COMPLEX_UNIT_PX, resources.getDimension(R.dimen.font_medium))
                val colorStateList = resources.getColorStateList(R.color.color_mdetal_act_rbtn)
                rbtn.setTextColor(colorStateList)
                rbtn.text = reportPages!![i]
                radioGroup!!.addView(rbtn, paramsRb)
                rbtn.setOnCheckedChangeListener { buttonView, isChecked ->
                    if (isChecked) {
                        val tag = buttonView.tag as Int
                        switchFragment(tag)
                    }
                }
                if (i == 0) {
                    rbtn.isChecked = true
                }
            }
        } else if (dataSize == 1) {
            hs_page_btn.visibility = View.GONE
            mTlTitleContainer!!.visibility = View.GONE
            switchFragment(0)
        }
    }

    /**
     * 初始化筛选框
     */
    private fun initFilter(filter: Filter) {
        this.filterDataList = filter.data!!
        this.filterDisplay = filter.display!!
        if (filterDataList.isEmpty()) {
            ll_filter.visibility = View.GONE
        } else {
            ll_filter.visibility = View.VISIBLE
            tv_location_address.text = filterDisplay
            tv_address_filter.setOnClickListener { showDialogFragment() }
        }
    }

    private fun showDialogFragment() {
        val mFragTransaction = supportFragmentManager.beginTransaction()
        val fragment = supportFragmentManager.findFragmentByTag("filterFragment")
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment)
        }
        val dialogFragment = FilterDialogFragment.newInstance(filterDataList)
        //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
        dialogFragment!!.show(mFragTransaction, "filterFragment")
    }

    /**
     * 筛选完成回调
     */
    override fun complete(data: ArrayList<MenuItem>) {
        var addStr = ""
        val size = data.size
        for (i in 0 until size) {
            addStr += data[i].name!! + "||"
        }
        addStr = addStr.substring(0, addStr.length - 2)
        if (filterDisplay != addStr && mFragmentManager!!.fragments.isNotEmpty()) {
            // 清空栈中 Fragment
            val ft = mFragmentManager!!.beginTransaction()
            mFragmentManager!!.fragments
                    .filterIsInstance<RootPageFragment>()
                    .forEach { ft.remove(it) }
            ft.commitAllowingStateLoss()
            LogUtil.d(this, "fragments Num ::: " + mFragmentManager!!.fragments.size)
            mTlTitleContainer!!.removeAllViews()
            hs_page_btn.visibility = View.GONE
            mTlTitleContainer!!.visibility = View.GONE

            presenter.saveFilterSelected(addStr)
        }
    }

    /**
     * 将当前选择的fragment显示(show)出来，没选择的隐藏(hide)
     *
     * @param checkId
     */
    private fun switchFragment(checkId: Int) {
        rootScrollView.smoothScrollTo(0, 0)
        lastCheckId = checkId
        currentFtName = fragmentTag + checkId + filterDisplay
        LogUtil.d(this, "currentFtName ::: " + currentFtName)
        toFragment = supportFragmentManager.findFragmentByTag(currentFtName)

        if (mCurrentFragment != null && mCurrentFragment == toFragment) {
            return
        }

        if (toFragment == null) {
            if (reportPages != null && reportPages!!.size > 0) {
                toFragment = RootPageFragment.newInstance(checkId, uuid)
                RootPagePresenter(RootPageImpl.getInstance(), (toFragment as RootPageFragment?)!!)
            }
        }

        mFragmentTransaction = mFragmentManager!!.beginTransaction()
        if (mCurrentFragment == null) {
            mFragmentTransaction!!.add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit()
            mCurrentFragment = toFragment
        } else if (mCurrentFragment !== toFragment) {
            if (!toFragment!!.isAdded) {
                mFragmentTransaction!!.hide(mCurrentFragment).add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit()
            } else {
                mFragmentTransaction!!.hide(mCurrentFragment).show(toFragment).commit()
            }
            mCurrentFragment = toFragment
        }

        hideLoading()
    }

    fun menuItemClick(view: View) {
        when (view.id) {
            R.id.ll_share -> share(this, "")
            R.id.ll_comment -> comment(this, reportId, objectType, bannerName)
            R.id.ll_refresh -> refresh()
        }
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * 刷新
     */
    private fun refresh() {
        //清空栈中 Fragment
        val ft = mFragmentManager!!.beginTransaction()
        mFragmentManager!!.fragments
                .filterIsInstance<RootPageFragment>()
                .forEach { ft.remove(it) }
        ft.commitAllowingStateLoss()
        presenter.loadData(this, groupId, templateId, reportId)
    }

    companion object {
        private val fragmentTag = "android:switcher:" + R.layout.actvity_meter_detal + ":"
        var lastCheckId: Int = 0
    }
}
