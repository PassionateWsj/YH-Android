package com.intfocus.yhdev.business.subject.template.two

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.intfocus.yhdev.R

/**
 * @author liuruilin
 * @data 2017/11/15
 * @describe
 */
class SubjectActivity: AppCompatActivity(), SubjectContract.View {
    override lateinit var presenter: SubjectContract.Presenter
    lateinit var bannerName: String
    lateinit var reportId: String
    lateinit var templateId: String
    lateinit var groupId: String
    lateinit var url: String
    lateinit var objectType: String

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

    }

    override fun show(path: String) {

    }
}