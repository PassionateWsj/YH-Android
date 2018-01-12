package com.intfocus.template.subject.two

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.KeyEvent
import android.view.View
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.intfocus.template.BuildConfig
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.JAVASCRIPT_INTERFACE_NAME
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.constant.Params.TEMPLATE_ID
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.dashboard.mine.adapter.FilterMenuAdapter
import com.intfocus.template.filter.FilterDialogFragment
import com.intfocus.template.model.response.filter.MenuItem
import com.intfocus.template.model.response.filter.MenuResult
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.ui.view.addressselector.FilterPopupWindow
import com.intfocus.template.util.*
import com.tencent.smtt.sdk.*
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.item_action_bar.*
import java.io.File

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class WebPageActivity : BaseActivity(), WebPageContract.View, OnPageErrorListener,
        FilterMenuAdapter.FilterMenuListener, FilterPopupWindow.MenuLisenter,
        FilterDialogFragment.FilterListener, CleanCacheCallback {

    private val REQUEST_CODE_CHOOSE = 1
    private var errorCount: Int = 0
    override lateinit var presenter: WebPageContract.Presenter
    lateinit var bannerName: String
    lateinit var reportId: String
    lateinit var templateId: String
    lateinit var groupId: String
    lateinit var url: String
    lateinit var objectType: String
    private var mWebView: WebView? = null
    private var mOverrideKeyCodes = intArrayOf(
            KeyEvent.KEYCODE_DPAD_CENTER,
            KeyEvent.KEYCODE_DPAD_UP,
            KeyEvent.KEYCODE_DPAD_LEFT,
            KeyEvent.KEYCODE_DPAD_DOWN,
            KeyEvent.KEYCODE_DPAD_RIGHT
    )

    /**
     * 图片上传接收参数
     */
    private var uploadFile: ValueCallback<Uri>? = null
    private var uploadFiles: ValueCallback<Array<Uri>>? = null

    /**
     * 地址选择
     */
    var locationDataList: ArrayList<MenuItem> = arrayListOf()
    var msg: MenuResult? = null

    /**
     * 菜单
     */
    var currentPosition = 0 //当前展开的menu
    var menuDatas: List<MenuItem> = arrayListOf()
    lateinit var menuAdapter: FilterMenuAdapter
    lateinit var filterPopupWindow: FilterPopupWindow

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_subject)
        init()
        WebPagePresenter(WebPageModelImpl.getInstance(), this)

        presenter.load(reportId, templateId, groupId, url)

        if (BuildConfig.FLAVOR == "template") {
            rl_action_bar.post { setBannerVisibility(View.GONE) }
            ll_filter.post { ll_filter.visibility = View.GONE }
        }
    }

    override fun setBannerTitle(title: String) {
        runOnUiThread { tv_banner_title.text = title }
    }

    override fun setBannerMenuVisibility(state: Int) {
        iv_banner_setting.visibility = state
    }

    override fun setBannerVisibility(state: Int) {
        rl_action_bar.visibility = state
    }

    override fun setBannerBackVisibility(state: Int) {
        iv_banner_back.visibility = state
    }

    override fun setAddressFilterText(text: String) {
        if (null != tv_location_address) {
            tv_location_address.text = text
        }
    }

    private fun setLoadingVisibility(state: Int) {
        anim_loading.visibility = state
    }

    override fun finishActivity() {
        PageLinkManage.pageBackIntent(this)
        WebPageModelImpl.destroyInstance()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        mWebView?.destroy()
    }

    override fun dismissActivity(v: View) {
        onBackPressed()
    }

    override fun onBackPressed() {
        if (url.startsWith("http")) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("温馨提示")
                    .setMessage("退出当前页面?")
                    .setPositiveButton("确认") { _, _ ->
                        PageLinkManage.pageBackIntent(this)
                        finish()
                    }
                    .setNegativeButton("取消") { _, _ ->
                    }
            builder.show()
        } else {
            PageLinkManage.pageBackIntent(this)
            finish()
        }
    }

    private fun init() {
        groupId = intent.getStringExtra(GROUP_ID)
        reportId = intent.getStringExtra(OBJECT_ID)
        objectType = intent.getStringExtra(OBJECT_TYPE)
        bannerName = intent.getStringExtra(BANNER_NAME)
        templateId = intent.getStringExtra(TEMPLATE_ID)
        url = intent.getStringExtra(LINK)

        tv_banner_title.text = bannerName
        iv_banner_setting.setOnClickListener { launchDropMenuActivity(url) }
        iv_banner_back.setOnClickListener { onBackPressed() }

        if (url.toLowerCase().endsWith(".pdf")) {
            pdfview.visibility = View.INVISIBLE
        } else {
            if (!url.startsWith("http")) {
                initAdapter()
            }
            initWebView()
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun initWebView() {
        mWebView = WebView(this)
        browser.addView(mWebView)

        val webSettings = mWebView?.settings
        // 允许 JS 执行
        webSettings?.javaScriptEnabled = true
        mWebView?.setDownloadListener { url, userAgent, contentDisposition, mimetype, contentLength ->
            LogUtil.d(this, "url=" + url)
            LogUtil.d(this, "userAgent=" + userAgent)
            LogUtil.d(this, "contentDisposition=" + contentDisposition)
            LogUtil.d(this, "mimetype=" + mimetype)
            LogUtil.d(this, "contentLength=" + contentLength)
            val uri = Uri.parse(url)
            startActivity(Intent(Intent.ACTION_VIEW, uri))
        }
        // 缓存模式为无缓存
        webSettings?.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings?.domStorageEnabled = true
        // 允许访问文件
        webSettings?.allowFileAccess = true
        // 使用 WebView 推荐的窗口
        webSettings?.useWideViewPort = true
        // 设置网页自适应屏幕大小
        webSettings?.loadWithOverviewMode = true
        // 显示网页滚动条
        mWebView?.isHorizontalScrollBarEnabled = false
        // 添加 javascript 接口
        mWebView?.addJavascriptInterface(CustomJavaScriptsInterface(this), JAVASCRIPT_INTERFACE_NAME)
        // 设置是否支持缩放
        webSettings?.setSupportZoom(false)
        // 设置是否支持对网页进行长按操作
        mWebView?.setOnKeyListener { _, _, _ -> return@setOnKeyListener false }
        // 设置网页默认编码
        webSettings?.defaultTextEncodingName = "utf-8"

        // 变更 UserAgent
        if (BuildConfig.FLAVOR == "template") {
            val userAgentWithNewChromeVersion = Regex("Chrome/[0-9.]*\\s+").replace(webSettings?.userAgentString!!, "Chrome/63.0.3239.132 ")
            LogUtil.d(this, "UserAgent ::: $userAgentWithNewChromeVersion ")
            webSettings.setUserAgent(userAgentWithNewChromeVersion.replace("MQQBrowser/6.2 TBS/043805 ", "").replace("Version/4.0 ","").replace("; wv",""))
        }
        mWebView?.webChromeClient = object : WebChromeClient() {
            // For Android  > 4.1.1
            override fun openFileChooser(uploadMsg: ValueCallback<Uri>, acceptType: String?, capture: String?) {
                uploadFile = uploadMsg
                imageSelect()
            }

            // For Android  >= 5.0
            override fun onShowFileChooser(webView: com.tencent.smtt.sdk.WebView?,
                                           filePathCallback: ValueCallback<Array<Uri>>?,
                                           fileChooserParams: WebChromeClient.FileChooserParams?): Boolean {
                uploadFiles = filePathCallback
                imageSelect()
                return true
            }
        }

        mWebView?.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                p0!!.loadUrl(p1)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {

                //是否有筛选数据，有就显示出来
                if (locationDataList.isNotEmpty()) {
                    rl_address_filter.visibility = View.VISIBLE
                    LogUtil.d("location", locationDataList.size.toString())
                } else {
                    rl_address_filter.visibility = View.GONE
                }
                if (menuDatas.isNotEmpty()) {
                    LogUtil.d("faster_select", menuDatas.size.toString())
                    filter_recycler_view.visibility = View.VISIBLE
                    view_line.visibility = View.VISIBLE
                    menuAdapter.setData(menuDatas)
                } else {
                    filter_recycler_view.visibility = View.GONE
                    view_line.visibility = View.GONE
                }
                setLoadingVisibility(View.GONE)
            }
        }

    }

    /**
     * 渲染报表
     */
    override fun show(path: String) {
        url = path
        if (url.toLowerCase().endsWith(".pdf")) {
            showPDF(url)
        }
        mWebView?.loadUrl(url)
    }

    /**
     * 错误页面
     */
    override fun showError(errorPagePath: String) {
        errorCount += 1
        when (errorCount) {
            1, 2 -> {
                ApiHelper.deleteHeadersFile()
                mWebView?.loadUrl(errorPagePath)
            }
            3 -> {
                AlertDialog.Builder(this)
                        .setTitle("提示")
                        .setMessage("报表多次加载错误, 是否清理缓存后继续加载?")
                        .setPositiveButton("确定") { _, _ ->
                            CacheCleanManager.clearAppUserCache(this)
                            errorCount = 0
                        }
                        .setNegativeButton("下一次") { dialog, _ ->
                            errorCount = 2
                            dialog.dismiss()
                        }
                        .setCancelable(false)
                        .show()
                mWebView?.loadUrl(errorPagePath)
            }
        }
    }

    /**
     * 渲染 PDF 文件
     */
    override fun showPDF(path: String) {
        val pdfFile = File(path)
        if (pdfFile.exists()) {
            pdfview.fromFile(pdfFile)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .scrollHandle(DefaultScrollHandle(this))
                    .spacing(10) // in dp
                    .onPageError(this)
                    .load()

            browser.visibility = View.GONE
            pdfview.visibility = View.VISIBLE
        } else {
            ToastUtils.show(this, "加载PDF失败")
        }
    }

    /**
     * 刷新
     */
    override fun refresh() {
        setLoadingVisibility(View.VISIBLE)
        show(url)
    }

    /**
     * 刷新
     */
    fun refreshWithClearCache() {
        setLoadingVisibility(View.VISIBLE)
        CacheCleanManager.clearAppUserCache(this, this)
    }
    override fun onCleanCacheSuccess() {
        presenter.load(reportId, templateId, groupId, url)
    }

    override fun onCleanCacheFailure() {
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == Params.REQUEST_CODE_CHOOSE && resultCode == Activity.RESULT_OK) {
            if (null != uploadFile) {
                val result = if (resultCode != Activity.RESULT_OK) null else Matisse.obtainResult(intent)

                uploadFile!!.onReceiveValue(result!![0])
                uploadFile = null
            }
            if (null != uploadFiles) {
                val result = if (resultCode != Activity.RESULT_OK) null else Matisse.obtainResult(intent)

                uploadFiles!!.onReceiveValue(arrayOf(result!![0]))
                uploadFiles = null
            }
        } else {
            if (null != uploadFile) {
                uploadFile!!.onReceiveValue(null)
                uploadFile = null
            } else if (null != uploadFiles) {
                uploadFiles!!.onReceiveValue(null)
                uploadFiles = null
            }
        }
    }

    override fun goBack() {
        mWebView?.goBack()
    }

    private fun imageSelect() {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(1)
                .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE)
    }

    /**
     * 初始化筛选栏适配器
     */
    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        filter_recycler_view.layoutManager = mLayoutManager
        menuAdapter = FilterMenuAdapter(this, menuDatas, this)
        filter_recycler_view.adapter = menuAdapter
        tv_address_filter.setOnClickListener { showDialogFragment() }
    }

    private fun showDialogFragment() {
        val mFragTransaction = supportFragmentManager.beginTransaction()
        val fragment = supportFragmentManager.findFragmentByTag("dialogFragment")
        if (fragment != null) {
            //为了不重复显示dialog，在显示对话框之前移除正在显示的对话框
            mFragTransaction.remove(fragment)
        }
        val dialogFragment = FilterDialogFragment.newInstance(locationDataList)
        dialogFragment!!.show(mFragTransaction, "dialogFragment") //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
    }

    /**
     * 点击普通筛选栏
     */
    override fun itemClick(position: Int) {
        //标记点击位置
        menuDatas[position].arrorDirection = true
        menuAdapter.setData(menuDatas)
        currentPosition = position
        showMenuPop(position)
    }

    private fun showMenuPop(position: Int) {
        if (filterPopupWindow == null) {
            initMenuPopup(position)
        } else {
            filterPopupWindow.upDateDatas(menuDatas[position].data!!)
        }
        filterPopupWindow.showAsDropDown(view_line)
        filterPopupWindow.setOnDismissListener {
            for (menuItem in menuDatas) {
                menuItem.arrorDirection = false
            }
            menuAdapter.setData(menuDatas)
        }
    }

    private fun initMenuPopup(position: Int) {
        filterPopupWindow = FilterPopupWindow(this, menuDatas[position].data!!, this)
        filterPopupWindow.init()
    }

    /**
     * 普通排序列表点击
     *
     * @param position
     */
    override fun menuItemClick(position: Int) {
        for (menuItem in menuDatas[currentPosition].data!!) {
            menuItem.arrorDirection = false
        }

        //标记点击位置
        menuDatas[currentPosition].data!![position].arrorDirection = true
        filterPopupWindow.dismiss()
    }

    override fun complete(data: java.util.ArrayList<MenuItem>) {
        var addStr = ""
        val size = data.size
        for (i in 0 until size) {
            addStr += data[i].name!! + "||"
        }

        addStr = addStr.substring(0, addStr.length - 2)
        val selectedItemPath = String.format("%s.selected_item", FileUtil.reportJavaScriptDataPath(this, groupId, templateId, reportId))
        FileUtil.writeFile(selectedItemPath, addStr)

        refresh()
    }

    override fun onPageError(page: Int, t: Throwable?) {
        ToastUtils.show(this, "加载PDF失败, 失败原因: " + t.toString())
    }

    /**
     * BannerMenu 下拉菜單點擊事件
     */
    fun menuItemClick(view: View) {
        when (view.id) {
            R.id.ll_share -> share(this, url)
            R.id.ll_comment -> comment(this, reportId, objectType, bannerName)
            R.id.ll_copylink -> actionCopyLink()
            R.id.ll_refresh -> refreshWithClearCache()
            else -> {
            }
        }
        if (popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * 拷贝链接
     */
    private fun actionCopyLink() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.text = url
        ToastUtils.show(this, "链接已拷贝", ToastColor.SUCCESS)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        when (keyCode) {
            KeyEvent.KEYCODE_BACK -> {
                mWebView?.let {
                    if (it.canGoBack()) {
                        it.goBack()
                    } else {
                        onBackPressed()
                    }
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    /**
     * Given a key code, this method will pass it into the web view to handle
     * accordingly
     *
     * @param keycode Native Android KeyCode
     */
    fun handleKeyInjection(keycode: Int) {

        val jsSend = ("javascript:androidKeyHandler.handleUri('nativewebsample://KEY_EVENT;"
                + keycode + ";');")
        loadJavascriptAction(jsSend)
    }

    private fun loadJavascriptAction(jsSend: String) {
        mWebView?.loadUrl(jsSend)
    }

    private fun addAndroidKeyHandler() {
        val androidKeyHandler = LoadAssetsJsonUtil.getAssetsJsonData("androidKeyHandler.js")
        mWebView?.loadUrl("javascript:(function(){$androidKeyHandler})()")
    }

//    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
//        if (event.keyCode == KeyEvent.KEYCODE_R) {
//            refresh()
//        }
//
//        val eventKeyCode = event.keyCode
//        for (i in 0 until mOverrideKeyCodes.size) {
//            if (eventKeyCode == mOverrideKeyCodes[i]) {
//                if (event.action == KeyEvent.ACTION_UP) {
//                    handleKeyInjection(eventKeyCode)
//                }
//                return true
//            }
//        }
//
//        return super.dispatchKeyEvent(event)
//    }
}
