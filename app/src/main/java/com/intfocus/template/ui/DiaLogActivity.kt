package com.intfocus.template.ui

import android.app.Activity
import android.os.Bundle
import com.intfocus.template.R
import com.intfocus.template.model.entity.PushMsgBean
import com.intfocus.template.ui.view.SelfDialog
import com.intfocus.template.util.PageLinkManage

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
        if (pushMsg.template_id != null && pushMsg.template_id != "") {
            selfDialog.setYesOnclickListener("查看") {
                PageLinkManage.pageLink(this, pushMsg)
                selfDialog.dismiss()
                this.finish()
            }
            selfDialog.setNoOnclickListener("取消") {
                selfDialog.dismiss()
                this.finish()
            }
        } else {
            selfDialog.setYesOnclickListener("知道了"){
                selfDialog.dismiss()
                this.finish()
            }
        }
        selfDialog.show()
    }
}
