package com.intfocus.template.subject.nine.module.options

import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.ui.BaseModuleFragment
import kotlinx.android.synthetic.main.module_drop_options.*

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class DropOptionsFragment : BaseModuleFragment(), OptionsModuleContract.View, SingleOptionsAdapter.OptionsSelectedListener {
    override lateinit var presenter: OptionsModuleContract.Presenter
    private lateinit var adapter: SingleOptionsAdapter
    private lateinit var datas: OptionsEntity
    private var param: String? = null
    private var key: String? = null
    private var listItemType: Int = 0
    private lateinit var optionsDialog: AlertDialog
    private var rootView: View? = null

    companion object {

        private val LIST_ITEM_TYPE = "list_item_type"

        fun newInstance(param: String?, key: String?, listItemType: Int): DropOptionsFragment {
            val fragment = DropOptionsFragment()
            val args = Bundle()
            args.putString(Params.ARG_PARAM, param)
            args.putString(Params.KEY, key)
            args.putInt(LIST_ITEM_TYPE, listItemType)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            param = arguments!!.getString(Params.ARG_PARAM)
            key = arguments!!.getString(Params.KEY)

            listItemType = arguments!!.getInt(LIST_ITEM_TYPE)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.module_drop_options, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        param?.let {
            presenter.loadData(it)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        OptionsModelImpl.destroyInstance()
    }

    override fun initModule(entity: OptionsEntity) {
        datas = entity
        if (entity.title.isEmpty()) tv_drop_options_title.visibility = View.GONE else tv_drop_options_title.text = entity.title
        if (entity.value.isEmpty()) tv_drop_options_value.text = "" else tv_drop_options_value.text = entity.value
        adapter = SingleOptionsAdapter(activity!!, this)
        adapter.setData(entity.options!!)
        rl_options_select.setOnClickListener { showDialog(entity.title) }
    }

    private fun showDialog(title: String) {
        optionsDialog = AlertDialog.Builder(activity!!, R.style.CommonDialog).setTitle(title).create()
        optionsDialog.show()
        val view = LayoutInflater.from(activity).inflate(R.layout.dialog_single_options, null)
        val tvCancel = view.findViewById<TextView>(R.id.tv_cancel)
        val tvTitle = view.findViewById<TextView>(R.id.tv_title)
        val listView = view.findViewById<ListView>(R.id.lv_single_options_list)
        tvTitle.text = title
        listView.adapter = adapter
        optionsDialog.setContentView(view)
        tvCancel.setOnClickListener { optionsDialog.dismiss() }
    }

    override fun onItemSelected(value: String) {
        datas.value = value
        tv_drop_options_value.text = value
        key?.let {
            presenter.update(datas, it, listItemType)
        }
        optionsDialog.dismiss()
    }
}
