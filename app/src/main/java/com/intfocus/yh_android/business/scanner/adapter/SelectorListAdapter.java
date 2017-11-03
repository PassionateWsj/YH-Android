package com.intfocus.yh_android.business.scanner.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.intfocus.yh_android.R;
import com.intfocus.yh_android.general.data.response.scanner.StoreItem;

import java.util.ArrayList;
import java.util.List;

/**
 * ****************************************************
 * @author JamesWong
 * created on: 17/08/22 上午10:53
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */

public class SelectorListAdapter extends BaseAdapter {
    private Context mContext;
    private List<StoreItem> items;

    public SelectorListAdapter(Context context) {
        mContext = context;
    }

    @Override
    public int getCount() {
        return items == null ? 0 : items.size();
    }

    @Override
    public Object getItem(int position) {
        return items == null ? null : items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SelectorListHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_report_selector, parent, false);
            holder = new SelectorListHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (SelectorListHolder) convertView.getTag();
        }
        holder.reportSelectorItem.setText(items.get(position).getName());
        holder.reportSelectorItemId.setText(items.get(position).getId());
        return convertView;
    }

    public void setData(List<StoreItem> data) {
        if (items == null) {
            items = new ArrayList<>();
        }
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    public StoreItem getSelectItem(int pos) {
        return items.get(pos);
    }

    class SelectorListHolder {
        private TextView reportSelectorItem;
        private TextView reportSelectorItemId;

        public SelectorListHolder(View convertView) {
            reportSelectorItem = (TextView) convertView.findViewById(R.id.report_selector_item);
            reportSelectorItemId = (TextView) convertView.findViewById(R.id.report_selector_item_id);
        }
    }
}
