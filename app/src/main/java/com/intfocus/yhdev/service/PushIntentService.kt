package com.intfocus.yhdev.service

import android.content.Intent
import com.google.gson.Gson
import com.intfocus.yhdev.bean.PushMessage
import com.intfocus.yhdev.dashboard.DashboardActivity
import com.intfocus.yhdev.login.LauncherActivity
import com.intfocus.yhdev.util.LogUtil
import com.intfocus.yhdev.util.URLs
import com.mixpush.client.core.MixPushIntentService
import com.mixpush.client.core.MixPushMessage


/**
 * ****************************************************
 * author: jameswong
 * created on: 17/09/11 上午09:36
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class PushIntentService : MixPushIntentService() {
    override fun onReceivePassThroughMessage(message: MixPushMessage) {
        LogUtil.d(MixPushIntentService.TAG, "收到透传消息 -> " + message.content)
    }

    override fun onNotificationMessageClicked(message: MixPushMessage) {
        LogUtil.d(MixPushIntentService.TAG, "通知栏消息点击 -> " + message.content)
        val pushMsg = Gson().fromJson(message.content, PushMessage::class.java)
        var intent: Intent
        val isLogin = applicationContext.getSharedPreferences("UserBean", MODE_PRIVATE).getBoolean(URLs.kIsLogin, false)
        intent = if (isLogin) {
            Intent(applicationContext, DashboardActivity::class.java)
        } else {
            Intent(applicationContext, LauncherActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
//        val bundle = Bundle()
//        bundle.putString("message", message.content)
//        bundle.putString("message_body_title", pushMsg.body!!.title)
//        bundle.putString("message_body_text", pushMsg.body!!.text)
        intent.putExtra("msgData", pushMsg)
        startActivity(intent)
    }
}