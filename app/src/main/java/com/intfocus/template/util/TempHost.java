package com.intfocus.template.util;

/**
 * ****************************************************
 * author jameswong
 * created on: 18/01/23 上午09:39
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public enum TempHost {
    INSTANCE;

    private String host;

    public static boolean hasHost() {
        return INSTANCE.host != null && !"".equals(INSTANCE.host);
    }

    public static void setHost(String host) {
        INSTANCE.host = host;
    }

    public static String getHost() {
        return INSTANCE.host;
    }
}
