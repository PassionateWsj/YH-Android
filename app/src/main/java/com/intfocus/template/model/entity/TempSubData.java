package com.intfocus.template.model.entity;

import com.intfocus.template.subject.one.entity.Tables;

import java.util.HashMap;
import java.util.Map;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/11/16 下午4:46
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public enum TempSubData {
    INSTANCE;

    private Map<String, Tables> mObjectList;

    public static boolean hasData(int index) {
        return INSTANCE.mObjectList.get(""+index) != null;
    }

    public static void setData(int index, Tables objectList) {
        if (INSTANCE.mObjectList == null) {
            INSTANCE.mObjectList = new HashMap<>(16);
        }
        INSTANCE.mObjectList.put(""+index, objectList);
    }

    public static Tables getData(int index) {
        Tables retList = INSTANCE.mObjectList.get(""+index);
        INSTANCE.mObjectList.remove(""+index);
        return retList;
    }
}
