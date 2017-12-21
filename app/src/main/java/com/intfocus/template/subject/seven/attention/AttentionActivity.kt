package com.intfocus.template.subject.seven.attention

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.intfocus.template.R
import com.intfocus.template.constant.ToastColor
import com.intfocus.template.model.response.attention.AttentionItem
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
        val RESULT_CODE_CHOOSE = 2
    }

    override lateinit var presenter: AttentionContract.Presenter
    lateinit var mItemAdapter: AttentionListItemAdapter

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
        presenter.loadAllData()
    }

    private fun initListener() {
        // 隐藏软键盘
        ll_store_selector_container.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        //　item 点击监听
        lv_attention_list_item.setOnItemClickListener { _, _, position, _ ->
//            val intent = Intent()
//            intent.putExtra(STORE, mItemAdapter.getSelectItem(position).name)
//            intent.putExtra(STORE_ID, mItemAdapter.getSelectItem(position).id)
//            setResult(RESULT_CODE_CHOOSE, intent)
            finish()
        }
        // 文本输入框内容监听
        ed_attention_list_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.loadData(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        // 根据搜索内容 请求数据
        iv_attention_list_scan.setOnClickListener {
            hideKeyboard()
            presenter.loadData(ed_attention_list_search.text.toString())
        }
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
    }

    override fun concernOrCancelConcernResult(isConcernSuccess: Boolean) {
        if (isConcernSuccess) {
            ToastUtils.show(this, "关注成功", ToastColor.SUCCESS)
        } else {
            ToastUtils.show(this, "取消关注成功")
        }
    }

    fun onCLick(view: View) {

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
}