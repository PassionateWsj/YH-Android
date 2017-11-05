package com.intfocus.syp_template.business.dashboard.mine

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.intfocus.syp_template.R
import com.intfocus.syp_template.general.util.K
import kotlinx.android.synthetic.main.activity_institute_content.*

class InstituteContentActivity : Activity() {
    lateinit var ctx: Context
    lateinit var mWebView: WebView
    var institute_id = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_institute_content)
        ctx = this
        initWebView()
        var mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        var intent = intent
        institute_id = intent.getStringExtra("id")
        tv_banner_title.text = "数据学院"
        var link = String.format("%s/mobile/v2/user/%s/article/%s", K.kBaseUrl, mUserSP.getString(K.kUserId, "0").toString(), institute_id)
//        var link = "https://ssl.sunny-tech.com/mobile_v2_group_165_template_2_report_67.html?from=groupmessage&isappinstalled=0";
        mWebView.loadUrl(link)
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun initWebView() {
        mWebView = WebView(this.applicationContext)
        wv_institute_view.addView(mWebView, 0)

        var webSettings = mWebView.settings
        webSettings.javaScriptEnabled = true
        webSettings.defaultTextEncodingName = "utf-8"
        webSettings.cacheMode = WebSettings.LOAD_NO_CACHE
        mWebView.setWebViewClient(object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                anim_loading.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    /**
     * 返回
     */
    fun dismissActivity(v: View) {
        this@InstituteContentActivity.onBackPressed()
    }
}
