package com.intfocus.yhdev.subject.template_v1;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.util.ArrayMap;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.base.BaseModeFragment;
import com.intfocus.yhdev.subject.template_v1.adapter.ModularTwo_TableNameAdapter;
import com.intfocus.yhdev.subject.template_v1.adapter.ModularTwo_TableValueAdapter;
import com.intfocus.yhdev.subject.template_v1.entity.DataHolder;
import com.intfocus.yhdev.subject.template_v1.entity.ModularTwo_UnitTableEntity;
import com.intfocus.yhdev.subject.template_v1.entity.msg.EventRefreshTableRect;
import com.intfocus.yhdev.subject.template_v1.mode.ModularTwo_UnitTableContMode;
import com.intfocus.yhdev.util.DisplayUtil;
import com.intfocus.yhdev.util.ToastUtils;
import com.intfocus.yhdev.view.NotScrollListView;
import com.intfocus.yhdev.view.RootScrollView;
import com.intfocus.yhdev.view.SortCheckBox;
import com.intfocus.yhdev.view.TableHorizontalScrollView;
import com.intfocus.yhdev.view.TableValueView;
import com.zbl.lib.baseframe.core.Subject;
import com.zbl.lib.baseframe.utils.ToastUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * 模板一表格内容页面
 */
public class ModularTwo_UnitTablesContModeFragment extends BaseModeFragment<ModularTwo_UnitTableContMode> implements SortCheckBox.SortViewSizeListener, AdapterView.OnItemClickListener {
    private static final String ARG_PARAM = "param";
    private static final String SU_ROOTID = "suRootID";
    public static int mCurrentSuRootID;
    private String mParam;

    private View rootView;

    public static String mCurrentData;

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
//    @ViewInject(R.id.ll_unit_table_header)
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

    private ModularTwo_UnitTableEntity dataEntity;
    private int headerSize;
    private ModularTwo_TableNameAdapter nameAdapter;
    private ModularTwo_TableValueAdapter valueAdapter;
    private TableDataComparator dataComparator;
    private TableValueView tableValue;
    /**
     * 悬浮View
     */
    private View suspensionView;
    private String TAG = ModularTwo_UnitTablesContModeFragment.class.getSimpleName();

    private int offsetTop;

    /**
     * 最上层跟跟标签ID
     */
    public int suRootID;

    @Override
    public Subject setSubject() {
        return new ModularTwo_UnitTableContMode(ctx);
    }

    public static ModularTwo_UnitTablesContModeFragment newInstance(int suRootID, String param) {
        ModularTwo_UnitTablesContModeFragment fragment = new ModularTwo_UnitTablesContModeFragment();
        mCurrentData = param;
        mCurrentSuRootID = suRootID;
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        mParam = mCurrentData;
        suRootID = mCurrentSuRootID;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void measureLocation(EventRefreshTableRect event) {
        ModularTwo_UnitTablesModeFragment parentFt = (ModularTwo_UnitTablesModeFragment) getParentFragment();
        if (parentFt == null)
            return;
        final int surootID = suRootID;
        if (event.eventTag == surootID) {
            ((ModularTwo_Mode_Activity) getActivity()).rScrollView.setOnScrollListener(new RootScrollView.OnScrollListener() {
                @Override
                public void onScroll(int scrollY) {

                    Rect rect = new Rect();
                    rootView.getGlobalVisibleRect(rect);
                    synchronized (this) {
                        if (fl_tableTitle_container.getChildCount() != 0) {
                            if (rect.top <= offsetTop && rect.bottom - 150 > offsetTop) {
                                Log.i("testlog", "1");
                                fl_tableTitle_container.removeView(suspensionView);
                                ((ModularTwo_Mode_Activity) getActivity()).suspendContainer.addView(suspensionView);
                            }
                        }
                        else {
                            int viewCont = ((ModularTwo_Mode_Activity) getActivity()).suspendContainer.getChildCount();
                            if (rect.top > offsetTop || rect.bottom - 150 < offsetTop && viewCont != 0) {
                                Log.i("testlog", "2");
                                ((ModularTwo_Mode_Activity) getActivity()).suspendContainer.removeView(suspensionView);
                                fl_tableTitle_container.addView(suspensionView);
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fm = getFragmentManager();
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.modulartwo_unittablescontfragment, container, false);
            x.view().inject(this, rootView);
            nslistView_LineName.setFocusable(false);

            dataComparator = new TableDataComparator();

            suspensionView = LayoutInflater.from(ctx).inflate(R.layout.item_suspension, null);
            fl_tableTitle_container.addView(suspensionView);
            tv_header = (TextView) suspensionView.findViewById(R.id.tv_unit_table_header);
            thscroll_header = (TableHorizontalScrollView) suspensionView.findViewById(R.id.thscroll_unit_table_header);
            linear_header = (LinearLayout) suspensionView.findViewById(R.id.ll_unit_table_header);

            thscroll_header.setScrollView(thscroll_data);
            thscroll_data.setScrollView(thscroll_header);
            hideLoading();
            rootView.post(new Runnable() {
                @Override
                public void run() {
                    Rect frame = new Rect();
                    act.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
                    offsetTop = frame.top + 150 - 30;//状态栏+标题栏高度-间隙

                    Log.i(TAG, "offsetTop:" + offsetTop);
                }
            });

            getModel().analysisData(mParam);
        }
        return rootView;
    }

    /**
     * 图表点击事件统一处理方法
     */
    public void onMessageEvent(final ModularTwo_UnitTableEntity entity) {
        act.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bindData(entity);

                int checkId;
                FragmentActivity fa = getActivity();
                if (fa instanceof ModularTwo_SubTableActivity) {
                    checkId = ((ModularTwo_SubTableActivity) fa).suRootID;
                } else {
                    checkId = suRootID;
                }
                EventBus.getDefault().post(new EventRefreshTableRect(checkId));
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindData(ModularTwo_UnitTableEntity result) {
        this.dataEntity = result;
        String[] header = result.head; // 表头数据
        tv_header.setText(header[0]);

        headerSize = header.length - 1;
        LayoutInflater inflater = LayoutInflater.from(ctx);
        SortViewClickListener listener = new SortViewClickListener();

        al_SortView.clear();
        linear_header.removeAllViews();

        for (int i = 0; i < headerSize; i++) {
            headerData.add(header[i + 1]);
        }

        lineData.add(headerData);
        for (int i = 0; i < headerSize; i++) {
            for (int j = 0; j < result.data.size(); j++) {
                ArrayList<String> data = new ArrayList<>();
                for (int k = 1; k < result.data.get(j).main_data.length; k++) {
                    data.add(result.data.get(j).main_data[k]);
                }
                lineData.add(data);
            }
        }

        ArrayList<Integer> mColumnMaxWidths = new ArrayList<Integer>();
        //初始化每列最大宽度
        for (int i = 0; i < lineData.size(); i++) {
            ArrayList<String> rowDatas = lineData.get(i);
            for (int j = 0; j < rowDatas.size(); j++) {
                TextView textView = new TextView(ctx);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
                textView.setText(rowDatas.get(j));
                textView.setGravity(Gravity.CENTER);
                //设置布局
                LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT);
                textViewParams.setMargins(30, 30, 30, 30);
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
            box.setText(header[i + 1]);
            box.setBoxWidth(DisplayUtil.dip2px(ctx, mColumnMaxWidths.get(i)));
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
        if (index == 1)
            al_HeaderLenght.clear();
        al_HeaderLenght.add(index - 1, width);

        // 表头加载完成后, 开始加载表格首列及表格内容
        if (al_HeaderLenght.size() == headerSize) {
            loadTableLeftData(); // 加载首列数据
            loadTableContentData(); // 加载表格内容
        }
    }

    /*
     * 加载首列数据
     */
    private void loadTableLeftData() {
        nameAdapter = new ModularTwo_TableNameAdapter(ctx, dataEntity.data);
        nslistView_LineName.setAdapter(nameAdapter);
        ViewGroup.LayoutParams params = nslistView_LineName.getLayoutParams();
        params.height = nslistView_LineName.getTotalHeight();
        nslistView_LineName.setLayoutParams(params);
        nslistView_LineName.setOnItemClickListener(this);
    }

    /*
     * 加载表格内容
     */
    private void loadTableContentData() {
        ArrayList<ModularTwo_UnitTableEntity.TableRowEntity> datas = dataEntity.data;
        ArrayMap<Integer, String[]> lables = new ArrayMap<>();
        int dataSize = datas.size();
        for (int i = 0; i < dataSize; i++) {
            lables.put(i, datas.get(i).main_data);
        }

        int itemHeight = getResources().getDimensionPixelSize(R.dimen.size_table_small);
        int dividerColor = getResources().getColor(R.color.co9);
        int textColor = getResources().getColor(R.color.co3);
        tableValue = new TableValueView(ctx);
        tableValue.setItemHeight(itemHeight);
        tableValue.setHeaderLenghts(al_HeaderLenght);
        tableValue.setTableValues(lables);
        tableValue.setDeviderColor(dividerColor);
        tableValue.setTextColor(textColor);
        fl_tableValue_container.addView(tableValue);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        if (dataEntity.data.get(position).sub_data == null) {
            ToastUtils.INSTANCE.showDefault(ctx, dataEntity.data.get(position).main_data[0]);
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
                if (i != index)
                    al_SortView.get(i).reset();
            }

            //TODO 执行排序功能
            SortCheckBox box = al_SortView.get(index);
            if (box.getCheckedState() == SortCheckBox.CheckedState.sort_noneicon) {
                dataComparator.setIndex(tag);
                Collections.sort(dataEntity.data, dataComparator);
            } else
                Collections.reverse(dataEntity.data);

            nameAdapter.updateData(dataEntity.data);
            updateTableValue();
        }
    }

    public void updateTableValue() {
        ArrayList<ModularTwo_UnitTableEntity.TableRowEntity> datas = dataEntity.data;
        ArrayMap<Integer, String[]> lables = new ArrayMap<>();
        int dataSize = datas.size();
        for (int i = 0; i < dataSize; i++) {
            lables.put(i, datas.get(i).main_data);
        }
        tableValue.setTableValues(lables);
        tableValue.invalidate();
    }

    class TableDataComparator implements Comparator<ModularTwo_UnitTableEntity.TableRowEntity> {
        NumberFormat nf = NumberFormat.getInstance();
        DecimalFormat df = new DecimalFormat("########.####");
        int index;

        public void setIndex(int index) {
            this.index = index;
        }

        public int compare(ModularTwo_UnitTableEntity.TableRowEntity obj1, ModularTwo_UnitTableEntity.TableRowEntity obj2) {
            String strv1 = obj1.main_data[index];
            String strv2 = obj2.main_data[index];

            float v1 = 0;
            float v2 = 0;
            try {
                if (strv1.contains("%"))
                    v1 = new Float(strv1.substring(0, strv1.indexOf("%"))) / 100;
                else
                    v1 = Float.valueOf(df.format(Float.valueOf(strv1)));

                if (strv2.contains("%"))
                    v2 = new Float(strv2.substring(0, strv2.indexOf("%"))) / 100;
                else
                    v2 = Float.valueOf(df.format(Float.valueOf(strv2)));
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
            JSONObject sub_data = new JSONObject(dataEntity.data.get(index).sub_data);
            if (sub_data == null) {
                String tableName = (String) nameAdapter.getItem(index);
                ToastUtil.showToast(ctx, tableName);
                return;
            }

            JSONObject jsonObject = new JSONObject();
            String header = sub_data.getJSONArray("head").toString();
            jsonObject.put("head", new JSONArray(header));
            JSONArray array = sub_data.getJSONArray("data");
            jsonObject.put("data", array);
            String subdata = jsonObject.toString();

            Intent intent = new Intent(ctx, ModularTwo_SubTableActivity.class);
            String itemName = dataEntity.data.get(index).main_data[0];
            intent.putExtra("Title", itemName);
            DataHolder.getInstance().setData(subdata);
            int checkId = suRootID;
            intent.putExtra("suRootID", checkId);
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
