package com.intfocus.template.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.intfocus.template.model.entity.PushMsgBean;
import com.intfocus.template.util.PageLinkManage;
import com.intfocus.template.util.TimeUtils;
import com.umeng.message.UTrack;
import com.umeng.message.common.UmLog;
import com.umeng.message.entity.UMessage;

import org.json.JSONObject;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/30 下午2:45
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class NotificationBroadcast extends BroadcastReceiver {
    public static final String EXTRA_KEY_ACTION = "ACTION";
    public static final String EXTRA_KEY_MSG = "MSG";
    public static final int ACTION_CLICK = 10;
    public static final int ACTION_DISMISS = 11;
    public static final int EXTRA_ACTION_NOT_EXIST = -1;
    private static final String TAG = NotificationBroadcast.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra(EXTRA_KEY_MSG);
        int action = intent.getIntExtra(EXTRA_KEY_ACTION,
                EXTRA_ACTION_NOT_EXIST);
        try {
            UMessage uMessage = (UMessage) new UMessage(new JSONObject(message));

            switch (action) {
                case ACTION_DISMISS:
                    UmLog.d(TAG, "dismiss notification");
                    UTrack.getInstance(context).setClearPrevMessage(true);
                    UTrack.getInstance(context).trackMsgDismissed(uMessage);
                    break;
                case ACTION_CLICK:
                    UmLog.d(TAG, "click notification");
                    UTrack.getInstance(context).setClearPrevMessage(true);
                    NotificationService.oldMessage = null;
                    UTrack.getInstance(context).trackMsgClick(uMessage);

                    PushMsgBean pushMsg = com.alibaba.fastjson.JSONObject.parseObject(uMessage.custom, PushMsgBean.class);
                    if (pushMsg == null) {
                        pushMsg = new PushMsgBean();
                        pushMsg.setDebug_timestamp(TimeUtils.getStringDate() + " +0800");
                    }
                    pushMsg.setTicker(uMessage.ticker);
                    pushMsg.setBody_title(uMessage.title);
                    pushMsg.setText(uMessage.text);
                    pushMsg.setNew_msg(true);

                    PageLinkManage.pageLink(context, pushMsg);
                    break;
                default:
                    break;
            }
            //
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}