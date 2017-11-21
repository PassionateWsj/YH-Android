package com.intfocus.syp_template.general.data;

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

    private String mObjectList;

    public static boolean hasData() {
        return INSTANCE.mObjectList != null;
    }

    public static void setData(String objectList) {
        INSTANCE.mObjectList = objectList;
    }

    public static String getData() {
        String retList = INSTANCE.mObjectList;
        INSTANCE.mObjectList = null;
        return retList;
//        return INSTANCE.mObjectList;
    }
}
