package com.intfocus.yhdev.subject.template_v1.entity.msg;

import com.intfocus.yhdev.subject.template_v1.entity.MererDetailEntity;

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
public class MDetailActRequestResult {
    public boolean isSuccess;
    public int stateCode;
    public MererDetailEntity datas;

    public MDetailActRequestResult(boolean isSuccess, int stateCode, MererDetailEntity datas) {
        this.isSuccess = isSuccess;
        this.stateCode = stateCode;
        this.datas = datas;
    }
}
