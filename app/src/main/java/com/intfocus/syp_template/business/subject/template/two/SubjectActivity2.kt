package com.intfocus.syp_template.business.subject.template.two

import android.app.AlertDialog
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import com.github.barteksc.pdfviewer.listener.OnPageErrorListener
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle
import com.intfocus.syp_template.R
import com.intfocus.syp_template.business.dashboard.mine.adapter.FilterMenuAdapter
import com.intfocus.syp_template.general.CommentActivity
import com.intfocus.syp_template.general.constant.ToastColor
import com.intfocus.syp_template.general.data.response.filter.MenuItem
import com.intfocus.syp_template.general.data.response.filter.MenuResult
import com.intfocus.syp_template.general.filter.MyFilterDialogFragment
import com.intfocus.syp_template.general.listen.UMSharedListener
import com.intfocus.syp_template.general.util.*
import com.intfocus.syp_template.general.util.URLs.kJSInterfaceName
import com.intfocus.syp_template.general.view.addressselector.FilterPopupWindow
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
import com.umeng.socialize.ShareAction
import com.umeng.socialize.bean.SHARE_MEDIA
import com.umeng.socialize.media.UMImage
import kotlinx.android.synthetic.main.activity_subject.*
import kotlinx.android.synthetic.main.item_action_bar.*
import java.io.File

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectActivity2 : AppCompatActivity(), SubjectContract.View, OnPageErrorListener, FilterMenuAdapter.FilterMenuListener, FilterPopupWindow.MenuLisenter, MyFilterDialogFragment.FilterListener {
    override lateinit var presenter: SubjectContract.Presenter
    lateinit var bannerName: String
    lateinit var reportId: String
    lateinit var templateId: String
    lateinit var groupId: String
    lateinit var url: String
    lateinit var objectType: String
    private lateinit var webView: WebView
    lateinit var popupWindow: PopupWindow

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
        SubjectPresenter(SubjectModelImpl.getInstance(), this)

        presenter.load(reportId, templateId, groupId, url)
    }

    override fun onDestroy() {
        super.onDestroy()
        SubjectModelImpl.destroyInstance()
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
        groupId = intent.getStringExtra("group_id")
        reportId = intent.getStringExtra("objectID")
        objectType = intent.getStringExtra("objectType")
        bannerName = intent.getStringExtra("bannerName")
        templateId = intent.getStringExtra("templateId")
        url = intent.getStringExtra("link")

        tv_banner_title.text = bannerName
        iv_banner_setting.setOnClickListener { launchDropMenuActivity() }
        iv_banner_back.setOnClickListener { onBackPressed() }

        if (url.toLowerCase().endsWith(".pdf")) {
            pdfview.visibility = View.INVISIBLE
        }
        else {
            if (!url.startsWith("http")) {
                initAdapter()
            }
            initWebView()
        }
    }

    private fun initWebView() {
        webView = WebView(this)
        browser.addView(webView)

        val webSettings = webView.settings
        // 允许 JS 执行
        webSettings.javaScriptEnabled = true
        webView.addJavascriptInterface(SubjectJavaScriptsInterface(this), kJSInterfaceName)
        // 缓存模式为无缓存
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        webSettings.domStorageEnabled = true
        // 允许访问文件
        webSettings.allowFileAccess = true
        // 使用 WebView 推荐的窗口
        webSettings.useWideViewPort = true
        // 设置网页自适应屏幕大小
        webSettings.loadWithOverviewMode = true
        // 显示网页滚动条
        webView.isHorizontalScrollBarEnabled = false
        // 添加 javascript 接口
        webView.addJavascriptInterface(null, null)
        // 设置是否支持缩放
        webSettings.setSupportZoom(false)
        // 设置是否支持对网页进行长按操作
        webView.setOnKeyListener { _, _, _ -> return@setOnKeyListener false }
        // 设置网页默认编码
        webSettings.defaultTextEncodingName = "utf-8"

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(p0: WebView?, p1: String?): Boolean {
                p0!!.loadUrl(url)
                return true
            }

            override fun onPageFinished(view: WebView, url: String) {
                //是否有筛选数据，有就显示出来
                if (locationDataList != null && locationDataList.isNotEmpty()) {
                    rl_address_filter.visibility = View.VISIBLE
                    LogUtil.d("location", locationDataList.size.toString())
                } else {
                    rl_address_filter.visibility = View.GONE
                }
                if (menuDatas != null && menuDatas.isNotEmpty()) {
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
        tv_location_address.text = text
    }

    override fun finishActivity() {
        PageLinkManage.pageBackIntent(this)
        finish()
    }

    override fun goBack() {
        webView.goBack()
    }

    private fun setLoadingVisibility(state: Int) {
        anim_loading.visibility = state
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
        val dialogFragment = MyFilterDialogFragment(locationDataList, this)
        dialogFragment.show(mFragTransaction, "dialogFragment") //显示一个Fragment并且给该Fragment添加一个Tag，可通过findFragmentByTag找到该Fragment
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
        //        viewBg.visibility = View.VISIBLE
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


    /**
     * 标题栏点击设置按钮显示下拉菜单
     */
    private fun launchDropMenuActivity() {
        val contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v2, null)
        val copyLinkButton = contentView.findViewById<LinearLayout>(R.id.ll_copylink)
        copyLinkButton.visibility = if (url.startsWith("http")) View.VISIBLE else View.GONE

        //设置弹出框的宽度和高度
        popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        //进入退出的动画
        popupWindow.showAsDropDown(iv_banner_setting)
    }

    /**
     * 渲染报表
     */
    override fun show(path: String) {
        url = path
        if (url.toLowerCase().endsWith(".pdf")) {
            showPDF(url)
        }
        webView.loadUrl(url)
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

    override fun onPageError(page: Int, t: Throwable?) {
        ToastUtils.show(this, "加载PDF失败, 失败原因: " + t.toString())
    }

    /**
     * 刷新
     */
    override fun refresh() {
        setLoadingVisibility(View.VISIBLE)
        show(url)
    }

    /**
     * BannerMenu 下拉菜單點擊事件
     */
    fun menuItemClick(view: View) {
        when (view.id) {
            R.id.ll_share -> actionShare2WeiXin()
            R.id.ll_comment -> actionLaunchCommentActivity()
            R.id.ll_copylink -> actionCopyLink()
            R.id.ll_refresh -> refresh()
            else -> {
            }
        }
        if (popupWindow != null && popupWindow.isShowing) {
            popupWindow.dismiss()
        }
    }

    /**
     * 分享截图至微信
     */
    private fun actionShare2WeiXin() {
        if (url.toLowerCase().endsWith(".pdf")) {
            ToastUtils.show(this, "暂不支持 PDF 分享")
            return
        }

        val bmpScreenShot = ImageUtil.takeScreenShot(this)
        if (bmpScreenShot == null) {
            ToastUtils.show(this, "截图失败")
        }

        val image = UMImage(this, bmpScreenShot!!)
        ShareAction(this)
                .withText("截图分享")
                .setPlatform(SHARE_MEDIA.WEIXIN)
                .setDisplayList(SHARE_MEDIA.WEIXIN)
                .withMedia(image)
                .setCallback(UMSharedListener())
                .open()

        /*
         * 用户行为记录, 单独异常处理，不可影响用户体验
         */
        ActionLogUtil.actionLog("分享")
    }

    /**
     * 评论
     */
    private fun actionLaunchCommentActivity() {
        val intent = Intent(this, CommentActivity::class.java)
        intent.putExtra(URLs.kBannerName, bannerName)
        intent.putExtra(URLs.kObjectId, reportId)
        intent.putExtra(URLs.kObjectType, objectType)
        startActivity(intent)
    }

    /**
     * 拷贝链接
     */
    private fun actionCopyLink() {
        val clipboardManager = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboardManager.text = url
        ToastUtils.show(this, "链接已拷贝", ToastColor.SUCCESS)
    }
}
