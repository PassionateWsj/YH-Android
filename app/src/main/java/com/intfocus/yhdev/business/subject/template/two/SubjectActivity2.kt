package com.intfocus.yhdev.business.subject.template.two

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.tencent.smtt.sdk.WebView
import com.intfocus.yhdev.R
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebViewClient
import kotlinx.android.synthetic.main.activity_subject.*

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectActivity2 : AppCompatActivity(), SubjectContract.View {
    override lateinit var presenter: SubjectContract.Presenter
    lateinit var bannerName: String
    lateinit var reportId: String
    lateinit var templateId: String
    lateinit var groupId: String
    lateinit var url: String
    lateinit var objectType: String
    lateinit var webView: WebView

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

    private fun init() {
        groupId = intent.getStringExtra("group_id")
        reportId = intent.getStringExtra("objectID")
        objectType = intent.getStringExtra("objectType")
        bannerName = intent.getStringExtra("bannerName")
        url = intent.getStringExtra("link")

        initWebView()
    }

    private fun initWebView() {
        webView = WebView(this)
        browser.addView(webView)

        val webSettings = webView.settings

        // 允许 JS 执行
        webSettings.javaScriptEnabled = true
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
            }
        }
    }

    override fun show(path: String) {

    }
}