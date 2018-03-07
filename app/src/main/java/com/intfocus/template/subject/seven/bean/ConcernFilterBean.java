package com.intfocus.template.subject.seven.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/02/27 上午10:23
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class ConcernFilterBean {
    @Id
    Long _id;
    int id;
    String obj_name;
    int pid;
    String obj_num;
    String uuid;
    @Generated(hash = 304045720)
    public ConcernFilterBean(Long _id, int id, String obj_name, int pid,
            String obj_num, String uuid) {
        this._id = _id;
        this.id = id;
        this.obj_name = obj_name;
        this.pid = pid;
        this.obj_num = obj_num;
        this.uuid = uuid;
    }
    @Generated(hash = 713176553)
    public ConcernFilterBean() {
    }
    public Long get_id() {
        return this._id;
    }
    public void set_id(Long _id) {
        this._id = _id;
    }
    public int getId() {
        return this.id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getObj_name() {
        return this.obj_name;
    }
    public void setObj_name(String obj_name) {
        this.obj_name = obj_name;
    }
    public int getPid() {
        return this.pid;
    }
    public void setPid(int pid) {
        this.pid = pid;
    }
    public String getObj_num() {
        return this.obj_num;
    }
    public void setObj_num(String obj_num) {
        this.obj_num = obj_num;
    }
    public String getUuid() {
        return this.uuid;
    }
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
