package com.intfocus.syp_template.general.data;

import com.intfocus.syp_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity;

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

    private Map<String, ModularTwo_UnitTableEntity> mObjectList;

    public static boolean hasData(int index) {
        return INSTANCE.mObjectList.get(""+index) != null;
    }

    public static void setData(int index, ModularTwo_UnitTableEntity objectList) {
        if (INSTANCE.mObjectList == null) {
            INSTANCE.mObjectList = new HashMap<>(16);
        }
        INSTANCE.mObjectList.put(""+index, objectList);
    }

    public static ModularTwo_UnitTableEntity getData(int index) {
        ModularTwo_UnitTableEntity retList = INSTANCE.mObjectList.get(""+index);
        INSTANCE.mObjectList.remove(""+index);
        return retList;
    }
}
