package com.intfocus.yhdev.subject.template_v1.entity.msg

import com.intfocus.yhdev.subject.template_v1.entity.MererDetailEntity

/**
 * 仪表数据详情页面请求结果
 * Created by zbaoliang on 17-4-28.
 */
class MDetailActRequestResult(var isSuccess: Boolean, var stateCode: Int, var datas: MererDetailEntity)
