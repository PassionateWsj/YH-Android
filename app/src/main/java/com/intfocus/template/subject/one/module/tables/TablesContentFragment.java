package com.intfocus.template.subject.one.module.tables;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.ArrayMap;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.intfocus.template.R;
import com.intfocus.template.model.entity.TempSubData;
import com.intfocus.template.subject.one.NativeReportActivity;
import com.intfocus.template.subject.one.entity.EventRefreshTableRect;
import com.intfocus.template.subject.one.entity.Tables;
import com.intfocus.template.subject.one.module.tables.adapter.TableNameAdapter;
import com.intfocus.template.ui.view.NotScrollListView;
import com.intfocus.template.ui.view.SortCheckBox;
import com.intfocus.template.ui.view.TableHorizontalScrollView;
import com.intfocus.template.ui.view.TableValueView;
import com.intfocus.template.util.DisplayUtil;
import com.intfocus.template.util.LogUtil;
import com.intfocus.template.util.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * 模板一表格内容页面
 */
public class TablesContentFragment extends Fragment implements SortCheckBox.SortViewSizeListener, AdapterView.OnItemClickListener, TableContentContract.View {
    private static final String ARG_PARAM = "param";
    private static final String SU_ROOT_ID = "suRootID";
    private static final String TABLE_ROOT_INDEX = "tableRootIndex";

    private Context ctx;

    private Tables mParam;
    private Dialog loadingDialog;
    private View rootView;

    private FragmentManager fm;

    @ViewInject(R.id.fl_tableTitle_container)
    private FrameLayout fl_tableTitle_container;

    /**
     * 标题列
     */
    private TextView tv_header;

    /**
     * 水平滑动的表头ScrollView
     */
    private TableHorizontalScrollView thscroll_header;

    /**
     * 水平滑动的数据ScrollView
     */
    @ViewInject(R.id.thscroll_unit_table_data)
    private TableHorizontalScrollView thscroll_data;

    /**
     * 动态加载表头的列表
     */
    private LinearLayout linear_header;
    /**
     * 动态加载数据的列表
     */
    @ViewInject(R.id.fl_tableValue_container)
    private FrameLayout fl_tableValue_container;

    /**
     * 动态加载首列的列表
     */
    @ViewInject(R.id.nslistView_unit_table_LineName)
    private NotScrollListView nslistView_LineName;

    ArrayList<ArrayList<String>> lineData = new ArrayList<>();
    ArrayList<String> headerData = new ArrayList<>();
    ArrayList<Integer> al_HeaderLenght = new ArrayList<>();
    ArrayList<SortCheckBox> al_SortView = new ArrayList<>();

    private Tables dataEntity;
    private int headerSize;
    private TableNameAdapter nameAdapter;
    private TableDataComparator dataComparator;
    private TableValueView tableValue;
    /**
     * 悬浮View
     */
    private View suspensionView;
    private String TAG = TablesContentFragment.class.getSimpleName();

    private int offsetTop;

    /**
     * 最上层跟跟标签ID
     */
    public int suRootID;
    private TableContentContract.Presenter mPresenter;
    /**
     * 表格在当前页面的下标（处理一个页面多表格逻辑）
     */
    private int mTableRootIndex;
    private int mTitleHigh;
    private RelativeLayout mRlActionBar;
    private LinearLayout mLlFilter;
    private int mNativeReportActionBarHight;
    private int mNativeReportLlFilterHight;

    @Override
    public TableContentContract.Presenter getPresenter() {
        return mPresenter;
    }

    @Override
    public void setPresenter(TableContentContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static TablesContentFragment newInstance(int suRootID, int tableRootIndex) {
        TablesContentFragment fragment = new TablesContentFragment();
        Bundle args = new Bundle();
        args.putInt(SU_ROOT_ID, suRootID);
        args.putInt(TABLE_ROOT_INDEX, tableRootIndex);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this.getContext();
        EventBus.getDefault().register(this);
        if (getArguments() != null) {
            suRootID = getArguments().getInt(SU_ROOT_ID);
            mTableRootIndex = getArguments().getInt(TABLE_ROOT_INDEX);
            if (TempSubData.hasData(mTableRootIndex)) {
                LogUtil.e(TAG, "表格有数据");
                mParam = TempSubData.getData(mTableRootIndex);
            } else {
                LogUtil.e(TAG, "表格无数据");
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        TableImpl.destroyInstance();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 模板一 最外层 ScrollView 滚动监听回调 -- 显示/隐藏 悬浮标题栏
     *
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void measureLocation(EventRefreshTableRect event) {
        final int surootID = suRootID;
        if (event.eventTag == surootID && !isHidden()) {
            Rect rect = new Rect();
            Point globalOffset = new Point();
            rootView.getGlobalVisibleRect(rect, globalOffset);
            synchronized (this) {
                if (fl_tableTitle_container.getChildCount() != 0) {
                    if (getActivity() instanceof NativeReportActivity) {
                        boolean showSuspendTableTitle = globalOffset.y <= offsetTop && rect.bottom - DisplayUtil.dip2px(getActivity(), 46) > offsetTop;
                        if (showSuspendTableTitle) {
                            fl_tableTitle_container.removeView(suspensionView);
                            ((NativeReportActivity) getActivity()).getSuspendContainer().addView(suspensionView);
                        }
                    }
                } else {
                    if (getActivity() instanceof NativeReportActivity) {
                        int viewCont = ((NativeReportActivity) getActivity()).getSuspendContainer().getChildCount();
                        boolean removeSuspendTableTitle = globalOffset.y > offsetTop || rect.bottom - DisplayUtil.dip2px(getActivity(), 46) < offsetTop && viewCont != 0;
                        if (removeSuspendTableTitle) {
                            ((NativeReportActivity) getActivity()).getSuspendContainer().removeView(suspensionView);
                            fl_tableTitle_container.addView(suspensionView);
                        }
                    }
                }
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fm = getFragmentManager();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.modulartwo_unittablescontfragment, container, false);
            x.view().inject(this, rootView);
            nslistView_LineName.setFocusable(false);

            suspensionView = LayoutInflater.from(ctx).inflate(R.layout.item_suspension, null);
            fl_tableTitle_container.addView(suspensionView);
            tv_header = suspensionView.findViewById(R.id.tv_unit_table_header);
            thscroll_header = suspensionView.findViewById(R.id.thscroll_unit_table_header);
            linear_header = suspensionView.findViewById(R.id.ll_unit_table_header);

            // 获取 标题栏 和 筛选框 的高度
            getTitleHeight();

            thscroll_header.setScrollView(thscroll_data);
            thscroll_data.setScrollView(thscroll_header);

            rootView.post(new Runnable() {
                @Override
                public void run() {
                    if (getActivity() == null) {
                        return;
                    }
                    Rect frame = new Rect();
                    getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    //状态栏 + 标题栏高度 - 间隙
                    if (mNativeReportActionBarHight != 0) {
                        mTitleHigh = +mNativeReportActionBarHight;
                    }
                    if (mNativeReportLlFilterHight != 0) {
                        mTitleHigh += mNativeReportLlFilterHight;
                    }
                    offsetTop = frame.top + mTitleHigh;
                }
            });

            dataComparator = new TableDataComparator();
            LogUtil.d(this, "mParam ::: " + mParam);
            mPresenter.loadData(mParam);
        }
        return rootView;
    }

    /**
     * 获取 标题栏 和 筛选框 的高度
     */
    private void getTitleHeight() {
        if (getActivity() != null && getActivity() instanceof NativeReportActivity) {
            NativeReportActivity nativeReportActivity = (NativeReportActivity) getActivity();
            mRlActionBar = nativeReportActivity.getActionbar();
            mLlFilter = nativeReportActivity.getMLlFilter();
            mRlActionBar.post(new Runnable() {
                @Override
                public void run() {
                    mNativeReportActionBarHight = mRlActionBar.getHeight();
                }
            });
            mLlFilter.post(new Runnable() {
                @Override
                public void run() {
                    mNativeReportLlFilterHight = mLlFilter.getHeight();
                }
            });
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }


    @Override
    public void showData(@NotNull Tables data) {
        bindData(data);
    }

    /**
     * 绑定数据
     */
    private void bindData(Tables entity) {
        this.dataEntity = entity;
        // 表头数据
        String[] header = entity.getHead();
        if (header.length == 0) {
            return;
        }

        tv_header.setText(header[0]);
        headerSize = header.length - 1;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        SortViewClickListener listener = new SortViewClickListener();

        al_SortView.clear();
        linear_header.removeAllViews();

        headerData.addAll(Arrays.asList(header).subList(1, headerSize + 1));

        lineData.add(headerData);
        int entityDataSize = entity.getData().size();
        int mainDataLength;
        for (int i = 0; i < headerSize; i++) {
            for (int j = 0; j < entityDataSize; j++) {
                ArrayList<String> data = new ArrayList<>();
                mainDataLength = entity.getData().get(j).getMain_data().length;
                for (int k = 1; k < mainDataLength; k++) {
                    data.add(entity.getData().get(j).getMain_data()[k]);
                }
                lineData.add(data);
            }
        }

        ArrayList<Integer> mColumnMaxWidths = new ArrayList<Integer>();
        //初始化每列最大宽度
        int lineDataSize = lineData.size();
        int rowDataSize;
        for (int i = 0; i < lineDataSize; i++) {
            ArrayList<String> rowDatas = lineData.get(i);
            rowDataSize = rowDatas.size();
            for (int j = 0; j < rowDataSize; j++) {
                TextView textView = new TextView(ctx);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setText(rowDatas.get(j));
                if (i != 0) {
                    try {
                        textView.setText(new JSONObject(rowDatas.get(j)).getString("value"));
                    } catch (JSONException e) {
                        textView.setText("00000");
                        e.printStackTrace();
                    }
                }

                textView.setGravity(Gravity.CENTER);
                //设置布局
                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewParams.setMargins(DisplayUtil.dip2px(ctx, 10), 30, DisplayUtil.dip2px(ctx, 10), 30);
                textView.setLayoutParams(textViewParams);
                if (i == 0) {
                    mColumnMaxWidths.add(measureTextWidth(textView, rowDatas.get(j)));
                } else {
                    int length = mColumnMaxWidths.get(j);
                    int current = measureTextWidth(textView, rowDatas.get(j));
                    if (current > length) {
                        mColumnMaxWidths.set(j, current);
                    }
                }
            }
        }

        // 遍历表头数据, 添加到 al_SortView
        for (int i = 0; i < headerSize; i++) {
            SortCheckBox box = (SortCheckBox) inflater.inflate(R.layout.item_table_sortcheckbox, null);
            box.setDrawingCacheEnabled(true);
            box.setText(header[i + 1]);
            box.setBoxWidth(DisplayUtil.dip2px(ctx, mColumnMaxWidths.get(i)));
            box.setTextSize(DisplayUtil.dip2px(getContext(), 14));
            box.setTag(i + 1);
            box.setOnClickListener(listener);
            box.setOnSortViewSizeListener(this);
            linear_header.addView(box);
            al_SortView.add(box);
        }

    }

    @Override
    public void onSortViewSize(int width, Object tag) {
        int index = (int) tag;
        if (index == 1) {
            al_HeaderLenght.clear();
        }
        al_HeaderLenght.add(index - 1, width);

        // 表头加载完成后, 开始加载表格首列及表格内容
        if (al_HeaderLenght.size() == headerSize) {
            // 加载首列数据
            loadTableLeftData();
            // 加载表格内容
            loadTableContentData();
        }
    }

    /**
     * 加载首列数据
     */
    private void loadTableLeftData() {
        nameAdapter = new TableNameAdapter(ctx, dataEntity.getData());
        nslistView_LineName.setAdapter(nameAdapter);
        ViewGroup.LayoutParams params = nslistView_LineName.getLayoutParams();
        params.height = nslistView_LineName.getTotalHeight();
        nslistView_LineName.setLayoutParams(params);
        nslistView_LineName.setOnItemClickListener(this);
    }

    /**
     * 加载表格内容
     */
    private void loadTableContentData() {
        ArrayList<Tables.TableRowEntity> datas = dataEntity.getData();
        ArrayMap<Integer, String[]> lables = new ArrayMap<>();
        int dataSize = datas.size();
        for (int i = 0; i < dataSize; i++) {
            lables.put(i, datas.get(i).getMain_data());
        }

        int itemHeight = getResources().getDimensionPixelSize(R.dimen.size_default);
        int dividerColor = ContextCompat.getColor(getContext(), R.color.co9);
        int textColor = ContextCompat.getColor(getContext(), R.color.co6_syr);
        tableValue = new TableValueView(ctx);
        tableValue.setItemHeight(itemHeight);
        tableValue.setHeaderLengths(al_HeaderLenght);
        tableValue.setTextSize(DisplayUtil.dip2px(getContext(), 13));
        tableValue.setTableValues(lables);
        tableValue.setDividerColor(dividerColor);
        tableValue.setTextColor(textColor);
        fl_tableValue_container.addView(tableValue);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        Tables modularTwoUnitTableEntitySubData = dataEntity.getData().get(position).getSub_data();
        boolean subDataNull = modularTwoUnitTableEntitySubData == null || (modularTwoUnitTableEntitySubData.getData() == null && modularTwoUnitTableEntitySubData.getHead() == null);
        if (subDataNull) {
            ToastUtils.INSTANCE.showDefault(getActivity(), JSON.parseObject(dataEntity.getData().get(position).getMain_data()[0]).getString("value"));
            return;
        }
        startSubTable(position);
    }

    class SortViewClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            int tag = (int) v.getTag();
            int index = tag - 1;
            for (int i = 0; i < headerSize; i++) {
                if (i != index) {
                    al_SortView.get(i).reset();
                }
            }

            SortCheckBox box = al_SortView.get(index);
            if (box.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                dataComparator.setIndex(tag);
                Collections.sort(dataEntity.getData(), dataComparator);
            } else {
                Collections.reverse(dataEntity.getData());
            }

            nameAdapter.updateData(dataEntity.getData());
            updateTableValue();
        }
    }

    public void updateTableValue() {
        ArrayList<Tables.TableRowEntity> datas = dataEntity.getData();
        ArrayMap<Integer, String[]> lables = new ArrayMap<>();
        int dataSize = datas.size();
        for (int i = 0; i < dataSize; i++) {
            lables.put(i, datas.get(i).getMain_data());
        }
        tableValue.setTableValues(lables);
        tableValue.invalidate();
    }

    class TableDataComparator implements Comparator<Tables.TableRowEntity> {
        NumberFormat nf = NumberFormat.getInstance();
        DecimalFormat df = new DecimalFormat("########.####");
        int index;

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public int compare(Tables.TableRowEntity obj1, Tables.TableRowEntity obj2) {
            float v1 = 0;
            float v2 = 0;
            try {
                String strv1 = new JSONObject(obj1.getMain_data()[index]).getString("value");
                String strv2 = new JSONObject(obj2.getMain_data()[index]).getString("value");

                if (strv1.contains("%")) {
                    v1 = Float.valueOf(strv1.substring(0, strv1.indexOf("%"))) / 100;
                } else {
                    v1 = Float.valueOf(df.format(Float.valueOf(strv1)));
                }

                if (strv2.contains("%")) {
                    v2 = Float.valueOf(strv2.substring(0, strv2.indexOf("%"))) / 100;
                } else {
                    v2 = Float.valueOf(df.format(Float.valueOf(strv2)));
                }
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }

            if (v1 > v2) {
                return 1;
            } else if (v1 < v2) {
                return -1;
            } else {
                return 0;
            }
        }
    }


    /**
     * 加载子表格
     *
     * @param index
     */
    public void startSubTable(int index) {
        try {
            TempSubData.setData(index, dataEntity.getData().get(index).getSub_data());
            Intent intent = new Intent(ctx, SubTableActivity.class);
            String itemData = dataEntity.getData().get(index).getMain_data()[0];
            intent.putExtra("Title", new JSONObject(itemData).getString("value"));
            int checkId = suRootID;
            intent.putExtra(SU_ROOT_ID, checkId);
            intent.putExtra(TABLE_ROOT_INDEX, index);
            startActivity(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据最大最小值，计算TextView的宽度
     *
     * @param textView
     * @param text
     * @return
     */
    private int measureTextWidth(TextView textView, String text) {
        if (textView != null) {
            textView.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) textView.getLayoutParams();
            int width = DisplayUtil.px2dip(ctx, layoutParams.leftMargin) +
                    DisplayUtil.px2dip(ctx, layoutParams.rightMargin) +
                    DisplayUtil.px2dip(ctx, textView.getMeasuredWidth());
            if (width <= 10) {
                return 10;
            } else if (width > 10 && width <= 1000) {
                return width;
            } else {
                return 1000;
            }
        }
        return 0;
    }
}
