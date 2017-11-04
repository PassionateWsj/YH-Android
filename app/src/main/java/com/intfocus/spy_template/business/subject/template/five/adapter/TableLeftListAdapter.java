package com.intfocus.spy_template.business.subject.template.five.adapter;

import android.content.Context;
import android.widget.TextView;

import com.intfocus.spy_template.R;
import com.intfocus.spy_template.general.util.Utils;
import com.intfocus.spy_template.business.subject.template.five.adapter.base.CommonAdapter;
import com.intfocus.spy_template.business.subject.template.five.adapter.base.ViewHolder;

import java.util.List;

import static android.text.TextUtils.TruncateAt.MIDDLE;

/**
 * Created by CANC on 2017/4/19.
 */

public class TableLeftListAdapter extends CommonAdapter<String> {

    private int rowHeight;//1行,2行,3行

    public TableLeftListAdapter(Context context, List<String> datas, int rowHeight) {
        super(context, datas);
        this.layoutId = R.layout.item_table_left;
        this.rowHeight = rowHeight;
    }

    @Override
    public void convert(ViewHolder holder, String s) {
        TextView tvHead = holder.getView(R.id.tv_left);
        tvHead.setText(s);
        tvHead.setEllipsize(MIDDLE);
        tvHead.setMaxLines(rowHeight);
        tvHead.getLayoutParams().height = Utils.dpToPx(context, 25 * rowHeight);
    }
}
