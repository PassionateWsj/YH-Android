package com.intfocus.yhdev.business.dashboard

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
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.View
import com.google.gson.Gson
import com.intfocus.yhdev.R
import com.intfocus.yhdev.business.dashboard.adapter.DashboardFragmentAdapter
import com.intfocus.yhdev.business.dashboard.kpi.KpiFragment
import com.intfocus.yhdev.business.dashboard.mine.MineFragment
import com.intfocus.yhdev.business.dashboard.mine.bean.PushMessageBean
import com.intfocus.yhdev.business.dashboard.report.ReportFragment
import com.intfocus.yhdev.business.dashboard.workbox.WorkBoxFragment
import com.intfocus.yhdev.business.scanner.BarCodeScannerActivity
import com.intfocus.yhdev.business.subject.template.five.TemplateFiveActivity
import com.intfocus.yhdev.business.subject.template.one.TemplateOneActivity
import com.intfocus.yhdev.business.subject.template.three.TemplateThreeActivity
import com.intfocus.yhdev.business.subject.template.two.SubjectActivity
import com.intfocus.yhdev.business.subject.webapplication.WebApplicationActivity
import com.intfocus.yhdev.business.subject.webapplication.WebApplicationActivityV6
import com.intfocus.yhdev.YHApplication
import com.intfocus.yhdev.general.bean.DashboardItemBean
import com.intfocus.yhdev.general.constant.ConfigConstants
import com.intfocus.yhdev.general.data.response.scanner.StoreItem
import com.intfocus.yhdev.general.data.response.scanner.StoreListResult
import com.intfocus.yhdev.general.db.OrmDBHelper
import com.intfocus.yhdev.general.net.ApiException
import com.intfocus.yhdev.general.net.CodeHandledSubscriber
import com.intfocus.yhdev.general.net.RetrofitUtil
import com.intfocus.yhdev.general.util.*
import com.intfocus.yhdev.general.view.NoScrollViewPager
import com.intfocus.yhdev.general.view.TabView
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.json.JSONException
import org.json.JSONObject
import rx.Observable
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import sumimakito.android.advtextswitcher.AdvTextSwitcher
import java.sql.SQLException

class DashboardActivity : FragmentActivity(), ViewPager.OnPageChangeListener, AdvTextSwitcher.Callback {
    //    private var mDashboardFragmentAdapter: DashboardFragmentAdapter? = null
    private var mSharedPreferences: SharedPreferences? = null
    private val mTabView: ArrayList<TabView> = ArrayList()
    private val mPagerData = ArrayList<Fragment>()
    private var userID: Int = 0
    private var mApp: YHApplication? = null
    private var mViewPager: NoScrollViewPager? = null
    private var mTabKPI: TabView? = null
    private var mTabReport: TabView? = null
    private var mTabWorkBox: TabView? = null
    private var mTabMessage: TabView? = null
    private var mContext: Context? = null
    private var mAppContext: Context? = null
    private var mGson: Gson? = null
    lateinit var mUserSP: SharedPreferences
    private var storeList: List<StoreItem>? = null
    private val loadLastFragmentWhenLaunch = true

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")

    private val EXTERNAL_LINK = "-1"
    private val SCANNER = "-2"
    private val TEMPLATE_ONE = "1"
    private val TEMPLATE_TWO = "2"
    private val TEMPLATE_THREE = "3"
    private val TEMPLATE_FOUR = "4"
    private val TEMPLATE_FIVE = "5"
    private val TEMPLATE_SIX = "6"
    private val TEMPLATE_NINE = "9"
//    companion object {
//        val PAGE_KPI = 0
//        val PAGE_REPORTS = 1
//        val PAGE_APP = 2
//        val PAGE_MINE = 3
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)
        EventBus.getDefault().register(this)
        mApp = this.application as YHApplication
        mAppContext = mApp!!.appContext
        mContext = this
        mGson = Gson()
        mSharedPreferences = getSharedPreferences("DashboardPreferences", Context.MODE_PRIVATE)
        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
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
        initTabShowAndPagerData(mTabKPI!!, mTabView, KpiFragment(), mPagerData, ConfigConstants.KPI_SHOW)
        initTabShowAndPagerData(mTabReport!!, mTabView, ReportFragment(), mPagerData, ConfigConstants.REPORT_SHOW)
        initTabShowAndPagerData(mTabWorkBox!!, mTabView, WorkBoxFragment(), mPagerData, ConfigConstants.WORKBOX_SHOW)
        initTabShowAndPagerData(mTabMessage!!, mTabView, MineFragment(), mPagerData, true)
    }

    private fun initTabShowAndPagerData(tab: TabView, tabs: ArrayList<TabView>, item: Fragment, pagerData: ArrayList<Fragment>, boolean: Boolean) {
        tab.visibility = if (boolean) {
            tabs.add(tab)
            pagerData.add(item)
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    override fun onDestroy() {
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    /*
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
                    mApp!!.setCurrentActivity(null)
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
                val barCodeScannerIntent = Intent(mContext, BarCodeScannerActivity::class.java)
                mContext!!.startActivity(barCodeScannerIntent)

                val logParams = JSONObject()
                logParams.put(URLs.kAction, "点击/扫一扫")
                ActionLogUtil.actionLog(mAppContext, logParams)
            }
        }
    }

    /*
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
        mTabKPI = findViewById(R.id.tab_kpi)
        mTabReport = findViewById(R.id.tab_report)
        mTabWorkBox = findViewById(R.id.tab_workbox)
        mTabMessage = findViewById(R.id.tab_mine)

        mTabKPI!!.setOnClickListener(mTabChangeListener)
        mTabReport!!.setOnClickListener(mTabChangeListener)
        mTabWorkBox!!.setOnClickListener(mTabChangeListener)
        mTabMessage!!.setOnClickListener(mTabChangeListener)
    }

    /**
     * @param dashboardFragmentAdapter
     */
    private fun initViewPaper() {

        mViewPager!!.adapter = DashboardFragmentAdapter(supportFragmentManager, mPagerData)
        mViewPager!!.offscreenPageLimit = mPagerData.size
        if (loadLastFragmentWhenLaunch) {
            mViewPager!!.currentItem = mSharedPreferences!!.getInt("LastTab", 0)
            mTabView[mViewPager!!.currentItem].setActive(true)
        } else {
            mTabView[0].setActive(true)
        }
        mViewPager!!.addOnPageChangeListener(this)
    }

    /*
     * Tab 栏按钮监听事件
     */
    private val mTabChangeListener = View.OnClickListener { v ->
        mTabView
                .filter { v.id == it.id }
                .forEach { mViewPager!!.currentItem = mTabView.indexOf(it) }
//        when (v.id) {
//            R.id.tab_kpi -> mViewPager!!.currentItem = PAGE_KPI
//            R.id.tab_report -> mViewPager!!.currentItem = PAGE_REPORTS
//            R.id.tab_workbox -> mViewPager!!.currentItem = PAGE_APP
//            R.id.tab_mine -> mViewPager!!.currentItem = PAGE_MINE
//            else -> {
//            }
//        }
        refreshTabView()
    }

    //重写ViewPager页面切换的处理方法
    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {
        if (state == 2) {
            mTabView[mViewPager!!.currentItem].setActive(true)
//            when (mViewPager!!.currentItem) {
//                PAGE_KPI -> mTabKPI!!.setActive(true)
//
//                PAGE_REPORTS -> mTabReport!!.setActive(true)
//
//                PAGE_APP -> mTabWorkBox!!.setActive(true)
//
//                PAGE_MINE -> mTabMessage!!.setActive(true)
//            }
        }
        refreshTabView()
        mSharedPreferences!!.edit().putInt("LastTab", mViewPager!!.currentItem).apply()
    }

    /*
     * 公告点击事件
     */
    override fun onItemClick(position: Int) {
        mTabView
                .filter { R.id.tab_mine == it.id }
                .forEach { mViewPager!!.currentItem = mTabView.indexOf(it) }
//        mViewPager!!.currentItem = PAGE_MINE
        refreshTabView()
    }

    /*
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
                            if ("" == mStoreInfoSP.getString(URLs.kStore, "")) {
                                val mStoreInfoSPEdit = mStoreInfoSP.edit()
                                mStoreInfoSPEdit.putString(URLs.kStore, data.data!![0].name)
                                mStoreInfoSPEdit.putString(URLs.kStoreIds, data.data!![0].id)
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

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    fun onNoticeItemEvent(click: NoticeBoardRequest) {
//        mViewPager!!.currentItem = PAGE_MINE
//        refreshTabView()
//    }

    /*
     * 图表点击事件统一处理方法
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onMessageEvent(items: DashboardItemBean?) {
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

    /*
     * 页面跳转事件
     */
    private fun pageLink(objTitle: String, link: String, objectId: String, templateId: String, objectType: String, paramsMappingBean: HashMap<String, String>) {
        try {
            val groupID = getSharedPreferences("UserBean", Context.MODE_PRIVATE).getString(URLs.kGroupId, "0")
            val urlString: String
            val intent: Intent

            when (templateId) {
                TEMPLATE_ONE -> {
                    intent = Intent(this, TemplateOneActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kGroupId, groupID)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    startActivity(intent)
                }
                TEMPLATE_TWO, TEMPLATE_FOUR -> {
                    intent = Intent(this, SubjectActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    startActivity(intent)
                }
                TEMPLATE_THREE -> {
                    intent = Intent(this, TemplateThreeActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            K.kBaseUrl, groupID, "3", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    startActivity(intent)
                }
                TEMPLATE_FIVE -> {
                    intent = Intent(this, TemplateFiveActivity::class.java)
                    urlString = String.format("%s/api/v1/group/%s/template/%s/report/%s/json",
                            K.kBaseUrl, groupID, "5", objectId)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    intent.putExtra("groupID", groupID)
                    intent.putExtra("urlString", urlString)
                    startActivity(intent)
                }
                TEMPLATE_SIX -> {
                    intent = Intent(this, WebApplicationActivityV6::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    intent.putExtra(URLs.kLink, link)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    startActivity(intent)
                }
                EXTERNAL_LINK -> {
                    var urlString = link
                    intent = Intent(this, WebApplicationActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra(URLs.kBannerName, objTitle)
                    for ((key, value) in paramsMappingBean) {
                        if (key == "user_num") {
                            continue
                        }
                        urlString = splitUrl(urlString, key, value)
                    }
                    intent.putExtra(URLs.kLink, urlString)
                    intent.putExtra(URLs.kObjectId, objectId)
                    intent.putExtra(URLs.kObjectType, objectType)
                    intent.putExtra(URLs.kTemplatedId, templateId)
                    startActivity(intent)
                }
                SCANNER -> {
                    intent = Intent(this, BarCodeScannerActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                    intent.putExtra("fromWorkBox", true)
//                    intent.putExtra("userNum", paramsMappingBean.user_name ?: "")
//                    intent.putExtra("groupName", paramsMappingBean.group_name ?: "")
                    startActivity(intent)
                }
                else -> showTemplateErrorDialog()
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        val logParams = JSONObject()
        if ("-1" == templateId) {
            logParams.put(URLs.kAction, "点击/" + objectTypeName[objectType.toInt() - 1] + "/链接")
        } else {
            logParams.put(URLs.kAction, "点击/" + objectTypeName[objectType.toInt() - 1] + "/报表")
        }
        logParams.put(URLs.kObjTitle, objTitle)
        logParams.put("obj_id", objectId)
        logParams.put("obj_link", link)
        ActionLogUtil.actionLog(mAppContext, logParams)
    }

    private fun splitUrl(urlString: String, paramsKey: String, paramsValue: String): String {
        val params = paramsValue + "=" + mUserSP.getString(paramsKey, "null")
        val splitString = if (urlString.contains("?")) "&" else "?"
        return String.format("%s%s%s", urlString, splitString, params)
    }

    private fun showTemplateErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(K.kPgyerUrl))
                    startActivity(browserIntent)
                }
                .setNegativeButton("稍后升级") { _, _ ->
                    // 返回 LoginActivity
                }
        builder.show()
    }
}
