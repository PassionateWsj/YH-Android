package com.intfocus.template.dashboard.mine.model;

import android.content.Context;

import com.intfocus.template.model.DaoUtil;
import com.intfocus.template.model.entity.PushMsgBean;

import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/01 下午4:56
 * e-mail: PassionateWsj@outlook.com
 * name: 推送消息数据处理类
 * desc: 根据 用户Id 查询数据库中存储的推送消息，结果回调 presenter
 * ****************************************************
 */

public class PushMessageModelImpl implements PushMessageModel {

    @Override
    public void loadData(Context context, final OnPushMessageDataResultListener listener, final int userId) {
        Observable.create(new Observable.OnSubscribe<List<PushMsgBean>>() {
            @Override
            public void call(Subscriber<? super List<PushMsgBean>> subscriber) {
                List<PushMsgBean> list = DaoUtil.INSTANCE.getPushMsgDao().queryBuilder().build().list();
                Collections.reverse(list);
                subscriber.onNext(list);
                subscriber.onCompleted();
            }
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<PushMsgBean>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        listener.onPushMessageDataResultFailure();
                    }

                    @Override
                    public void onNext(List<PushMsgBean> pushMessageBeen) {
                        listener.onPushMessageDataResultSuccess(pushMessageBeen);

                    }
                });
    }

}
