package com.intfocus.syp_template.business.dashboard.mine

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import com.intfocus.syp_template.R
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.util.K
import com.tencent.smtt.sdk.WebSettings
import com.tencent.smtt.sdk.WebView
import com.tencent.smtt.sdk.WebViewClient
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
        var link = String.format("%s/mobile/v2/user/%s/article/%s", ConfigConstants.kBaseUrl, mUserSP.getString(K.K_USER_ID, "0").toString(), institute_id)
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
        mWebView.webViewClient = object : WebViewClient() {
            override fun onPageFinished(view: WebView?, url: String?) {
                anim_loading.visibility = View.GONE
                super.onPageFinished(view, url)
            }
        }
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