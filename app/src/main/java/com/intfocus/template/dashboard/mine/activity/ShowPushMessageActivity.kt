package com.intfocus.template.dashboard.mine.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import com.alibaba.fastjson.TypeReference
import com.google.gson.Gson
import com.intfocus.template.R
import com.intfocus.template.dashboard.mine.adapter.ShowPushMessageAdapter
import com.intfocus.template.dashboard.mine.presenter.PushMessagePresenter
import com.intfocus.template.dashboard.mine.view.PushMessageView
import com.intfocus.template.model.entity.PushMsgBean
import com.intfocus.template.model.entity.User
import com.intfocus.template.util.FileUtil
import com.intfocus.template.util.K
import com.intfocus.template.util.PageLinkManage
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.activity_show_push_message.*
import rx.Subscription
import java.io.File
import java.util.*

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
//        subscribe = RxBusUtil.getInstance().toObservable(String::class.java)
//                .subscribe {
//                    if ("UpDatePushMessage" == it) presenter.loadData()
//                }
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
    override fun onResultSuccess(data: MutableList<PushMsgBean>?) {
        adapter.setData(data!!)
    }

    /**
     * 消息 item 的点击回调处理方法
     */
    override fun onItemClick(position: Int) {
        // 更新点击状态
        val pushMsg = adapter.mData[position]

        // 重新获取数据
        presenter.loadData()

        // 通知 mAdapter 刷新数据
        adapter.notifyDataSetChanged()
        val paramsMappingBean = com.alibaba.fastjson.JSONObject.parseObject(pushMsg.params_mapping, object : TypeReference<HashMap<String, String>>() {
        })
        var templateId = ""
        if (pushMsg.template_id == null || "" == pushMsg.template_id) {
            val temp = pushMsg.url.split("/".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            for (i in temp.indices) {
                if ("template" == temp[i] && i + 1 < temp.size) {
                    templateId = temp[i + 1]
                    break
                }
            }
        } else {
            templateId = pushMsg.template_id
        }
        PageLinkManage.pageLink(this, pushMsg.title, pushMsg.url, pushMsg.obj_id, templateId, "4", paramsMappingBean, false)

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
