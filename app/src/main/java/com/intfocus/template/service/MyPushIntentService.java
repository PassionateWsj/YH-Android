package com.intfocus.template.service;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.intfocus.template.model.DaoUtil;
import com.intfocus.template.model.entity.PushMsgBean;
import com.intfocus.template.ui.DiaLogActivity;
import com.intfocus.template.util.TimeUtils;
import com.umeng.message.UTrack;
import com.umeng.message.UmengMessageService;
import com.umeng.message.entity.UMessage;

import org.android.agoo.common.AgooConstants;
import org.json.JSONObject;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/30 下午1:19
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class MyPushIntentService extends UmengMessageService {
    private static final String TAG = MyPushIntentService.class.getName();

    @Override
    public void onMessage(Context context, Intent intent) {
        try {
            String message = intent.getStringExtra(AgooConstants.MESSAGE_BODY);
            UMessage uMessage = new UMessage(new JSONObject(message));
            UTrack.getInstance(context).trackMsgClick(uMessage);

            Intent intent1 = new Intent();
            intent1.setClass(context, NotificationService.class);
            intent1.putExtra("UmengMsg", message);
            context.startService(intent1);

            PushMsgBean pushMsg = com.alibaba.fastjson.JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
            if (pushMsg == null) {
                pushMsg = new PushMsgBean();
                pushMsg.setDebug_timestamp(TimeUtils.getStringDate() + " +0800");
            }
            pushMsg.setTicker(uMessage.ticker);
            pushMsg.setBody_title(uMessage.title);
            pushMsg.setText(uMessage.text);
            pushMsg.setNew_msg(true);
            DaoUtil.INSTANCE.getPushMsgDao().insert(pushMsg);

            Intent diaLogIntent = new Intent(context, DiaLogActivity.class);
            diaLogIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            diaLogIntent.putExtra("body_title", pushMsg.getBody_title());
            diaLogIntent.putExtra("title", pushMsg.getTitle());
            diaLogIntent.putExtra("url", pushMsg.getUrl());
            diaLogIntent.putExtra("text", pushMsg.getText());
            diaLogIntent.putExtra("obj_id", pushMsg.getObj_id());
            diaLogIntent.putExtra("template_id", pushMsg.getTemplate_id());
            diaLogIntent.putExtra("params_mapping", pushMsg.getParams_mapping());
            startActivity(diaLogIntent);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
