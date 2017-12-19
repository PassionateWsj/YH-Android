package com.intfocus.template.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.intfocus.template.ConfigConstants;
import com.intfocus.template.constant.Params;

import static com.intfocus.template.constant.Params.USER_BEAN;

/**
 * ****************************************************
 *
 * @author jameswong
 *         created on: 17/10/19 下午5:43
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */

public class MapUtil {
    private static MapUtil mInstance = null;
    private final AMapLocationClient locationClient;
    private Context mCtx;

    /**
     * 双重校验锁单例模式
     */
    public static synchronized MapUtil getInstance(Context ctx) {
        if (mInstance == null) {
            synchronized (MapUtil.class) {
                if (mInstance == null) {
                    mInstance = new MapUtil(ctx);
                }
            }
        }
        return mInstance;
    }

    private MapUtil(Context ctx) {
        mCtx = ctx;
        //初始化client
        locationClient = new AMapLocationClient(mCtx);
        AMapLocationClient.setApiKey(ConfigConstants.GAODE_MAP_APP_KEY);
        AMapLocationClientOption locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
    }

    public void getAMapLocation(AMapLocationListener mAMapLocationListener) {
        locationClient.setLocationListener(mAMapLocationListener);
        locationClient.startLocation();
    }

    /**
     * 默认的定位参数
     *
     * @author hongming.wang
     * @since 2.8.0
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
//        mOption.setInterval(60000 * 10);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(true);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        return mOption;
    }

    /**
     * 设置定位回调监听
     */
    public  void updateSPLocation() {
        mInstance.getAMapLocation(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (null != location) {
                    StringBuffer sb =new  StringBuffer();
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if (location.getErrorCode() == 0) {
                        SharedPreferences mUserSP = mCtx.getSharedPreferences(USER_BEAN, Context.MODE_PRIVATE);
                        mUserSP.edit().putString(Params.USER_LOCATION,
                                String.format("%.6f", location.getLongitude()) + ","
                                        + String.format("%.6f", location.getLatitude())).apply();

                        sb.append("经    度    : " + location.getLongitude() + "\n");
                        sb.append("纬    度    : " + location.getLatitude() + "\n");
                    } else {
                        //定位失败;
                        sb.append("错误码:" + location.getErrorCode() + "\n");
                        sb.append("错误信息:" + location.getErrorInfo() + "\n");
                        sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    }

                    //解析定位结果
                    String result = sb.toString();
                    LogUtil.d("testlog", result);
                } else {
                    LogUtil.d("testlog", "定位失败，loc is null");
                }
            }
        });
    }
}
