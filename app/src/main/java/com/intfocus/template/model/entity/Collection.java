package com.intfocus.template.model.entity;

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

    /**
     * 创建时间
     */
    Long created_at;
    /**
     * 更新时间
     */
    Long updated_at;

    String h1;

    String h2;

    String h3;

    String h4;

    String h5;

    @Generated(hash = 1149123052)
    public Collection() {
    }

    @Generated(hash = 1239032216)
    public Collection(Long id, String reportId, String uuid, String dJson, int status,
            int imageStatus, Long created_at, Long updated_at, String h1, String h2,
            String h3, String h4, String h5) {
        this.id = id;
        this.reportId = reportId;
        this.uuid = uuid;
        this.dJson = dJson;
        this.status = status;
        this.imageStatus = imageStatus;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.h1 = h1;
        this.h2 = h2;
        this.h3 = h3;
        this.h4 = h4;
        this.h5 = h5;
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

    public String getH1() {
        return this.h1;
    }

    public void setH1(String h1) {
        this.h1 = h1;
    }

    public String getH2() {
        return this.h2;
    }

    public void setH2(String h2) {
        this.h2 = h2;
    }

    public String getH3() {
        return this.h3;
    }

    public void setH3(String h3) {
        this.h3 = h3;
    }

    public String getH4() {
        return this.h4;
    }

    public void setH4(String h4) {
        this.h4 = h4;
    }

    public String getH5() {
        return this.h5;
    }

    public void setH5(String h5) {
        this.h5 = h5;
    }

    public Long getCreated_at() {
        return this.created_at;
    }

    public void setCreated_at(Long created_at) {
        this.created_at = created_at;
    }

    public Long getUpdated_at() {
        return this.updated_at;
    }

    public void setUpdated_at(Long updated_at) {
        this.updated_at = updated_at;
    }
}
