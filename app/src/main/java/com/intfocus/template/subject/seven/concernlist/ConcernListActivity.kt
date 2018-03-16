package com.intfocus.template.subject.seven.concernlist

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.blankj.utilcode.util.BarUtils
import com.intfocus.template.ConfigConstants
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.Params.REPORT_ID
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.scanner.BarCodeScannerActivity
import com.intfocus.template.subject.seven.bean.ConcernListBean
import com.intfocus.template.subject.seven.listener.ConcernListItemClickListener
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.activity_attention_list.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:14
 * e-mail: PassionateWsj@outlook.com
 * name: 关注列表
 * desc: 关注/取消关注功能
 * ****************************************************
 */
class ConcernListActivity : BaseActivity(), ConcernListContract.View, ConcernListItemClickListener {

    companion object {
        val REQUEST_CODE = 2
        val RESPONSE_CODE = 201
    }

    override lateinit var presenter: ConcernListContract.Presenter
    private lateinit var mItemAdapter: ConcernListItemAdapter
    var loadConcernedData: Boolean = false
    var reportId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention_list)
        presenter = ConcernListPresenter(ConcernListModelImpl.getInstance(), this)

        initShow()
        initAdapter()
        initData()
        initListener()
    }

    private fun initShow() {
        if (Build.VERSION.SDK_INT >= 21 && ConfigConstants.ENABLE_FULL_SCREEN_UI) {
            action_bar.post {
                BarUtils.addMarginTopEqualStatusBarHeight(action_bar)
            }
        }
    }

    private fun initAdapter() {
        mItemAdapter = ConcernListItemAdapter(this, this)
        lv_attention_list_item.adapter = mItemAdapter
    }


    private fun initData() {
        intent.extras.getString(REPORT_ID)?.let {
            reportId = it
            presenter.loadData(loadConcernedData, it)
        }
    }

    private fun initListener() {
        // 隐藏软键盘
        rl_attention_list_layout.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        //　item 点击监听
        lv_attention_list_item.setOnItemClickListener { _, _, _, _ ->
            hideKeyboard()
        }
        // 文本输入框内容监听
        ed_attention_list_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.loadData(p0.toString(), loadConcernedData, reportId)
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
    }

    override fun onResultFailure(e: Throwable) {
        mItemAdapter.clearData()
    }

    override fun onResultSuccess(data: List<ConcernListBean>) {
        mItemAdapter.setData(data)
    }

    override fun itemClick(pos: Int) {
        val attentionItem = mItemAdapter.getSelectItem(pos)
        presenter.concernOrCancelConcern(attentionItem.obj_num, attentionItem.obj_name, reportId)
        hideKeyboard()
    }

    override fun concernOrCancelConcernResult(isConcernSuccess: Boolean) {
        if (isConcernSuccess) {
            ToastUtils.show(this, "关注成功", ToastColor.SUCCESS)
        } else {
            ToastUtils.show(this, "取消关注成功")
        }
    }

    fun onClick(view: View) {
        hideKeyboard()
        when (view.id) {
            R.id.tv_attention_list_attentioned_btn -> {
                changeStyle(tv_attention_list_attentioned_btn, tv_attention_list_attention_btn)
//                rl_attention_list_input_container.visibility = View.GONE
                loadConcernedData = true
                presenter.loadData(ed_attention_list_search.text.toString(), loadConcernedData, reportId)
            }
            R.id.tv_attention_list_attention_btn -> {
                changeStyle(tv_attention_list_attention_btn, tv_attention_list_attentioned_btn)
//                rl_attention_list_input_container.visibility = View.VISIBLE
                loadConcernedData = false
                presenter.loadData(ed_attention_list_search.text.toString(), loadConcernedData, reportId)
            }
            R.id.iv_attention_list_scan -> {
                intent = Intent(this, BarCodeScannerActivity::class.java)
                Intent.FLAG_ACTIVITY_SINGLE_TOP
                intent.putExtra(BarCodeScannerActivity.INTENT_FOR_RESULT, true)
                startActivityForResult(intent, REQUEST_CODE)
            }
        }
    }

    private fun changeStyle(currentTextView: TextView, standbyTextView: TextView) {
        currentTextView.setTextColor(ContextCompat.getColor(this, R.color.co10_syr))
        currentTextView.background = ContextCompat.getDrawable(this, R.drawable.bg_radius_fill_blue)
        standbyTextView.setTextColor(ContextCompat.getColor(this, R.color.co3_syr))
        standbyTextView.background = ContextCompat.getDrawable(this, R.drawable.bg_radius_sold)
    }

    /**
     * 返回监听
     */
    fun backPress(v: View) {
        finishAct()
        finish()
    }

    private fun finishAct() {
        hideKeyboard()
        setResult(RESPONSE_CODE)
    }

    override fun onBackPressed() {
        finishAct()
        super.onBackPressed()
    }
    /**
     * 隐藏软件盘
     */
    fun hideKeyboard() {
        val imm = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(this.currentFocus
                .windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == BarCodeScannerActivity.RESULT_CODE_SCAN) {
            data?.getStringExtra(Params.CODE_INFO)?.let {
                ed_attention_list_search.setText(it.toCharArray(), 0, it.length)
            }
        }
    }
}