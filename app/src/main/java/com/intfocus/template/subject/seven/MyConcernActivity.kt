package com.intfocus.template.subject.seven

import android.content.Intent
import android.os.Bundle
import android.support.design.widget.AppBarLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.intfocus.template.R
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.constant.Params.USER_NUM
import com.intfocus.template.filter.FilterDialogFragment
import com.intfocus.template.model.response.filter.MenuItem
import com.intfocus.template.subject.nine.CollectionModelImpl.Companion.uuid
import com.intfocus.template.subject.one.ModeImpl
import com.intfocus.template.subject.one.entity.Filter
import com.intfocus.template.subject.seven.concernlist.ConcernListActivity
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.util.LogUtil
import kotlinx.android.synthetic.main.actvity_my_attention.*
import java.util.*

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/12/18 上午11:14
 * e-mail: PassionateWsj@outlook.com
 * name: 模板七
 * desc: 我的关注，提供筛选和关注功能
 * ****************************************************
 */
class MyConcernActivity : BaseActivity(), MyConcernContract.View, FilterDialogFragment.FilterListener {

    companion object {
        val REQUEST_CODE = 2
        val FRAGMENT_TAG = "filterFragment"
    }

    private lateinit var mUserNum: String
    private var filterDisplay: String = ""
    private var currentFilterId: String = ""
    lateinit var groupId: String
    lateinit var reportId: String
    lateinit var objectType: String
    lateinit var bannerName: String
    lateinit var templateId: String

    /**
     * 地址选择
     */
    private var filterDataList: ArrayList<MenuItem> = arrayListOf()

    override lateinit var presenter: MyConcernContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.actvity_my_attention)
        MyConcernPresenter(ModeImpl.getInstance(), this)
        initData()
        initView()
    }


    fun initData() {
        mUserNum = mUserSP.getString(USER_NUM, "")

        val intent = intent
        groupId = intent.getStringExtra(GROUP_ID)
        reportId = intent.getStringExtra(OBJECT_ID)
        objectType = intent.getStringExtra(OBJECT_TYPE)
        bannerName = intent.getStringExtra(BANNER_NAME)
        templateId = intent.getStringExtra(TEMPLATE_ID)

        uuid = reportId + templateId + groupId

        presenter.loadData(this,groupId,templateId, reportId)
    }

    private fun initView() {
        tv_banner_title.text = bannerName
    }

    override fun onUpdateData(filter: Filter) {
//        presenter.
    }
//    override fun onUpdateData(data: Test2, filter: Filter) {
//        if (data.data.main_attention_data.isNotEmpty()) {
//            if (null != filter.data) {
//                initFilter(filter)
//            }
//            tv_banner_title.text = data.data.main_data_name
//            tv_my_attention_update_time.text = TimeUtils.getStrTime(data.data.updated_at)
//
//            val indicatorGroupFragment: Fragment = IndicatorGroupFragment().newInstance(data.data.main_attention_data as ArrayList<SingleValue>)
//            addItemView(indicatorGroupFragment, ll_my_attention_container)
//
//            val indicatorListFragment: Fragment = IndicatorListFragment().newInstance(data.data.attentioned_data as ArrayList<Test2.DataBeanXX.AttentionedDataBean>)
//            addItemView(indicatorListFragment, ll_my_attention_container)
//
//        }
//    }

    private fun addItemView(fragment: Fragment, viewGroup: ViewGroup) {
        val layout = FrameLayout(this)
        val params = AppBarLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        params.scrollFlags = AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL or AppBarLayout.LayoutParams.SCROLL_FLAG_ENTER_ALWAYS
        layout.layoutParams = params
        val id = Random().nextInt(Integer.MAX_VALUE)
        layout.id = id
        viewGroup.addView(layout)
        val ft = supportFragmentManager.beginTransaction()
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        ft.replace(layout.id, fragment)
        ft.commitNow()
    }

    fun menuOnClicked(view: View) {
        when (view.id) {
            R.id.iv_attention -> {
                startActivityForResult(Intent(this, ConcernListActivity::class.java), REQUEST_CODE)
            }
            R.id.tv_address_filter -> {
                showDialogFragment()
            }
        }
    }

    /**
     * 初始化筛选框
     */
    private fun initFilter(filter: Filter) {
        filter.data?.let { it[0].id?.let { currentFilterId = it } }
        this.filterDataList = filter.data!!
        this.filterDisplay = filter.display!!
        if (filterDataList.isEmpty()) {
            ll_filter.visibility = View.GONE
        } else {
            ll_filter.visibility = View.VISIBLE
            tv_location_address.text = filterDisplay
        }
    }

    /**
     * 筛选框
     */
    private fun showDialogFragment() {
        val mFragTransaction = supportFragmentManager.beginTransaction()
        val fragment = supportFragmentManager.findFragmentByTag(FRAGMENT_TAG)
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment)
        }
        val dialogFragment = FilterDialogFragment.newInstance(filterDataList)
        //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
        dialogFragment!!.show(mFragTransaction, FRAGMENT_TAG)
    }

    /**
     * 筛选回调
     */
    override fun complete(menuItems: ArrayList<MenuItem>) {
        var addStr = ""
        val size = menuItems.size
        for (i in 0 until size) {
            addStr += menuItems[i].name!! + "||"
        }
        addStr = addStr.substring(0, addStr.length - 2)
        tv_location_address.text = addStr
        menuItems[menuItems.size - 1].id?.let {
            currentFilterId = it
            ll_my_attention_container.removeAllViews()
            presenter.loadData(mUserNum, currentFilterId)
        }
        LogUtil.d(this, "Filter FullName ::: " + addStr)
        LogUtil.d(this, "Filter ItemName ::: " + menuItems[menuItems.size - 1].name)
        LogUtil.d(this, "Filter id ::: " + menuItems[menuItems.size - 1].id)
    }

    /**
     * 关注 item 后的回调刷新
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == ConcernListActivity.RESPONSE_CODE) {
            presenter.loadData(mUserNum, currentFilterId)
        }
    }
}