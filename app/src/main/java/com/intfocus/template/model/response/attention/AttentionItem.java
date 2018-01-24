package com.intfocus.template.model.response.attention;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;

/**
 * ****************************************************
 * author: JamesWong
 * created on: 17/08/22 上午09:48
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
@Entity
public class AttentionItem {
    @Id
    Long _id;
    String attention_item_id;
    String attention_item_name;
    boolean isAttentioned = false;

    @Generated(hash = 1019312121)
    public AttentionItem(Long _id, String attention_item_id,
            String attention_item_name, boolean isAttentioned) {
        this._id = _id;
        this.attention_item_id = attention_item_id;
        this.attention_item_name = attention_item_name;
        this.isAttentioned = isAttentioned;
    }

    @Generated(hash = 1362773183)
    public AttentionItem() {
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getAttention_item_id() {
        return this.attention_item_id;
    }

    public void setAttention_item_id(String attention_item_id) {
        this.attention_item_id = attention_item_id;
    }

    public String getAttention_item_name() {
        return this.attention_item_name;
    }

    public void setAttention_item_name(String attention_item_name) {
        this.attention_item_name = attention_item_name;
    }

    public boolean getIsAttentioned() {
        return this.isAttentioned;
    }

    public void setIsAttentioned(boolean isAttentioned) {
        this.isAttentioned = isAttentioned;
    }

}
