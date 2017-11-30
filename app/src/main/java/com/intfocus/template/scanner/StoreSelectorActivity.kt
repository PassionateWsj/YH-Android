package com.intfocus.template.scanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.intfocus.template.R
import com.intfocus.template.constant.Params.STORE
import com.intfocus.template.constant.Params.STORE_ID
import com.intfocus.template.scanner.adapter.SelectorListAdapter
import com.intfocus.template.scanner.presenter.StoreSelectorPresenter
import com.intfocus.template.scanner.view.StoreSelectorView
import com.intfocus.template.ui.BaseActivity
import com.intfocus.template.model.response.scanner.StoreItem
import kotlinx.android.synthetic.main.activity_store_selector.*

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/19 下午5:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class StoreSelectorActivity : BaseActivity(), StoreSelectorView {
    companion object {
        val RESULT_CODE_CHOOSE = 2
    }

    lateinit var presenter: StoreSelectorPresenter
    lateinit var adapter: SelectorListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_selector)

        initAdapter()
        initData()
        initListener()
    }

    private fun initAdapter() {
        store_item_select.text = intent.getStringExtra(STORE)
        adapter = SelectorListAdapter(this)
        listStores.adapter = adapter
    }


    private fun initData() {
        presenter = StoreSelectorPresenter(this, this)
        presenter.loadData()
    }

    private fun initListener() {
        // 隐藏软键盘
        ll_store_selector_container.setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }
        //　item 点击监听
        listStores.setOnItemClickListener { _, _, position, _ ->
            val intent = Intent()
            intent.putExtra(STORE, adapter.getSelectItem(position).name)
            intent.putExtra(STORE_ID, adapter.getSelectItem(position).id)
            setResult(RESULT_CODE_CHOOSE, intent)
            finish()
        }
        // 文本输入框内容监听
        storeSearchView.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                presenter.loadData(p0.toString())
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
        })
        // 根据搜索内容 请求数据
        tv_store_selector_search.setOnClickListener {
            hideKeyboard()
            presenter.loadData(storeSearchView.text.toString())
        }
        // 上次选择内容 点击监听
        store_item_select.setOnClickListener { finish() }
    }

    /**
     * 获取数据成功
     */
    override fun onResultSuccess(data: MutableList<StoreItem>?) {
        adapter.setData(data)
    }

    /**
     * 获取数据失败
     */
    override fun onResultFailure(e: Throwable?) {

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
