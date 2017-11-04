package com.intfocus.spy_template.business.dashboard.mine.activity;import android.content.Intent;import android.content.SharedPreferences;import android.os.Bundle;import android.view.View;import android.widget.AdapterView;import android.widget.ArrayAdapter;import android.widget.ListView;import android.widget.TextView;import com.intfocus.spy_template.R;import com.intfocus.spy_template.general.base.BaseActivity;import com.intfocus.spy_template.general.util.FileUtil;import com.intfocus.spy_template.general.util.K;import com.intfocus.spy_template.general.util.ToastUtils;import java.io.File;import java.util.ArrayList;public class DeveloperActivity extends BaseActivity {    private ArrayAdapter<String> mListAdapter;    @Override    protected void onCreate(Bundle savedInstanceState) {        super.onCreate(savedInstanceState);        setContentView(R.layout.activity_developer);        initSettingListItem();    }    /**     * 个人信息页菜单项初始化     */    private void initSettingListItem() {        ArrayList<String> listItem = new ArrayList<>();        String[] itemName = {"报表缓存数据列表", "请求头缓存列表", "配置文件列表"};        for (int i = 0; i < itemName.length; i++) {            listItem.add(itemName[i]);        }        mListAdapter = new ArrayAdapter(this, R.layout.list_item_setting, R.id.item_setting, listItem);        ListView listView = (ListView) findViewById(R.id.list_setting);        listView.setAdapter(mListAdapter);        listView.setOnItemClickListener(mListItemListener);    }    /**     * 个人信息菜单项点击事件     */    private ListView.OnItemClickListener mListItemListener = new ListView.OnItemClickListener() {        @Override        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {            TextView mItemText = (TextView) arg1.findViewById(R.id.item_setting);            switch (mItemText.getText().toString()) {                case "报表缓存数据列表":                    SharedPreferences sp = getSharedPreferences("subjectCache", MODE_PRIVATE);                    String cache = sp.getString("cache", "");                    if ("".equals(cache)) {                        ToastUtils.INSTANCE.show(mAppContext, "无缓存报表");                    } else {                        Intent cacheIntent = new Intent(DeveloperActivity.this, ShowListMsgActivity.class);                        cacheIntent.putExtra("type", "subjectCache");                        cacheIntent.putExtra("title", "报表缓存数据列表");                        cacheIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);                        startActivity(cacheIntent);                    }                    break;                case "请求头缓存列表":                    String cacheHeaderPath = FileUtil.dirPath(mAppContext, "HTML", K.kCachedHeaderConfigFileName);                    if (new File(cacheHeaderPath).exists()) {                        Intent intent = new Intent(DeveloperActivity.this, ShowListMsgActivity.class);                        intent.putExtra("type", "cacheHeaders");                        intent.putExtra("title", "配置文件列表");                        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);                        startActivity(intent);                    } else {                        ToastUtils.INSTANCE.show(mAppContext, "请求头缓存文件不存在");                    }                    break;                case "配置文件列表":                    Intent configIntent = new Intent(DeveloperActivity.this, ShowListMsgActivity.class);                    configIntent.putExtra("type", "config");                    configIntent.putExtra("title", "配置文件列表");                    configIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);                    startActivity(configIntent);                    break;                default:                    break;            }        }    };    @Override    public void dismissActivity(View v) {        DeveloperActivity.this.onBackPressed();    }}