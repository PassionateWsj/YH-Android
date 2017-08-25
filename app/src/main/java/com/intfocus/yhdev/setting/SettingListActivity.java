package com.intfocus.yhdev.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseActivity;
import com.intfocus.yhdev.subject.SimpleListAdapter;
import com.intfocus.yhdev.util.K;
import com.intfocus.yhdev.util.ToastColor;
import com.umeng.message.PushAgent;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by liuruilin on 2017/3/29.
 */
public class SettingListActivity extends BaseActivity {
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleListAdapter mSimpleAdapter;
    private String[] mItemNameList;
    private String[] mItemContentList;
    private TextView mBannerTitle;
    private String mListType;
    private long mLastExitTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        mBannerTitle = (TextView)findViewById(R.id.bannerTitle);
        mListType = getIntent().getStringExtra("type");
        mBannerTitle.setText(mListType);
        initListInfo(mListType);
    }

    private void initListInfo(String type) {
        switch (type) {
            case "应用信息" :
                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String appName = getString(getApplicationInfo().labelRes);
                String deviceInfo = String.format("%s(Android %s)", TextUtils.split(android.os.Build.MODEL, " - ")[0], Build.VERSION.RELEASE);
                String apiDomain = K.kBaseUrl.replace("http://", "").replace("https://", "");
                String versionInfo = String.format("%s(%d)", packageInfo.versionName, packageInfo.versionCode);
                String appPackageInfo = packageInfo.packageName;
                mItemNameList = new String[]{"应用名称", "检测更新", "设备型号", "数据接口", "应用标识"};
                mItemContentList = new String[]{appName, "当前版本: " + versionInfo, deviceInfo, apiDomain, appPackageInfo};
                break;

            case "消息推送" :
                PushAgent mPushAgent = PushAgent.getInstance(mAppContext);
                String isPushOpened = isPushOpened(mPushAgent);
                mItemNameList = new String[]{"消息推送", "关联的设备列表", "推送的消息列表"};
                mItemContentList = new String[]{isPushOpened, "arrow", "arrow"};
                break;

            default:
                mItemNameList = new String[]{};
                mItemContentList = new String[]{};
                break;
        }
        initListView(mItemNameList, mItemContentList);
    }

    private String isPushOpened(PushAgent mPushAgent) {
        try {
            String deviceToken  = mPushAgent.getRegistrationId();
            if (deviceToken.length() == 44) {
                return "开启";
            }
        }catch (NullPointerException e){
            return "关闭";
        }
        return "关闭";
    }

    /*
     * ListView 内容填充
     */
    private void initListView(String[] mItemNameList, String[] mItemContentList) {
        listItem = new ArrayList<>();
        for (int i = 0; i < mItemNameList.length; i++) {
            HashMap<String, Object> map = new HashMap<>();
            map.put("ItemName", mItemNameList[i]);
            map.put("ItemContent", mItemContentList[i]);
            listItem.add(map);
        }

        mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});

        ListView listView = (ListView) findViewById(R.id.list_listSetting);
        listView.setAdapter(mSimpleAdapter);
        listView.setOnItemClickListener(mListItemListener);
    }

    /*
     * 个人信息菜单项点击事件
     */
    private ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView mItemText = (TextView) arg1.findViewById(R.id.item_setting_key);
            switch (mItemText.getText().toString()) {
                case "应用标识" :
                    if (System.currentTimeMillis() - mLastExitTime < 2000) {
                        Intent developerActivityIntent = new Intent(SettingListActivity.this, DeveloperActivity.class);
                        developerActivityIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(developerActivityIntent);
                        break;
                    } else {
                        mLastExitTime = System.currentTimeMillis();
                        toast("再点击一下将打开 开发者选项", ToastColor.SUCCESS);
                    }
                    break;

                case "检测更新" :
                    checkPgyerVersionUpgrade(SettingListActivity.this, true);
                    break;

                case "关联的设备列表":
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
//                                String deviceTokenUrl = String.format(K.kDeviceTokenAPIPath, K.kBaseUrl, mUserSP.getString("user_num", "0"));
//                                final Map<String, String> response = HttpUtil.httpGet(deviceTokenUrl, new HashMap<String, String>());
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (response.containsKey("code") && response.get("code").equals("200")){
//                                            Intent intent = new Intent(SettingListActivity.this, ShowListMsgActivity.class);
//                                            try {
//                                                JSONObject jsonObject = new JSONObject(response.get("body"));
//                                                intent.putExtra("response", jsonObject.getString("devices"));
//                                                intent.putExtra("title", "关联的设备列表");
//                                                startActivity(intent);
//                                            } catch (JSONException e) {
//                                                e.printStackTrace();
//                                            }
//                                        }else {
//                                            toast("获取关联的设备列表失败");
//                                        }
//                                    }
//                                });
//                        }
//                    }).start();
                    break;

                case "推送的消息列表":
                    try {
                        SharedPreferences sp = getSharedPreferences("allPushMessage", MODE_PRIVATE);
                        String allMessage = sp.getString("message","false");
                        if (allMessage.equals("false")){
                            toast("从未接收到推送消息");
                        }else {
                            Intent intent = new Intent(SettingListActivity.this, ShowListMsgActivity.class);
                            intent.putExtra("type", "pushMessage");
                            intent.putExtra("title", "推送的消息列表");
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };
}