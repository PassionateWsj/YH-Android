package com.intfocus.template.subject.seven.attention

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.model.response.attention.AttentionItem
import com.intfocus.template.scanner.BarCodeScannerActivity
import com.intfocus.template.subject.seven.listener.AttentionListItemClickListener
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.util.ToastUtils
import kotlinx.android.synthetic.main.activity_attention_list.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:14
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class AttentionActivity : BaseActivity(), AttentionContract.View, AttentionListItemClickListener {

    companion object {
        val REQUEST_CODE = 2
    }

    override lateinit var presenter: AttentionContract.Presenter
    private lateinit var mItemAdapter: AttentionListItemAdapter
    var loadConcernedData: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_attention_list)
        presenter = AttentionPresenter(AttentionModelImpl.getInstance(), this)

        initAdapter()
        initData()
        initListener()
    }

    private fun initAdapter() {
        mItemAdapter = AttentionListItemAdapter(this, this)
        lv_attention_list_item.adapter = mItemAdapter
    }


    private fun initData() {
        presenter.loadData(loadConcernedData)
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
                presenter.loadData(p0.toString(), loadConcernedData)
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

    override fun onResultSuccess(data: List<AttentionItem>) {
        mItemAdapter.setData(data)
    }

    override fun itemClick(pos: Int) {
        val attentionItem = mItemAdapter.getSelectItem(pos)
        presenter.concernOrCancelConcern(attentionItem.attention_item_id, attentionItem.attention_item_name)
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
                presenter.loadData(ed_attention_list_search.text.toString(), loadConcernedData)
            }
            R.id.tv_attention_list_attention_btn -> {
                changeStyle(tv_attention_list_attention_btn, tv_attention_list_attentioned_btn)
//                rl_attention_list_input_container.visibility = View.VISIBLE
                loadConcernedData = false
                presenter.loadData(ed_attention_list_search.text.toString(), loadConcernedData)
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
        hideKeyboard()
        finish()
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