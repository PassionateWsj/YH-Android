package com.intfocus.template.dashboard.mine.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.blankj.utilcode.util.BarUtils;
import com.intfocus.template.ConfigConstants;
import com.intfocus.template.R;
import com.intfocus.template.dashboard.mine.adapter.SimpleListAdapter;
import com.intfocus.template.ui.BaseActivity;
import com.intfocus.template.util.FileUtil;
import com.intfocus.template.util.K;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ShowListMsgActivity extends BaseActivity {
    private ListView pushListView;
    private TextView bannerTitle;
    private ArrayList<HashMap<String, Object>> listItem;
    private SimpleListAdapter mSimpleAdapter;
    private String response;
    private String type;
    private RelativeLayout mActionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_list_msg);

        pushListView = (ListView) findViewById(R.id.pushListView);
        bannerTitle = (TextView) findViewById(R.id.bannerTitle);
        bannerTitle = (TextView) findViewById(R.id.bannerTitle);
        mActionBar = (RelativeLayout) findViewById(R.id.rl_action_bar);

        if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
            mActionBar.post(new Runnable() {
                @Override
                public void run() {
                    BarUtils.addMarginTopEqualStatusBarHeight(mActionBar);
                }
            });
        }

        listItem = new ArrayList<>();
        Intent intent = getIntent();
        bannerTitle.setText(intent.getStringExtra("title"));
        if (intent.hasExtra("type")) {
            type = intent.getStringExtra("type");
            initListInfo(type);
        }

        if (intent.hasExtra("response")) {
            response = intent.getStringExtra("response");
            try {
                JSONArray array = new JSONArray(response);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject json = array.getJSONObject(i);
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("ItemName", json.getString("name"));
                    if (json.getString("os").startsWith("iPhone")) {
                        map.put("ItemContent", "iPhone" + "(" + json.getString("os_version") + ")");
                    } else {
                        map.put("ItemContent", "Android" + "(" + json.getString("os_version") + ")");
                    }
                    listItem.add(map);
                }
                mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                pushListView.setAdapter(mSimpleAdapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void initListInfo(String type) {
        switch (type) {
            case "subjectCache":
                try {
                    SharedPreferences sp = getSharedPreferences("subjectCache", MODE_PRIVATE);
                    String cache = sp.getString("cache", "");
                    JSONObject json = new JSONObject(cache);
                    Iterator<String> it = json.keys();
                    while (it.hasNext()) {
                        HashMap<String, Object> map = new HashMap<>();
                        String key = it.next();
                        map.put("ItemName", json.getString(key));
                        map.put("ItemContent", "");
                        listItem.add(map);
                    }
                    mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                    pushListView.setAdapter(mSimpleAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "cacheHeaders":
                try {
                    String cacheHeaderPath = FileUtil.dirPath(getMAppContext(), "HTML", K.K_CACHED_HEADER_CONFIG_FILE_NAME);
                    if (new File(cacheHeaderPath).exists()) {
                        JSONObject json = FileUtil.readConfigFile(cacheHeaderPath);
                        Iterator<String> it = json.keys();
                        while (it.hasNext()) {
                            HashMap<String, Object> map = new HashMap<>();
                            String key = it.next();
                            map.put("ItemName", key + " :");
                            map.put("ItemContent", "");
                            listItem.add(map);
                            map = new HashMap<>();
                            map.put("ItemName", "");
                            map.put("ItemContent", ((JSONObject) json.get(key)).toString());
                            listItem.add(map);
                        }
                        mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                        pushListView.setAdapter(mSimpleAdapter);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case "config":
                String userConfigPath = String.format("%s/%s", FileUtil.basePath(this), K.K_USER_CONFIG_FILE_NAME);
                JSONObject user = FileUtil.readConfigFile(userConfigPath);
                Iterator<String> it = user.keys();
                while (it.hasNext()) {
                    try {
                        HashMap<String, Object> map = new HashMap<>();
                        String key = it.next();
                        if (!"assets".equals(key)) {
                            map.put("ItemName", key + " :");
                            map.put("ItemContent", "");
                            listItem.add(map);
                            map = new HashMap<>();
                            map.put("ItemName", "");
                            map.put("ItemContent", user.getString(key));
                            listItem.add(map);
                        }
                        mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.list_info_setting, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                        pushListView.setAdapter(mSimpleAdapter);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;

            case "pushMessage":
                try {
                    SharedPreferences sp = getSharedPreferences("allPushMessage", MODE_PRIVATE);
                    JSONObject json = new JSONObject(sp.getString("message", "false"));
                    for (int i = 0; i < json.length(); i++) {
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("ItemName", json.getString("" + i));
                        map.put("ItemContent", json.getString("" + i));
                        listItem.add(map);
                    }
                    mSimpleAdapter = new SimpleListAdapter(this, listItem, R.layout.layout_push_list, new String[]{"ItemName", "ItemContent"}, new int[]{R.id.item_setting_key, R.id.item_setting_info});
                    pushListView.setAdapter(mSimpleAdapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }
    }
}
