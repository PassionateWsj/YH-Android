package com.intfocus.spy_template.business.subject.templateone.adapter

import android.content.Context
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.intfocus.spy_template.R
import org.json.JSONObject
import org.xutils.view.annotation.ViewInject
import org.xutils.x

/**
 * 表格第一列名字列表适配器
 * Created by zbaoliang on 17-5-15.
 */
class ModularOneTableNameAdapter(private val ctx: Context, ltdata: List<com.intfocus.spy_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity.TableRowEntity>) : BaseAdapter() {
    private var ltdata: List<com.intfocus.spy_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity.TableRowEntity>? = null

    private val defaultColor: Int = ContextCompat.getColor(ctx,R.color.co6)
    private val hasSubColor: Int = ContextCompat.getColor(ctx,R.color.co15)

    init {
        setData(ltdata)
    }

    private fun setData(ltdata: List<com.intfocus.spy_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity.TableRowEntity>?) {
        if (ltdata == null)
            return
        this.ltdata = ltdata
    }

    fun updateData(ltdata: List<com.intfocus.spy_template.business.subject.template.one.entity.ModularTwo_UnitTableEntity.TableRowEntity>) {
        setData(ltdata)
        notifyDataSetChanged()
    }

    override fun getCount(): Int = if (ltdata == null) 0 else ltdata!!.size

    override fun getItem(position: Int): Any = ltdata!![position].main_data[0]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var mConvertView = convertView
        val viewHolder: ViewHolder
        if (mConvertView == null) {
            mConvertView = LayoutInflater.from(ctx).inflate(R.layout.item_table_name, parent, false)
            viewHolder = ViewHolder()
            x.view().inject(viewHolder, mConvertView)
            mConvertView!!.tag = viewHolder
        } else {
            viewHolder = mConvertView.tag as ViewHolder
        }

        val entity = ltdata!![position]

        if (entity.sub_data == "{}") {
            viewHolder.tvName!!.setTextColor(defaultColor)
            viewHolder.imgDot!!.visibility = View.GONE
        } else {
            viewHolder.tvName!!.setTextColor(hasSubColor)
            viewHolder.imgDot!!.visibility = View.VISIBLE
        }

        viewHolder.tvName!!.text = JSONObject(entity.main_data[0]).getString("value")
//        viewHolder.tvName!!.gravity = Gravity.LEFT
        return mConvertView
    }

    internal class ViewHolder {
        @ViewInject(R.id.tv_tableName_value)
        var tvName: TextView? = null
        @ViewInject(R.id.img_tableName_dot)
        var imgDot: ImageView? = null
    }
}
