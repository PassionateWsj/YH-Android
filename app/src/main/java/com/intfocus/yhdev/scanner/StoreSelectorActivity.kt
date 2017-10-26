package com.intfocus.yhdev.scanner

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.intfocus.yhdev.R
import com.intfocus.yhdev.base.BaseActivity
import com.intfocus.yhdev.data.response.scanner.StoreItem
import com.intfocus.yhdev.scanner.adapter.SelectorListAdapter
import com.intfocus.yhdev.scanner.presenter.StoreSelectorPresenter
import com.intfocus.yhdev.scanner.view.StoreSelectorView
import com.intfocus.yhdev.util.URLs
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
        store_item_select.text = intent.getStringExtra(URLs.kStore)
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
            intent.putExtra(URLs.kStore, adapter.getSelectItem(position).name)
            intent.putExtra(URLs.kStoreIds, adapter.getSelectItem(position).id)
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
