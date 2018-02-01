package com.intfocus.template.dashboard.mine.activity;

import android.content.Context;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.intfocus.template.ConfigConstants;
import com.intfocus.template.R;
import com.intfocus.template.dashboard.mine.adapter.SimpleListAdapter;
import com.intfocus.template.ui.BaseActivity;
import com.intfocus.template.util.TempHost;
import com.intfocus.template.util.ToastUtils;
import com.intfocus.template.util.VersionUtil;
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
    private RelativeLayout mActionBar;
    private TextView mBannerTitle;
    private String mListType;
    private long mLastExitTime;
    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_list);
        mBannerTitle = (TextView) findViewById(R.id.bannerTitle);
        mActionBar = (RelativeLayout) findViewById(R.id.rl_action_bar);
        mListType = getIntent().getStringExtra("type");
        mBannerTitle.setText(mListType);
        initShow();
        initListInfo(mListType);
    }

    private void initShow() {
        if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
            mActionBar.post(new Runnable() {
                @Override
                public void run() {
                    BarUtils.addMarginTopEqualStatusBarHeight(mActionBar);
                }
            });
        }
    }

    private void initListInfo(String type) {
        switch (type) {
            case "基本信息":
                PackageInfo packageInfo = null;
                try {
                    packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                String appName = getString(getApplicationInfo().labelRes);
                String deviceInfo = String.format("%s(Android %s)", TextUtils.split(android.os.Build.MODEL, " - ")[0], Build.VERSION.RELEASE);
                String apiDomain = TempHost.getHost().replace("http://", "").replace("https://", "");
                String versionInfo = String.format("%s(%d)", packageInfo.versionName, packageInfo.versionCode);
                String appPackageInfo = packageInfo.packageName;
                mItemNameList = new String[]{"应用名称", "检测更新", "设备型号", "数据接口", "应用标识"};
                mItemContentList = new String[]{appName, "当前版本: " + versionInfo, deviceInfo, apiDomain, appPackageInfo};
                break;

            case "消息推送":
                PushAgent mPushAgent = PushAgent.getInstance(mContext);
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
            String deviceToken = mPushAgent.getRegistrationId();
            if (deviceToken.length() == 44) {
                return "开启";
            }
        } catch (NullPointerException e) {
            return "关闭";
        }
        return "关闭";
    }

    /**
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

    /**
     * 个人信息菜单项点击事件
     */
    private ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView mItemText = (TextView) arg1.findViewById(R.id.item_setting_key);
            switch (mItemText.getText().toString()) {
                case "应用标识":
                    break;

                case "检测更新":
                    VersionUtil.INSTANCE.checkPgyerVersionUpgrade(SettingListActivity.this, true);
                    break;

                case "关联的设备列表":
                    break;

                case "推送的消息列表":
                    try {
                        SharedPreferences sp = getSharedPreferences("allPushMessage", MODE_PRIVATE);
                        String allMessage = sp.getString("message", "false");
                        if ("false".equals(allMessage)) {
                            ToastUtils.INSTANCE.show(mContext, "从未接收到推送消息");
                        } else {
                            Intent intent = new Intent(SettingListActivity.this, ShowListMsgActivity.class);
                            intent.putExtra("type", "pushMessage");
                            intent.putExtra("title", "推送的消息列表");
                            startActivity(intent);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;

                default:
                    break;
            }
        }
    };
}
