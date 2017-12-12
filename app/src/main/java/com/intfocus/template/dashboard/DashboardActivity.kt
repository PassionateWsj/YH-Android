package com.intfocus.template.dashboard

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.LinearLayout
import com.google.gson.Gson
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params.STORE
import com.intfocus.template.constant.Params.STORE_ID
import com.intfocus.template.dashboard.adapter.DashboardFragmentAdapter
import com.intfocus.template.dashboard.kpi.KpiFragment
import com.intfocus.template.dashboard.mine.MineFragment
import com.intfocus.template.dashboard.mine.bean.PushMessageBean
import com.intfocus.template.dashboard.report.ReportFragment
import com.intfocus.template.dashboard.workbox.WorkBoxFragment
import com.intfocus.template.general.net.ApiException
import com.intfocus.template.general.net.CodeHandledSubscriber
import com.intfocus.template.general.net.RetrofitUtil
import com.intfocus.template.model.OrmDBHelper
import com.intfocus.template.model.entity.DashboardItem
import com.intfocus.template.model.response.scanner.StoreItem
import com.intfocus.template.model.response.scanner.StoreListResult
import com.intfocus.template.scanner.BarCodeScannerActivity
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.ui.view.NoScrollViewPager
import com.intfocus.template.ui.view.TabView
import com.intfocus.template.util.ActionLogUtil
import com.intfocus.template.util.PageLinkManage
import com.intfocus.template.util.ToastUtils
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.sql.SQLException

class DashboardActivity : BaseActivity(), ViewPager.OnPageChangeListener {
    private var mSharedPreferences: SharedPreferences? = null
    private val mTabView: ArrayList<TabView> = ArrayList()
    private val mPagerData = ArrayList<Fragment>()
    private var userID: Int = 0
    private var mViewPager: NoScrollViewPager? = null
    private var mToolBar: LinearLayout? = null
    private var mTabKPI: TabView? = null
    private var mTabReport: TabView? = null
    private var mTabWorkBox: TabView? = null
    private var mTabMessage: TabView? = null
    private var mGson: Gson? = null
    private var storeList: List<StoreItem>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        mGson = Gson()
        mSharedPreferences = getSharedPreferences("DashboardPreferences", Context.MODE_PRIVATE)
        mViewPager = findViewById(R.id.content_view)

        initTabView()
        initShow()
        initViewPaper()
        getStoreList()

        val intent = intent
        if (intent.hasExtra("msgData")) {
            handlePushMessage(intent.getBundleExtra("msgData").getString("message"))
        }
    }

    /**
     *
     */
    private fun initShow() {
        var itemFragment: Fragment
        if (ConfigConstants.KPI_SHOW) {
            itemFragment = KpiFragment()
            initTabShowAndPagerData(mTabKPI!!, mTabView, itemFragment, mPagerData)
//                KpiPresenter(KpiImpl.getInstance(),itemFragment)
        }
        if (ConfigConstants.REPORT_SHOW) {
            itemFragment = ReportFragment()
            initTabShowAndPagerData(mTabReport!!, mTabView, itemFragment, mPagerData)
        }
        if (ConfigConstants.WORKBOX_SHOW) {
            itemFragment = WorkBoxFragment()
            initTabShowAndPagerData(mTabWorkBox!!, mTabView, itemFragment, mPagerData)
        }
        initTabShowAndPagerData(mTabMessage!!, mTabView, MineFragment(), mPagerData)
    }

    private fun initTabShowAndPagerData(tab: TabView, tabs: ArrayList<TabView>, item: Fragment, pagerData: ArrayList<Fragment>) {
        tabs.add(tab)
        pagerData.add(item)
        tab.visibility = View.VISIBLE
    }

    /**
     * 推送消息处理
     */
    private fun handlePushMessage(message: String) {
        val pushMessage = mGson!!.fromJson(message, PushMessageBean::class.java)
        pushMessage.body_title = intent.getBundleExtra("msgData").getString("message_body_title")
        pushMessage.body_text = intent.getBundleExtra("msgData").getString("message_body_text")
        pushMessage.new_msg = true
        pushMessage.user_id = userID
        val personDao = OrmDBHelper.getInstance(this).pushMessageDao
        //  RxJava异步存储推送过来的数据
        Observable.create(Observable.OnSubscribe<PushMessageBean> {
            try {
                personDao.createIfNotExists(pushMessage)
            } catch (e: SQLException) {
                e.printStackTrace()
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe { }

        // RxBus通知消息界面 ShowPushMessageActivity 更新数据
//        RxBusUtil.getInstance().post("UpDatePushMessage")
//        when (pushMessage.type) {
//            "report" -> pageLink(pushMessage.title + "", pushMessage.url, pushMessage.obj_id.toString(), "-1", pushMessage.obj_type.toString())
//            "analyse" -> {
//                mViewPager!!.currentItem = PAGE_REPORTS
//                mTabView[mViewPager!!.currentItem].setActive(true)
//            }
//            "app" -> {
//                mViewPager!!.currentItem = PAGE_REPORTS
//                mTabView[mViewPager!!.currentItem].setActive(true)
//            }
//            "message" -> {
//                mViewPager!!.currentItem = PAGE_MINE
//                mTabView[mViewPager!!.currentItem].setActive(true)
//            }
//        }
    }

    override fun onBackPressed() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
                .setMessage(String.format("确认退出【%s】？", resources.getString(R.string.app_name)))
                .setPositiveButton("确认") { _, _ ->
                    finish()
                    System.exit(0)
                }
                .setNegativeButton("取消") { _, _ ->
                    // 返回DashboardActivity
                }
        builder.show()
    }

    fun startBarCodeActivity(v: View) {
        if (!ConfigConstants.SCAN_ENABLE_KPI && !ConfigConstants.SCAN_ENABLE_REPORT && !ConfigConstants.SCAN_ENABLE_WORKBOX) {
            return
        }
        when {
            ContextCompat.checkSelfPermission(this@DashboardActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED -> {
                val builder = AlertDialog.Builder(this@DashboardActivity)
                builder.setTitle("温馨提示")
                        .setMessage("相机权限获取失败，是否到本应用的设置界面设置权限")
                        .setPositiveButton("确认") { _, _ -> goToAppSetting() }
                        .setNegativeButton("取消") { _, _ ->
                            // 返回DashboardActivity
                        }
                builder.show()
                return
            }
            storeList == null -> {
                val builder = AlertDialog.Builder(this@DashboardActivity)
                builder.setTitle("温馨提示")
                        .setMessage("抱歉, 您没有扫码权限")
                        .setPositiveButton("确认") { _, _ -> }
                builder.show()
                return
            }
            else -> {
                val barCodeScannerIntent = Intent(this, BarCodeScannerActivity::class.java)
                startActivity(barCodeScannerIntent)
                ActionLogUtil.actionLog("点击/扫一扫")
            }
        }
    }

    /**
     * 跳转系统设置页面
     */
    private fun goToAppSetting() {
        val intent = Intent()
        intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun initTabView() {
        mToolBar = findViewById(R.id.toolBar)

        mTabKPI = findViewById(R.id.tab_kpi)
        mTabReport = findViewById(R.id.tab_report)
        mTabWorkBox = findViewById(R.id.tab_workbox)
        mTabMessage = findViewById(R.id.tab_mine)

        mTabKPI!!.setOnClickListener(mTabChangeListener)
        mTabReport!!.setOnClickListener(mTabChangeListener)
        mTabWorkBox!!.setOnClickListener(mTabChangeListener)
        mTabMessage!!.setOnClickListener(mTabChangeListener)
    }

    private fun initViewPaper() {
        mViewPager!!.adapter = DashboardFragmentAdapter(supportFragmentManager, mPagerData)
        mViewPager!!.offscreenPageLimit = mPagerData.size
        if (ConfigConstants.LOAD_LAST_FRAGMENT_WHEN_LAUNCH) {
            mViewPager!!.currentItem = mSharedPreferences!!.getInt("LastTab", 0)
            mTabView[mViewPager!!.currentItem].setActive(true)
        } else {
            mTabView[0].setActive(true)
        }
        mViewPager!!.addOnPageChangeListener(this)
    }

    /**
     * Tab 栏按钮监听事件
     */
    private val mTabChangeListener = View.OnClickListener { v ->
        mTabView
                .filter { v.id == it.id }
                .forEach { mViewPager!!.currentItem = mTabView.indexOf(it) }
        refreshTabView()
    }

    /**
     * 重写ViewPager页面切换的处理方法
     */
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {
        if (state == 2) {
            mTabView[mViewPager!!.currentItem].setActive(true)
        }
        refreshTabView()
        mSharedPreferences!!.edit().putInt("LastTab", mViewPager!!.currentItem).apply()
    }

    /**
     * 刷新 TabView 高亮状态
     */
    private fun refreshTabView() {
        mTabView[mViewPager!!.currentItem].setActive(true)
        mTabView.indices
                .filter { it != mViewPager!!.currentItem }
                .forEach { mTabView[it].setActive(false) }
    }

    private fun getStoreList() {
        val storeItemDao = OrmDBHelper.getInstance(this).storeItemDao
        val mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        RetrofitUtil.getHttpService(applicationContext).getStoreList(mUserSP.getString("user_num", "0"))
                .compose(RetrofitUtil.CommonOptions<StoreListResult>())
                .subscribe(object : CodeHandledSubscriber<StoreListResult>() {
                    override fun onCompleted() {
                    }

                    override fun onError(apiException: ApiException?) {
                    }

                    override fun onBusinessNext(data: StoreListResult) {
                        if (data.data != null && data.data!!.isNotEmpty()) {
                            val mStoreInfoSP = getSharedPreferences("StoreInfo", Context.MODE_PRIVATE)
                            if ("" == mStoreInfoSP.getString(STORE, "")) {
                                val mStoreInfoSPEdit = mStoreInfoSP.edit()
                                mStoreInfoSPEdit.putString(STORE, data.data!![0].name)
                                mStoreInfoSPEdit.putString(STORE_ID, data.data!![0].id)
                                mStoreInfoSPEdit.apply()
                            }
                            storeList = data.data
                            Thread(Runnable {
                                try {
                                    storeItemDao.executeRaw("delete from store_data")
                                    for (item in storeList!!) {
                                        storeItemDao.createIfNotExists(item)
                                    }
                                } catch (e: SQLException) {
                                    e.printStackTrace()
                                }
                            }).start()
                        }
                    }
                })
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(items: DashboardItem?) {
        if (items != null) {
            val link = items.obj_link
            val objTitle = items.obj_title
            val objectId = items.obj_id
            val templateId = items.template_id
            val objectType = items.objectType
            val paramsMappingBean = items.paramsMappingBean ?: HashMap()

            PageLinkManage.pageLink(this, objTitle!!, link!!, objectId!!, templateId!!, objectType!!, paramsMappingBean)
        } else {
            ToastUtils.show(this, "没有指定链接")
        }
    }
}
