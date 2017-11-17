package com.intfocus.yhdev.collection

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.CompoundButton
import android.widget.RadioGroup
import com.intfocus.yhdev.R
import com.intfocus.yhdev.general.util.PageLinkManage
import com.intfocus.yhdev.general.util.URLs
import com.intfocus.yhdev.base.BaseWidgetFragment
import com.intfocus.yhdev.collection.entity.CollectionEntity
import com.intfocus.yhdev.collection.root.RootPageFragment
import com.intfocus.yhdev.collection.root.RootPageModelImpl
import com.intfocus.yhdev.collection.root.RootPagePresenter
import kotlinx.android.synthetic.main.activity_collection.*

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe 信息采集模板
 */
class CollectionActivity : AppCompatActivity(), CollectionContract.View {
    var lastCheckId: Int = 0
    private var currentFtName: String? = null
    private val fragmentTag = "android:switcher:"

    private lateinit var reportId: String
    private lateinit var templateId: String
    private lateinit var groupId: String

    /** 当前的Fragment */
    private var mCurrentFragment: Fragment? = null
    /** 目标Fragment */
    private lateinit var toFragment: BaseWidgetFragment

    /** 根页签 */
    private var radioGroup: RadioGroup? = null
    private var rootTableListener: RootTableCheckedChangeListener? = null

    private var mFragmentManager: FragmentManager? = null
    private var mFragmentTransaction: FragmentTransaction? = null

    /** 数据实体类 */
    private var mEntity: CollectionEntity? = null

    override lateinit var presenter: CollectionContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection)
        mFragmentManager = supportFragmentManager

        CollectionPresenter(CollectionModelImpl.getInstance(), this)
        init()
    }

    private fun init() {
        reportId = intent.getStringExtra(URLs.kObjectId)
        templateId = intent.getStringExtra(URLs.kTemplatedId)
        groupId = intent.getStringExtra(URLs.kGroupId)

        tv_collection_title.text = intent.getStringExtra(URLs.kBannerName)
        btn__submit.setOnClickListener {
            presenter.submit(this)
            onBackPressed()
        }
        presenter.loadData(reportId, templateId, groupId)
    }

    fun back(v: View) {
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        CollectionModelImpl.destroyInstance()
    }

    override fun onBackPressed() {
        PageLinkManage.pageBackIntent(this)
        finish()
    }

    override fun initRootView(entity: CollectionEntity) {
        this.mEntity = entity
        val pageDataArrayList = entity.data
        if (pageDataArrayList == null || pageDataArrayList.size == 0) {
            return
        }

        // 根页签数量
        val rootPageSize = pageDataArrayList.size

        // 多个根页签
        if (rootPageSize > 1) {
            TODO("多个根页签时, 页面顶部显示可滑动的 root_tab 区域")
        } else if (rootPageSize == 1) {

            switchFragment(0)
        }
    }

    internal inner class RootTableCheckedChangeListener : CompoundButton.OnCheckedChangeListener {
        override fun onCheckedChanged(buttonView: CompoundButton, isChecked: Boolean) {
            if (isChecked) {
                val tag = buttonView.tag as Int
                switchFragment(tag)
            }
        }
    }

    /**
     * 将当前选择的fragment显示(show)出来，没选择的隐藏(hide)
     * @param checkId
     */
    fun switchFragment(checkId: Int) {
        lastCheckId = checkId
        currentFtName = fragmentTag + checkId
        if (supportFragmentManager.findFragmentByTag(currentFtName) != null) {
            toFragment = supportFragmentManager.findFragmentByTag(currentFtName) as BaseWidgetFragment
        } else {
            val pageDataArrayList = mEntity!!.data
            if (pageDataArrayList != null && pageDataArrayList!!.size > 0) {
                toFragment = RootPageFragment.newInstance(checkId, pageDataArrayList!![checkId].content!!)
                RootPagePresenter(RootPageModelImpl.getInstance(), toFragment as RootPageFragment)
            }
        }

        mFragmentTransaction = mFragmentManager!!.beginTransaction()
        if (mCurrentFragment == null) {
            mFragmentTransaction!!.add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit()
            mCurrentFragment = toFragment
        } else if (mCurrentFragment !== toFragment) {
            if (!toFragment.isAdded) {
                mFragmentTransaction!!.hide(mCurrentFragment).add(R.id.fl_mdetal_cont_container, toFragment, currentFtName).commit()
            } else {
                mFragmentTransaction!!.hide(mCurrentFragment).show(toFragment).commit()
            }
            mCurrentFragment = toFragment
        }
    }
}
