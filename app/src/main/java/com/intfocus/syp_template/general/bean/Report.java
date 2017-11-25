package com.intfocus.syp_template.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * @author liuruilin
 * @data 2017/11/10
 * @describe
 */
@Entity
public class Report {
    /**
     * id
     */
    @Id (autoincrement = true)
    Long id;

    /**
     * 报表唯一标识, 由 reportId + templateI + groupId 组成
     */
    String uuid;

    /**
     * 报表筛选条件
     */
    String name;

    /**
     * 报表组件id
     */
    int index;

    /**
     * 报表根页签名
     */
    String page_title;

    /**
     * 组件类型
     * 整包数据对应的类型为: main_data
     */
    String type;

    /**
     * JSON 格式的配置信息
     */
    String config;
}
