package com.intfocus.syp_template.general.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * @author liuruilin
 * @data 2017/11/7
 * @describe
 */
@Entity
public class Collection {
    /**
     * id
     */
    @Id(autoincrement = true)
    Long id;

    /**
     * report_id 数据采集模板 id
     */
    String reportId;

    /**
     * uuid
     */
    String uuid;

    /**
     * D-JSON 数据采集完成后拼装的JSON
     */
    String dJson;

    /**
     * status 采集信息上传状态
     */
    int status;

    /**
     * imageStatus 图片上传状态
     */
    int imageStatus;
}
