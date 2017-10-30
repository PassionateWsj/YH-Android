package com.intfocus.yhdev.subject.template_v1.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.subject.template_v1.entity.BargraphComparator;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.LinkedList;

/**
 * Created by zbaoliang on 17-5-15.
 */
public class BargraptAdapter extends BaseAdapter {
    private Context ctx;
    private LinkedList<BargraphComparator> ltdata;
    private Drawable herearrow;
    private int selectItemIndex = 0;
    private int percentDefaultColor;
    private int defaultColor;
    private int selectColor;

    public BargraptAdapter(Context ctx) {
        this.ctx = ctx;
        herearrow = ctx.getResources().getDrawable(R.drawable.icon_herearrow);
        herearrow.setBounds(0, 0, herearrow.getMinimumWidth(),
                herearrow.getMinimumHeight());
        percentDefaultColor = ctx.getResources().getColor(R.color.co4_syr);
        defaultColor = ctx.getResources().getColor(R.color.co3_syr);
        selectColor = ctx.getResources().getColor(R.color.co14_syr);
    }

    public void updateData(LinkedList<BargraphComparator> ltdata) {
        if (this.ltdata == null) {
            this.ltdata = new LinkedList<>();
        }
        this.ltdata.clear();
        this.ltdata.addAll(ltdata);
        selectItemIndex = 0;
        notifyDataSetChanged();
    }

    public void setSelectItem(int index) {
        selectItemIndex = index;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return ltdata == null ? 0 : ltdata.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_mdrpunit_bargraph, parent, false);
            viewHolder = new ViewHolder();
            x.view().inject(viewHolder, convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (selectItemIndex == position) {
            viewHolder.tv_name.setTextColor(selectColor);
            viewHolder.tv_percentage.setTextColor(selectColor);
            viewHolder.img_cursor.setImageResource(R.drawable.icon_herearrow);
        } else {
            viewHolder.tv_name.setTextColor(defaultColor);
            viewHolder.tv_percentage.setTextColor(percentDefaultColor);
            viewHolder.img_cursor.setImageResource(0);
        }

        viewHolder.tv_name.setText(ltdata.get(position).name);
        viewHolder.tv_percentage.setText(ltdata.get(position).data);
        return convertView;
    }

    static class ViewHolder {
        @ViewInject(R.id.tv_name)
        TextView tv_name;
        @ViewInject(R.id.tv_percentage)
        TextView tv_percentage;
        @ViewInject(R.id.img_cursor)
        ImageView img_cursor;
    }
}
