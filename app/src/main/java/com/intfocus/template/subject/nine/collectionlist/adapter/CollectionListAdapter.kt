package com.intfocus.template.subject.nine.collectionlist.adapter

import android.widget.Toast
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.intfocus.template.R
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.util.TimeUtils

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/29 下午0:01
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class CollectionListAdapter : BaseQuickAdapter<Collection, BaseViewHolder>(R.layout.item_collection_list) {
    override fun convert(helper: BaseViewHolder, item: Collection) {
        val status = when {
            item.status == 1 -> ""
            item.status == 0 -> "上传中"
            else -> "草稿"
        }
        helper.setText(R.id.tv_item_collection_list_status, status)
                .setText(R.id.tv_item_collection_list_title, item.h1 ?: "")
                .setText(R.id.tv_item_collection_list_content, item.h2 ?: "")
                .setText(R.id.tv_item_collection_list_title_label, item.h3 ?: "")
                .setGone(R.id.right_menu_sync, item.status != 1)
                .setOnClickListener(R.id.rl_item_collection_list_container, { view ->
                    Toast.makeText(mContext, "onItemClick" + data.indexOf(item), Toast.LENGTH_SHORT).show()
                })
                .setOnClickListener(R.id.right_menu_delete, { view ->
                    remove(data.indexOf(item))
                })
                .setOnClickListener(R.id.right_menu_sync, { view ->
                    Toast.makeText(mContext, "onItemClick" + data.indexOf(item), Toast.LENGTH_SHORT).show()
                })
        item.updated_at?.let {
            helper.setText(R.id.tv_item_collection_list_time, TimeUtils.getStandardDate(it))
        }
    }
}