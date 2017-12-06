package com.intfocus.template.model.entity;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/01 上午11:46
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class PushMsgBean {

    @Id
    Long id;

    /**
     * AmIpKqkMdFACG4PW7M128GZxjznNYM2I1B292EokC0Vg
     * {
     * "msg_id":"ul42751150157385157100",
     * "display_type":"notification",
     * "random_min":0,
     * "body":{
     * "ticker":"消息中心(7851)",
     * "body_title":"消息中心(7851)",
     * "text":"野竹分青霭，飞泉挂碧峰",
     * "sound":"default",
     * "play_sound":"true",
     * "play_lights":"true",
     * "play_vibrate":"true",
     * "after_open":"go_custom",
     * "custom":{
     * "type":"report",
     * "body_title":"第二集群销售额",
     * "url":"/mobile/v2/group/%@/template/4/report/8",
     * "obj_id":8,
     * "obj_type":1,
     * "debug_timestamp":"2017-08-01 15:50:51 +0800"
     * }
     * }
     * }
     */

    /*
    {
        "type":"report",
        "url":"/mobile/v2/group/%@/template/1/report/7",
        "title": "第二集群生鲜销售概况",
        "obj_id": "7",
        "template_id": "1",
        "obj_type": "3",
        "params_mapping": {
          "user_num": "user_num"
        },
        "debug_timestamp":"2017-08-01 15:50:51 +0800"
    }
    */

    String ticker;
    String body_title;
    String text;

    String type;
    String url;
    String title;
    String obj_id;
    String template_id;
    String obj_type;
    String params_mapping;
    String debug_timestamp;

    boolean new_msg;

    @Generated(hash = 2009287025)
    public PushMsgBean(Long id, String ticker, String body_title, String text,
            String type, String url, String title, String obj_id,
            String template_id, String obj_type, String params_mapping,
            String debug_timestamp, boolean new_msg) {
        this.id = id;
        this.ticker = ticker;
        this.body_title = body_title;
        this.text = text;
        this.type = type;
        this.url = url;
        this.title = title;
        this.obj_id = obj_id;
        this.template_id = template_id;
        this.obj_type = obj_type;
        this.params_mapping = params_mapping;
        this.debug_timestamp = debug_timestamp;
        this.new_msg = new_msg;
    }

    @Generated(hash = 738489565)
    public PushMsgBean() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return this.ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getBody_title() {
        return this.body_title;
    }

    public void setBody_title(String body_title) {
        this.body_title = body_title;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getObj_id() {
        return this.obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getTemplate_id() {
        return this.template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getObj_type() {
        return this.obj_type;
    }

    public void setObj_type(String obj_type) {
        this.obj_type = obj_type;
    }

    public String getParams_mapping() {
        return this.params_mapping;
    }

    public void setParams_mapping(String params_mapping) {
        this.params_mapping = params_mapping;
    }

    public String getDebug_timestamp() {
        return this.debug_timestamp;
    }

    public void setDebug_timestamp(String debug_timestamp) {
        this.debug_timestamp = debug_timestamp;
    }

    public boolean getNew_msg() {
        return this.new_msg;
    }

    public void setNew_msg(boolean new_msg) {
        this.new_msg = new_msg;
    }

}
