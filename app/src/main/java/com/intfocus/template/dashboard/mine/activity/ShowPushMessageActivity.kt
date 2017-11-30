package com.intfocus.template.dashboard.mine.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.google.gson.Gson
import com.intfocus.template.R
import com.intfocus.template.dashboard.mine.adapter.ShowPushMessageAdapter
import com.intfocus.template.dashboard.mine.bean.PushMessageBean
import com.intfocus.template.dashboard.mine.presenter.PushMessagePresenter
import com.intfocus.template.dashboard.mine.view.PushMessageView
import com.intfocus.template.subject.one.NativeReportActivity
import com.intfocus.template.subject.three.MultiIndexActivity
import com.intfocus.template.subject.two.WebPageActivity
import com.intfocus.template.model.entity.User
import com.intfocus.template.ConfigConstants
import com.intfocus.template.constant.Params.ACTION
import com.intfocus.template.constant.Params.BANNER_NAME
import com.intfocus.template.constant.Params.GROUP_ID
import com.intfocus.template.constant.Params.LINK
import com.intfocus.template.constant.Params.OBJECT_ID
import com.intfocus.template.constant.Params.OBJECT_TITLE
import com.intfocus.template.constant.Params.OBJECT_TYPE
import com.intfocus.template.util.*
import kotlinx.android.synthetic.main.activity_show_push_message.*
import org.json.JSONException
import org.json.JSONObject
import rx.Subscription
import java.io.File

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:56
 * e-mail: PassionateWsj@outlook.com
 * name: 推送消息显示 Activity
 * desc: 根据 用户Id 显示推送消息的 activity
 * ****************************************************
 */

class ShowPushMessageActivity : AppCompatActivity(), PushMessageView, ShowPushMessageAdapter.OnPushMessageListener {
    /**
     * 当前用户id
     */
    private var mUserID = 0
    /**
     * 显示消息的 mAdapter
     */
    val adapter = ShowPushMessageAdapter(this, this)
    /**
     * RxBus 接收推送消息的类，需要手动取消注册释放资源
     */
    lateinit var subscribe: Subscription
    /**
     * mPresenter 类
     */
    lateinit var presenter: PushMessagePresenter

    private var objectTypeName = arrayOf("生意概况", "报表", "工具箱")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_push_message)

        // 初始化数据
        initData()
        // 初始化适配器
        initAdapter()
    }

    private fun initData() {
        // 获取 User 信息
        val userConfigPath = String.format("%s/%s", FileUtil.basePath(this), K.K_USER_CONFIG_FILE_NAME)
        if (File(userConfigPath).exists()) {
            val user = Gson().fromJson(FileUtil.readConfigFile(userConfigPath).toString(), User::class.java)
            if (user!!.isIs_login) {
                mUserID = user.user_id
            }
        }
        // 请求数据
        presenter = PushMessagePresenter(applicationContext, this, mUserID)
        presenter.loadData()

        // RxBus接收到推送信息，处理数据列表更新
        subscribe = RxBusUtil.getInstance().toObservable(String::class.java)
                .subscribe { msg ->
                    if ("UpDatePushMessage" == msg) presenter.loadData()
                }
    }

    private fun initAdapter() {
        val mLayoutManager = LinearLayoutManager(this)
        mLayoutManager.orientation = LinearLayoutManager.VERTICAL
        rv_show_push_message.layoutManager = mLayoutManager
        rv_show_push_message.adapter = adapter
    }

    /**
     * 请求数据失败的回调方法
     */
    override fun onResultFailure() {
        ToastUtils.show(this, "没有找到数据")
    }

    /**
     * 请求数据成功的回调方法
     */
    override fun onResultSuccess(data: MutableList<PushMessageBean>?) {
        adapter.setData(data!!)
    }

    /**
     * 消息 item 的点击回调处理方法
     */
    override fun onItemClick(position: Int) {
        // 更新点击状态
        val pushMessageBean = adapter.mData[position]

        // 重新获取数据
        presenter.loadData()

        // 通知 mAdapter 刷新数据
        adapter.notifyDataSetChanged()

        // 点击 item 判断类型 进行页面跳转
        val intent = Intent(this, PushMessageContentActivity::class.java)
        intent.putExtra("push_message_bean", pushMessageBean)
        if ("report" == pushMessageBean.type) {
            // 跳转到报表页面
            pageLink(pushMessageBean.title + "", pushMessageBean.url + "", 1, 1)
        } else {
            startActivity(intent)
        }
    }

    /**
     * 页面跳转事件
     */
    private fun pageLink(mBannerName: String, link: String, objectId: Int, objectType: Int) =
            if (link.indexOf("template") > 0 && link.indexOf("group") > 0) {
                try {
                    val groupID = getSharedPreferences("UserBean", Context.MODE_PRIVATE).getInt(GROUP_ID, 0)
                    val intent: Intent

                    when {
                        link.indexOf("template/2") > 0 -> {
                            intent = Intent(this, WebPageActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(BANNER_NAME, mBannerName)
                            intent.putExtra(LINK, link)
                            intent.putExtra(OBJECT_ID, objectId)
                            intent.putExtra(OBJECT_TYPE, objectType)
                            intent.putExtra(GROUP_ID, groupID)
                            startActivity(intent)
                        }
                        link.indexOf("template/4") > 0 -> {
                            intent = Intent(this, WebPageActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(BANNER_NAME, mBannerName)
                            intent.putExtra(LINK, link)
                            intent.putExtra(OBJECT_ID, objectId)
                            intent.putExtra(OBJECT_TYPE, objectType)
                            intent.putExtra(GROUP_ID, groupID)
                            startActivity(intent)
                        }
                        link.indexOf("template/3") > 0 -> {
                            intent = Intent(this, MultiIndexActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(BANNER_NAME, mBannerName)
                            intent.putExtra(LINK, link)
                            intent.putExtra(OBJECT_ID, objectId)
                            intent.putExtra(OBJECT_TYPE, objectType)
                            intent.putExtra(GROUP_ID, groupID)
                            startActivity(intent)
                        }
                        link.indexOf("template/1") > 0 -> {
                            intent = Intent(this, NativeReportActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                            intent.putExtra(BANNER_NAME, mBannerName)
                            intent.putExtra(LINK, link)
                            intent.putExtra(OBJECT_ID, objectId)
                            intent.putExtra(OBJECT_TYPE, objectType)
                            intent.putExtra(GROUP_ID, groupID)
                            startActivity(intent)
                        }
                        else -> showTemplateErrorDialog()
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }

                val logParams = JSONObject()
                logParams.put(ACTION, "点击/" + objectTypeName[objectType - 1] + "/报表")
                logParams.put(OBJECT_TITLE, mBannerName)
                ActionLogUtil.actionLog(logParams)
            } else {
                val intent = Intent(this, WebPageActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(BANNER_NAME, mBannerName)
                intent.putExtra(LINK, link)
                intent.putExtra(OBJECT_ID, objectId)
                intent.putExtra(OBJECT_TYPE, objectType)
                startActivity(intent)

                val logParams = JSONObject()
                logParams.put(ACTION, "点击/生意概况/链接")
                logParams.put(OBJECT_TITLE, mBannerName)
                ActionLogUtil.actionLog(logParams)
            }

    private fun showTemplateErrorDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("温馨提示")
                .setMessage("当前版本暂不支持该模板, 请升级应用后查看")
                .setPositiveButton("前去升级") { _, _ ->
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ConfigConstants.kPgyerUrl))
                    startActivity(browserIntent)
                }
                .setNegativeButton("稍后升级") { _, _ ->
                    // 返回 LoginActivity
                }
        builder.show()
    }

    fun back(v: View?) {
        finish()
    }

    /**
     * 释放资源
     */
    override fun onDestroy() {
        super.onDestroy()
        if (subscribe.isUnsubscribed)
            subscribe.unsubscribe()
    }
}
