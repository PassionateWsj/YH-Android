package com.intfocus.yhdev.scanner;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseActivity;
import com.intfocus.yhdev.data.response.scanner.StoreItem;
import com.intfocus.yhdev.data.response.scanner.StoreListResult;
import com.intfocus.yhdev.net.ApiException;
import com.intfocus.yhdev.net.CodeHandledSubscriber;
import com.intfocus.yhdev.net.RetrofitUtil;
import com.intfocus.yhdev.util.FileUtil;
import com.intfocus.yhdev.util.K;
import com.intfocus.yhdev.util.URLs;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lijunjie on 16/8/15.
 */
public class StoreSelectorActivity extends BaseActivity {
    private ListView mListView;
    private String cachedPath;
    private ArrayList<StoreItem> dataList = new ArrayList<>();
    private ArrayList<String> storeNameList = new ArrayList<>();
    private JSONObject cachedJSON;

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_selector);

        final LinearLayout mListHead = (LinearLayout) findViewById(R.id.store_list_head);
        TextView mSelectedItem = (TextView) findViewById(R.id.store_item_select);

        try {
            cachedPath = FileUtil.dirPath(mAppContext, K.kCachedDirName, K.kBarCodeResultFileName);
            cachedJSON = FileUtil.readConfigFile(cachedPath);
            JSONObject currentStore = cachedJSON.getJSONObject(URLs.kStore);

            mSelectedItem.setText(currentStore.getString("name")); // 已选项显示当前门店

            RetrofitUtil.getHttpService(getApplicationContext()).getStoreList(mUserSP.getString("user_num", "0"))
                    .compose(new RetrofitUtil.CommonOptions<StoreListResult>())
                    .subscribe(new CodeHandledSubscriber<StoreListResult>() {
                        @Override
                        public void onError(ApiException apiException) {

                        }

                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onBusinessNext(StoreListResult data) {
                            if (data.getData() != null) {
                                for (int i = 0, len = data.getData().size(); i < len; i++) {
                                    dataList.add(data.getData().get(i));
                                    storeNameList.add(data.getData().get(i).getName());
                                }
                            }

                            /*
                             * 搜索框初始化
                             */
                            SearchView mSearchView = (SearchView) findViewById(R.id.storeSearchView);
                            int searchEditId = mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text", null, null);
                            TextView mSearchEdit = (TextView) findViewById(searchEditId);
                            mSearchEdit.setTextSize(14);
                            mSearchEdit.setPadding(0, 30, 0, 0);

                            /*
                             * ListView 初始化
                             */
                            mListView = (ListView) findViewById(R.id.listStores);
                            ListArrayAdapter mArrayAdapter = new ListArrayAdapter(mAppContext, R.layout.list_item_report_selector, storeNameList);
                            mListView.setAdapter(mArrayAdapter);
                            mListView.setTextFilterEnabled(true);

                            /*
                             * 搜索框事件监听
                             */
                            mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                                // 当点击搜索按钮时触发该方法
                                @Override
                                public boolean onQueryTextSubmit(String query) {
                                    return false;
                                }

                                // 当搜索内容改变时触发该方法
                                @Override
                                public boolean onQueryTextChange(String newText) {
                                    if (!TextUtils.isEmpty(newText)) {
                                        mListHead.setVisibility(View.GONE);
                                        mListView.setFilterText(newText);
                                    } else {
                                        mListHead.setVisibility(View.VISIBLE);
                                        mListView.clearTextFilter();
                                    }
                                    return true;
                                }
                            });

                            /**
                             *  用户点击项写入本地缓存文件
                             */
                            mListView.setOnItemClickListener(mItemClickListener);
                        }
                    });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /*
     * listview 点击事件
     */
    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            try {
                TextView mSelector = (TextView) view.findViewById(R.id.reportSelectorItem);
                String selectedItem = mSelector.getText().toString();
                for (int i = 0; i < dataList.size(); i++) {
                    if (dataList.get(i).getName().equals(selectedItem)) {
                        JSONObject storeJson = new JSONObject();
                        storeJson.put("id", dataList.get(i).getId());
                        storeJson.put("name", dataList.get(i).getName());
                        cachedJSON.put(URLs.kStore, storeJson);
                        FileUtil.writeFile(cachedPath, cachedJSON.toString());
                    }
                }

                dismissActivity(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dismissActivity(null);
        }
    };

    protected void onResume() {
        mMyApp.setCurrentActivity(this);
        super.onResume();
    }

    protected void onDestroy() {
        mWebView = null;
        super.onDestroy();
    }

    public class ListArrayAdapter extends ArrayAdapter<String> {
        private int resourceId;
        private List<String> items;

        public ListArrayAdapter(Context context, int textViewResourceId, List<String> items) {
            super(context, textViewResourceId, items);
            this.resourceId = textViewResourceId;
            this.items = items;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            String item = items.get(position).trim();
            LinearLayout listItem = new LinearLayout(getContext());
            String inflater = Context.LAYOUT_INFLATER_SERVICE;
            LayoutInflater vi = (LayoutInflater) getContext().getSystemService(inflater);
            vi.inflate(resourceId, listItem, true);
            TextView viewItem = (TextView) listItem.findViewById(R.id.reportSelectorItem);
            viewItem.setText(item);
            viewItem.setTextColor(getResources().getColor(R.color.black));
            viewItem.setBackgroundColor(Color.WHITE);

            return listItem;
        }
    }

    /*
     * 返回
     */
    public void dismissActivity(View v) {
        StoreSelectorActivity.this.onBackPressed();
        finish();
    }
}
