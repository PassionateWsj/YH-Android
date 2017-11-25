package com.intfocus.syp_template.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author liuruilin
 * @data 2017/10/27
 * @describe 信息采集模板 -> 组件采集信息存储表
 */
@Entity
public class Source {

    /**
     * id
     */
    @Id(autoincrement = true)
    Long id;

    /**
     * report_id 数据采集模板id
     */
    String reportId;

    /**
     * 每份信息都有一个唯一的标识 uuid
     */
    String uuid;

    /**
     * 单个组件对应数据库字段的 key 值
     */
    String key;

    /**
     * 组件用来解析渲染的配置信息
     */
    String config;

    /**
     * 组件的类型
     */
    String type;

    /**
     * 组件是否默认显示
     */
    int isShow;

    /**
     * 组件是否为列表展示字段
     */
    int isList;

    /**
     * 组件是否为过滤字段
     */
    int isFilter;

    /**
     * 组件的值
     */
    String value;
}
