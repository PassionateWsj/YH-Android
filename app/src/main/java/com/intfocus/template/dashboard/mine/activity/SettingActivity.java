package com.intfocus.template.dashboard.mine.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.intfocus.template.R;
import com.intfocus.template.ui.BaseActivity;

import org.xutils.x;

import java.util.ArrayList;

/**
 * @author liuruilin
 * @date 2017/3/28
 */

public class SettingActivity extends BaseActivity {
    ListView mListItem;

    private ArrayAdapter<String> mListAdapter;
    private Context mContext;
    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        x.view().inject(this);
        mContext = this;
        mSharedPreferences = getSharedPreferences("SettingPreference", MODE_PRIVATE);
        initSettingListItem();
    }

    /**
     * 个人信息页菜单项初始化
     */
    private void initSettingListItem() {
        ArrayList<String> listItem = new ArrayList<>();
        String[] itemName = {"基本信息", "选项配置", "消息推送", "更新日志"};

        for (int i = 0; i < itemName.length; i++) {
            listItem.add(itemName[i]);
        }

        mListAdapter = new ArrayAdapter(this, R.layout.list_item_setting, R.id.item_setting, listItem);

        mListItem = (ListView) findViewById(R.id.list_setting);
        mListItem.setAdapter(mListAdapter);
        mListItem.setOnItemClickListener(mListItemListener);
    }

    /**
     * 个人信息菜单项点击事件
     */
    private ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
            TextView mItemText = (TextView) arg1.findViewById(R.id.item_setting);
            switch (mItemText.getText().toString()) {
                case "基本信息":
                    Intent appInfoIntent = new Intent(mContext, SettingListActivity.class);
                    appInfoIntent.putExtra("type", "基本信息");
                    startActivity(appInfoIntent);
                    break;

                case "消息推送":
                    Intent pushIntent = new Intent(mContext, SettingListActivity.class);
                    pushIntent.putExtra("type", "消息推送");
                    startActivity(pushIntent);
                    break;

                case "更新日志":
                    // 更新日志页面待实现
                    break;

                case "选项配置":
                    Intent settingPreferenceIntent = new Intent(mContext, SettingPreferenceActivity.class);
                    startActivity(settingPreferenceIntent);
                    break;

                default:
                    break;
            }
        }
    };

}
