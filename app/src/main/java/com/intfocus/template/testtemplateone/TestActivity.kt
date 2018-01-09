package com.intfocus.template.testtemplateone

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.model.DaoUtil
import com.intfocus.template.subject.one.NativeReportActivity
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.util.SnackbarUtil
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.activity_test.*

class TestActivity : BaseActivity() {

    private val testReportIds = arrayListOf(
            "2",
            "3",
            "4",
            "5",
            "6",
            "7",
            "8",
            "9",
            "10",
            "11",
            "30",
            "31",
            "32",
            "67",
            "70",
            "72",
            "73",
            "75",
            "76",
            "77",
            "102",
            "103",
            "104",
            "105",
            "888",
            "9901",
            "9902",
            "9903",
            "9904",
            "84"
    )
    private var mExitTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test)

        val adapter = TestListAdapter(this)
        rl_test.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rl_test.adapter = adapter
        adapter.setNewData(testReportIds)

        fab_test_del.setOnClickListener {
            SnackbarUtil.shortSnackbar(it,
                    "清空数据库成功",
                    ContextCompat.getColor(this, R.color.co10_syr),
                    ContextCompat.getColor(this, R.color.co1_syr))
                    .show()
            DaoUtil.getReportDao().deleteAll()
        }
    }

    class TestListAdapter(val ctx: Context) : BaseQuickAdapter<String, BaseViewHolder>(R.layout.item_test_list) {
        private val testReportTitles = arrayListOf(
                "模板一报表",
                "有机馆销售(门店)",
                "有机馆销售(商行)",
                "箱子数统计",
                "生鲜买手采购销售",
                "第二集群生鲜销售概况",
                "生鲜采购码头销售",
                "物流库存",
                "食用品折价商品销售(门店)",
                "食用品折价商品销售(商行)",
                "区域实时销售",
                "门店实时销售",
                "小店实时销售",
                "个单量异常实时预警(门店)",
                "生鲜定价毛利异常实时预警(门店)",
                "返配概况(小店长)",
                "生鲜负毛利实时预警(门店)",
                "返配概况(区总/营运)",
                "区总团队商品分析",
                "小店长商品分析",
                "天天赛马",
                "赛马成绩查询",
                "天天赛马",
                "赛马成绩查询",
                "报表监控",
                "门店销售概况",
                "小店销售概况",
                "区域销售概况",
                "品类同赛马群销售概况",
                "商品分析测试"
        )

        override fun convert(helper: BaseViewHolder, item: String) {
            helper.setText(R.id.tv_test_report_id, item)
                    .setText(R.id.tv_test_report_title, testReportTitles[helper.layoutPosition])
                    .setOnClickListener(R.id.test_container, { view ->
//                        ToastUtils.show(ctx, testReportTitles[helper.layoutPosition] + " 报表id:" + item, ToastColor.SUCCESS)
                        val intent = Intent(ctx, NativeReportActivity::class.java)
                        intent.putExtra(Params.GROUP_ID, "165")
                        intent.putExtra(Params.TEMPLATE_ID, "1")
                        intent.putExtra(Params.BANNER_NAME, testReportTitles[helper.layoutPosition] + " 报表id:" + item)
                        intent.putExtra(Params.LINK, "")
                        intent.putExtra(Params.OBJECT_ID, item)
                        intent.putExtra(Params.OBJECT_TYPE, "-1")
                        ctx.startActivity(intent)
                    })
        }
    }

    override fun onBackPressed() {
        if ((System.currentTimeMillis() - mExitTime) > 200) {
            ToastUtils.showDefault(this, "不准走, 再试试")
            mExitTime = System.currentTimeMillis()
            return
        }
        super.onBackPressed()
    }
}