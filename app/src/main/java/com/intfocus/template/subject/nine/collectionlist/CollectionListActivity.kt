package com.intfocus.template.subject.nine.collectionlist

import android.os.Bundle
import com.intfocus.template.R
import com.intfocus.template.model.entity.Collection
import com.intfocus.template.subject.nine.collectionlist.adapter.CollectionListAdapter
import com.intfocus.template.ui.BaseActivity
import kotlinx.android.synthetic.main.activity_collection_list.*


class CollectionListActivity : BaseActivity(), CollectionListContract.View {

    override lateinit var presenter: CollectionListContract.Presenter
    lateinit var mAdapter: CollectionListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_list)

        initView()
        initData()
        initListener()
    }

    private fun initListener() {

    }

    private fun initData() {
        presenter.loadData(this)
    }

    private fun initView() {
        initAdapter()
        CollectionListPresenter(CollectionListModelImpl.getInstance(), this)
    }

    private fun initAdapter() {
        mAdapter = CollectionListAdapter(this)
        rv_collection_list.adapter = mAdapter
    }

    override fun updateData(dataList: List<Collection>) {
        mAdapter.setData(dataList)
    }
}
