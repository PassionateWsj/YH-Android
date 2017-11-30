package com.intfocus.template.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

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

    @Generated(hash = 1640627332)
    public Collection(Long id, String reportId, String uuid, String dJson,
                      int status, int imageStatus) {
        this.id = id;
        this.reportId = reportId;
        this.uuid = uuid;
        this.dJson = dJson;
        this.status = status;
        this.imageStatus = imageStatus;
    }

    @Generated(hash = 1149123052)
    public Collection() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getReportId() {
        return this.reportId;
    }

    public void setReportId(String reportId) {
        this.reportId = reportId;
    }

    public String getUuid() {
        return this.uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getDJson() {
        return this.dJson;
    }

    public void setDJson(String dJson) {
        this.dJson = dJson;
    }

    public int getStatus() {
        return this.status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getImageStatus() {
        return this.imageStatus;
    }

    public void setImageStatus(int imageStatus) {
        this.imageStatus = imageStatus;
    }
}
