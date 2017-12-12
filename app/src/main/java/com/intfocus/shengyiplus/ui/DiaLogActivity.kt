package com.intfocus.shengyiplus.ui

import android.app.Activity
import android.os.Bundle
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.model.entity.PushMsgBean
import com.intfocus.shengyiplus.ui.view.SelfDialog
import com.intfocus.shengyiplus.util.PageLinkManage

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/07 下午0:00
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class DiaLogActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dialog)

        val pushMsg = PushMsgBean()

        pushMsg.body_title = intent.getStringExtra("body_title")
        pushMsg.title = intent.getStringExtra("title")
        pushMsg.url = intent.getStringExtra("url")
        pushMsg.obj_id = intent.getStringExtra("obj_id")
        pushMsg.template_id = intent.getStringExtra("template_id")
        pushMsg.params_mapping = intent.getStringExtra("params_mapping")

        val selfDialog = SelfDialog(this)
        selfDialog.setTitle("收到通知")
        selfDialog.setMessage(pushMsg.body_title)
        selfDialog.setYesOnclickListener("查看") {
            PageLinkManage.pageLink(this, pushMsg)
            selfDialog.dismiss()
            this.finish()
        }
        selfDialog.setNoOnclickListener("取消") {
            selfDialog.dismiss()
            this.finish()
        }
        selfDialog.show()
    }
}
