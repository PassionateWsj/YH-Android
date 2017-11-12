package com.intfocus.yhdev.general.bean;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * @author liuruilin
 * @data 2017/10/30
 * @describe
 */

public class TextBean {
    /**
     * title : 问题修改及改进意见
     * hint : 请描述您遇到的问题
     * value :
     */
    private String title;
    private String hint;
    private String value;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("title", title);
            jsonObject.put("hint", hint);
            jsonObject.put("value", value);

            return jsonObject.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }
}
