package com.intfocus.yh_android.subject

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.RadioButton
import android.widget.TextView
import com.google.gson.Gson
import com.intfocus.yh_android.R
import com.intfocus.yh_android.bean.QueryOptions
import com.intfocus.yh_android.scanner.QueryOptionsScannerActivity
import com.intfocus.yh_android.subject.adapter.ElvQueryOptionRadioBoxExpandableListAdapter
import com.intfocus.yh_android.subject.adapter.QueryOptionRadioListAdapter
import com.intfocus.yh_android.util.DisplayUtil
import com.zbl.lib.baseframe.utils.ToastUtil
import kotlinx.android.synthetic.main.activity_query_options.*
import java.text.SimpleDateFormat
import java.util.*

class QueryOptionsActivity : AppCompatActivity() {

    private val testData = "{\n" +
            "\t\"data\":[\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"text_input\",\n" +
            "\t\t\t\"typeId\":\"1\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"scan_input\",\n" +
            "\t\t\t\"typeId\":\"2\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"radio_box\",\n" +
            "\t\t\t\"typeId\":\"3\",\n" +
            "\t\t\t\"data\":[\"选项一\",\"选项二\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"check_box\",\n" +
            "\t\t\t\"typeId\":\"4\",\n" +
            "\t\t\t\"data\":[\"优惠活动1\",\"优惠活动2\",\"优惠活动3\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"radio_box_spinner\",\n" +
            "\t\t\t\"typeId\":\"5\",\n" +
            "\t\t\t\"data\":[\"100%\",\"75%\",\"50%\",\"25%\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"check_box_spinner\",\n" +
            "\t\t\t\"typeId\":\"5\",\n" +
            "\t\t\t\"data\":[\"100%\",\"75%\",\"50%\",\"25%\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"radio_list\",\n" +
            "\t\t\t\"typeId\":\"6\",\n" +
            "\t\t\t\"data\":[\"门店1\",\"门店2\",\"门店3\",\"门店4\",\"门店5\",\"门店6\",\"门店7\",\"门店8\",\"门店9\",\"门店10\",\"门店11\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"time_select\",\n" +
            "\t\t\t\"typeId\":\"7\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t},\n" +
            "\t\t{\n" +
            "\t\t\t\"typeName\":\"start_and_end_time\",\n" +
            "\t\t\t\"typeId\":\"8\",\n" +
            "\t\t\t\"data\":[\"\"]\n" +
            "\t\t}\n" +
            "\t]\n" +
            "}"

    /**
     * 起始时间
     */
    private var query_option_start_time: Long = 0
    /**
     * 结束时间
     */
    private var query_option_end_time: Long = 0
    /**
     * 单选列表当前选择的position
     */
    private var mRadioListPos: Int = 0
    /**
     * 控制单选下拉框高度的开关
     */
    private var elv_query_option_radio_box_switch = false
    /**
     * 控制多选下拉框高度的开关
     */
    private var elv_query_option_check_box_switch = false
    /**
     * 跳转扫一扫activity的请求code
     */
    private val mRequestCode = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_query_options)

        //解析的数据
        val queryOptions = Gson().fromJson(testData, QueryOptions::class.java)

        val queryOptionsData = queryOptions!!.data

        val mSubmitData = hashMapOf<String, String>()

        //初始化数据
        initData(queryOptionsData)

        //初始化监听器
        initListener(queryOptionsData, mSubmitData)
    }

    /**
     * 动态加载视图
     */
    private fun initData(queryOptionsData: List<QueryOptions.DataBean>) {
        for (i in queryOptionsData.indices) {
            when (queryOptionsData[i].typeName) {
                "text_input" -> {
                    ll_query_option_text_input.visibility = View.VISIBLE
                    et_query_option_text_input.text.clear()
                }
                "scan_input" -> {
                    ll_query_option_scan_input.visibility = View.VISIBLE
                    et_query_option_scan_input.text.clear()
                }
                "radio_box" -> {
                    ll_query_option_radio_box_input.visibility = View.VISIBLE
                    (rg_query_option.getChildAt(0) as RadioButton).text = queryOptionsData[i].data[0]
                    (rg_query_option.getChildAt(1) as RadioButton).text = queryOptionsData[i].data[1]
                    (rg_query_option.getChildAt(0) as RadioButton).isChecked = true
                }
                "check_box" -> {
                    ll_query_option_check_box_input.visibility = View.VISIBLE
                    for (s in queryOptionsData[i].data) {
                        val mCheckBox = CheckBox(this)
                        mCheckBox.buttonDrawable = resources.getDrawable(R.drawable.icon_selector_query_option_check_box)
                        mCheckBox.text = s
                        mCheckBox.textSize = 12f
                        mCheckBox.height = DisplayUtil.dip2px(this, 36f)
                        mCheckBox.setPadding(DisplayUtil.dip2px(this, 10f), 0, 0, 0)
                        ll_query_option_check_box_container.addView(mCheckBox)
                    }
                }
                "radio_box_spinner" -> {
                    ll_query_option_radio_box_drop_down_input.visibility = View.VISIBLE
                    val mElvRadioBoxExpandableListAdapter = ElvQueryOptionRadioBoxExpandableListAdapter(this, ElvQueryOptionRadioBoxExpandableListAdapter.RADIO_BOX_TYPE)
                    mElvRadioBoxExpandableListAdapter.mGroupData = listOf(queryOptionsData[i].data[0])
                    mElvRadioBoxExpandableListAdapter.mChildData = queryOptionsData[i].data
                    elv_query_option_radio_box.setAdapter(mElvRadioBoxExpandableListAdapter)

                }
                "check_box_spinner" -> {
                    ll_query_option_check_box_drop_down_input.visibility = View.VISIBLE
                    val mElvCheckBoxExpandableListAdapter = ElvQueryOptionRadioBoxExpandableListAdapter(this, ElvQueryOptionRadioBoxExpandableListAdapter.CHECK_BOX_TYPE)
                    mElvCheckBoxExpandableListAdapter.mGroupData = listOf(queryOptionsData[i].data[0])
                    mElvCheckBoxExpandableListAdapter.mChildData = queryOptionsData[i].data
                    elv_query_option_check_box.setAdapter(mElvCheckBoxExpandableListAdapter)
                }
                "radio_list" -> {
                    ll_query_option_radio_list_input.visibility = View.VISIBLE
                    lv_query_option_radio_list.adapter = QueryOptionRadioListAdapter(this, queryOptionsData[i].data)
                }
                "time_select" -> {
                    ll_query_option_time_select_input.visibility = View.VISIBLE
                }
                "start_and_end_time" -> {
                    ll_query_option_start_time_input.visibility = View.VISIBLE
                    ll_query_option_end_time_input.visibility = View.VISIBLE
                }
                else -> {
                }
            }
        }
    }

    /**
     * 初始化监听器
     */
    private fun initListener(queryOptionsData: MutableList<QueryOptions.DataBean>, mSubmitData: HashMap<String, String>) {
        //日历icon设置点击事件 弹出时间选择Dialog
        initCalenderListener()

        //单选列表item点击监听
        lv_query_option_radio_list.setOnItemClickListener { _, _, position, _ ->
            var queryOptionRadioListAdapter = lv_query_option_radio_list.adapter as QueryOptionRadioListAdapter
            queryOptionRadioListAdapter.mPos = position
            queryOptionRadioListAdapter.notifyDataSetChanged()
            ToastUtil.showToast(this, "mRadioListPos:::" + position)
        }

        //单选列表触摸事件监听，处理ScrollView嵌套ListView滑动冲突
        lv_query_option_radio_list.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                sv_query_options.requestDisallowInterceptTouchEvent(false)
            } else {
                sv_query_options.requestDisallowInterceptTouchEvent(true)
            }
            false
        }
        sv_query_options.smoothScrollTo(0, 20)

        //扫一扫
        ibtn_query_option_scan.setOnClickListener {
            startActivityForResult(Intent(this, QueryOptionsScannerActivity::class.java), mRequestCode)
        }

        //单选下拉框父项点击监听
        elv_query_option_radio_box.setOnGroupClickListener { parent, v, groupPosition, id ->
            var measuredHeight: Int
            if (elv_query_option_radio_box_switch) {
                measuredHeight = DisplayUtil.dip2px(this, 40f)
                iv_query_option_radio_box_arrow.setImageDrawable(resources.getDrawable(R.drawable.icon_down_arrow))
                elv_query_option_radio_box_switch = false
            } else {
                measuredHeight = DisplayUtil.dip2px(this, 40f + 4 * 35f)
                iv_query_option_radio_box_arrow.setImageDrawable(resources.getDrawable(R.drawable.icon_up_arrow))
                elv_query_option_radio_box_switch = true
            }
            rl_query_option_radio_box_drop_down_container.layoutParams.height = measuredHeight
            false
        }

        //单选下拉框子项item点击监听
        elv_query_option_radio_box.setOnChildClickListener { parent, _, _, childPosition, _ ->
            var adapter = parent.expandableListAdapter as ElvQueryOptionRadioBoxExpandableListAdapter
            adapter.mGroupData = listOf(adapter.mChildData!![childPosition])
            adapter.mClickedItemPos = childPosition
            adapter.notifyDataSetChanged()
            false
        }

        //多选下拉框父项点击监听
        elv_query_option_check_box.setOnGroupClickListener { parent, v, groupPosition, id ->
            var measuredHeight: Int
            if (elv_query_option_check_box_switch) {
                measuredHeight = DisplayUtil.dip2px(this, 40f)
                iv_query_option_check_box_arrow.setImageDrawable(resources.getDrawable(R.drawable.icon_down_arrow))
                elv_query_option_check_box_switch = false
            } else {
                measuredHeight = DisplayUtil.dip2px(this, 40f + 4 * 35f)
                iv_query_option_check_box_arrow.setImageDrawable(resources.getDrawable(R.drawable.icon_up_arrow))
                elv_query_option_check_box_switch = true
            }
            rl_query_option_check_box_drop_down_container.layoutParams.height = measuredHeight
            false
        }

        //多选下拉框子项item点击监听
        elv_query_option_check_box.setOnChildClickListener { parent, _, _, childPosition, _ ->
            var adapter = parent.expandableListAdapter as ElvQueryOptionRadioBoxExpandableListAdapter
            adapter.mGroupData = listOf(adapter.mGroupData!![0] + "," + adapter.mChildData!![childPosition])
            if (adapter.mCheckedItemsPos.contains(childPosition)) {
                adapter.mCheckedItemsPos.remove(childPosition)
            } else {
                adapter.mCheckedItemsPos.add(childPosition)
            }
            adapter.notifyDataSetChanged()
            false
        }

        // 提交的监听,获取各选项中的数据到StringBuilder中
        btn_query_option_submit.setOnClickListener(View.OnClickListener {
            for (i in queryOptionsData!!.indices) {
                when (queryOptionsData!![i].typeName) {
                    "text_input" -> {
                        if (et_query_option_text_input.text.toString() == null || et_query_option_text_input.text.toString().equals("")) {
                            ToastUtil.showToast(this, "请输入文本")
                        } else {
                            mSubmitData.put("text_input", et_query_option_text_input.text.toString())
                        }
                    }
                    "scan_input" -> {
                        if (et_query_option_scan_input.text.toString() == null || et_query_option_scan_input.text.toString().equals("")) {
                            ToastUtil.showToast(this, "请扫一扫输入")
                        } else {
                            mSubmitData.put("scan_input", et_query_option_scan_input.text.toString())
                        }
                    }
                    "radio_box" -> {
                        mSubmitData.put("radio_box", rg_query_option.checkedRadioButtonId.toString())
                    }
                    "check_box" -> {
                        val mCheckBoxBuilder: StringBuilder = StringBuilder()
                        for (s in queryOptionsData[i].data.indices) {
                            val checkbox = (ll_query_option_check_box_container.getChildAt(s)) as CheckBox
                            if (checkbox.isChecked) {
                                mCheckBoxBuilder.append(s)
                                mCheckBoxBuilder.append("-")
                            }
                        }
                        mCheckBoxBuilder.replace(queryOptionsData[i].data.size - 2, queryOptionsData[i].data.size - 1, "")
                        mSubmitData.put("check_box", mCheckBoxBuilder.toString())
                    }
                    "radio_box_spinner" -> {
                        mSubmitData.put("radio_box_spinner", (elv_query_option_radio_box.expandableListAdapter as ElvQueryOptionRadioBoxExpandableListAdapter)
                                .mGroupViewText.text.toString())
                    }
                    "check_box_spinner" -> {
                    }
                    "radio_list" -> {
                        mSubmitData.put("radio_list", mRadioListPos.toString())
                    }
                    "time_select" -> {
                        if (tv_query_option_time_select.text == null || tv_query_option_time_select.text.equals("")) {
                            ToastUtil.showToast(this, "请选择日期")
                        } else {
                            mSubmitData.put("time_select", tv_query_option_time_select.text.toString())
                        }
                    }
                    "start_and_end_time" -> {
                        if (tv_query_option_start_time.text == null || tv_query_option_start_time.text.equals("")) {
                            ToastUtil.showToast(this, "请选择起始日期")
                        } else if (tv_query_option_end_time.text == null || tv_query_option_end_time.text.equals("")) {
                            ToastUtil.showToast(this, "请选择结束日期")
                        } else {
                            query_option_start_time = getTimeMillis(tv_query_option_start_time.text.toString())
                            query_option_end_time = getTimeMillis(tv_query_option_end_time.text.toString())
                            Log.i(TAG, "query_option_start_time = $query_option_start_time")
                            Log.i(TAG, "query_option_end_time = $query_option_end_time")
                            if (query_option_start_time > query_option_end_time) {
                                ToastUtil.showToast(this, "起始或结束日期有误")
                            } else {
                                mSubmitData.put("start_time", tv_query_option_start_time.text.toString())
                                mSubmitData.put("end_time", tv_query_option_end_time.text.toString())
                            }
                        }
                    }
                    else -> {

                    }
                }
            }

        })

        //查询历史记录的点击监听
        ibtn_query_option_history.setOnClickListener {
            val queryResult: StringBuilder = StringBuilder()
            for ((key, value) in mSubmitData) {
                Log.i(TAG, "key = $key, value = $value")
                queryResult.append("key = $key, value = $value\n")
            }
            ToastUtil.showToast(this, String(queryResult))
        }
    }

    /**
     * 日历icon设置点击事件 弹出时间选择Dialog
     */
    private fun initCalenderListener() {
        tv_query_option_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_time_select)
        }
        ibtn_query_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_time_select)
        }

        tv_query_option_start_time.setOnClickListener {
            getDatePickerDialog(tv_query_option_start_time)

        }
        ibtn_query_start_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_start_time)

        }
        tv_query_option_end_time.setOnClickListener {
            getDatePickerDialog(tv_query_option_end_time)

        }
        ibtn_query_end_time_select.setOnClickListener {
            getDatePickerDialog(tv_query_option_end_time)
        }
    }

    /**
     * 获取日期选择器
     */
    private fun getDatePickerDialog(textView: TextView) {
        val calendar = Calendar.getInstance()
        DatePickerDialog(this, DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            calendar.set(year, monthOfYear, dayOfMonth)
            val df = SimpleDateFormat("yyyy/MM/dd")
            textView.text = df.format(calendar.time)
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    /**
     * 将 yyyy年MM月dd日 格式时间转为时间戳
     */
    private fun getTimeMillis(text: String?): Long {
        return SimpleDateFormat("yyyy/MM/dd").parse(text).time
    }

    /**
     * 扫一扫返回的逻辑
     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var barCode: String? = null
        when (resultCode) {
            QueryOptionsScannerActivity.QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_EAN_13 -> {
                var bundle = data!!.getBundleExtra("data")
                barCode = bundle.get("barcode_value") as String
                ToastUtil.showToast(this, "条形码:::" + barCode)
            }
            QueryOptionsScannerActivity.QUERY_OPTIONS_SCANNER_ACTIVITY_RESULTCODE_QR_CODE -> {
//                var bundle = data!!.getBundleExtra("data")
//                barCode = bundle.get("barcode_value") as String
                ToastUtil.showToast(this, "暂不支持二维码")
            }
        }
        et_query_option_scan_input.text.replace(0, et_query_option_scan_input.text.length, barCode)
    }

    companion object {

        private val TAG = "hjjzz"
    }
}
