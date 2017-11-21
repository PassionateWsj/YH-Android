package com.intfocus.syp_template.general.data;

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

    private Map<String, String> mObjectList;

    public static boolean hasData(int index) {
        return INSTANCE.mObjectList.get(""+index) != null;
    }

    public static void setData(int index, String objectList) {
        if (INSTANCE.mObjectList == null) {
            INSTANCE.mObjectList = new HashMap<>(16);
        }
        INSTANCE.mObjectList.put(""+index, objectList);
    }

    public static String getData(int index) {
        String retList = INSTANCE.mObjectList.get(""+index);
        INSTANCE.mObjectList.remove(""+index);
        return retList;
//        return INSTANCE.mObjectList;
    }
}
