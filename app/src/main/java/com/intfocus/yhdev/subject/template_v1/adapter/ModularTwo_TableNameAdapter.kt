package com.intfocus.yhdev.subject.template_v1.adapter

import android.content.Context
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.intfocus.yhdev.R
import com.intfocus.yhdev.subject.template_v1.entity.ModularTwo_UnitTableEntity

import org.json.JSONObject
import org.xutils.view.annotation.ViewInject
import org.xutils.x

/**
 * 表格第一列名字列表适配器
 * Created by zbaoliang on 17-5-15.
 */
class ModularTwo_TableNameAdapter(private val ctx: Context, ltdata: List<ModularTwo_UnitTableEntity.TableRowEntity>) : BaseAdapter() {
    private var ltdata: List<ModularTwo_UnitTableEntity.TableRowEntity>? = null

    private val defauteColor: Int
    private val hasSubColor: Int

    init {
        defauteColor = ctx.resources.getColor(R.color.co3)
        hasSubColor = ctx.resources.getColor(R.color.co14)
        setData(ltdata)
    }

    private fun setData(ltdata: List<ModularTwo_UnitTableEntity.TableRowEntity>?) {
        if (ltdata == null)
            return
        this.ltdata = ltdata
    }

    fun updateData(ltdata: List<ModularTwo_UnitTableEntity.TableRowEntity>) {
        setData(ltdata)
        notifyDataSetChanged()
    }

    override fun getCount(): Int {
        return if (ltdata == null) 0 else ltdata!!.size
    }

    override fun getItem(position: Int): Any {
        return ltdata!![position].main_data[0]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var convertView = convertView
        val viewHolder: ViewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(ctx).inflate(R.layout.item_table_name, parent, false)
            viewHolder = ViewHolder()
            x.view().inject(viewHolder, convertView)
            convertView!!.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolder
        }

        val entity = ltdata!![position]

        if (entity.sub_data == "{}") {
            viewHolder.img_dot!!.visibility = View.GONE
            viewHolder.tv_name!!.setTextColor(defauteColor)
        } else {
            viewHolder.tv_name!!.setTextColor(hasSubColor)
            viewHolder.img_dot!!.visibility = View.GONE
        }

        viewHolder.tv_name!!.text = JSONObject(entity.main_data[0]).getString("value")
        viewHolder.tv_name!!.gravity = Gravity.LEFT
        return convertView
    }

    internal class ViewHolder {
        @ViewInject(R.id.tv_tableName_value)
        var tv_name: TextView? = null
        @ViewInject(R.id.img_tableName_dot)
        var img_dot: ImageView? = null
    }
}
