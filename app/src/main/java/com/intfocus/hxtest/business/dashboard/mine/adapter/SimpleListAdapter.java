package com.intfocus.hxtest.business.dashboard.mine.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.intfocus.hxtest.R;

import java.util.List;
import java.util.Map;

/**
 * Created by liuruilin on 2017/4/24.
 */

public class SimpleListAdapter extends SimpleAdapter {
    private final Context mContext;
    private List<? extends Map<String, ?>> listItem;

    public SimpleListAdapter(Context context, List<? extends Map<String, ?>> data,
                             int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        this.mContext = context;
        this.listItem = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        TextView mItemContent = (TextView) v.findViewById(R.id.item_setting_info);
        ImageView mItemArrow = (ImageView) v.findViewById(R.id.item_setting_arrow);

        mItemContent.setVisibility(View.GONE);
        mItemArrow.setVisibility(View.GONE);
        if ("arrow".equals(listItem.get(position).get("ItemContent"))) {
            mItemArrow.setVisibility(View.VISIBLE);
        } else {
            mItemContent.setVisibility(View.VISIBLE);
        }

        return v;
    }
}
