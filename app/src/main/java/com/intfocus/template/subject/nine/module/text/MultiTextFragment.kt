package com.intfocus.template.subject.nine.module.text

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.constant.Params
import com.intfocus.template.ui.BaseModuleFragment
import kotlinx.android.synthetic.main.module_single_text.*

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class MultiTextFragment : BaseModuleFragment(), TextModuleContract.View {
    private var rootView: View? = null
    private lateinit var datas: TextEntity
    private var param: String? = null
    private var key: String? = null
    private var listItemType: Int = 0
    override lateinit var presenter: TextModuleContract.Presenter

    companion object {

        private val LIST_ITEM_TYPE = "list_item_type"

        fun newInstance(param: String?, key: String?, listItemType: Int): MultiTextFragment {
            val fragment = MultiTextFragment()
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
            rootView = inflater.inflate(R.layout.module_multi_text, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        param?.let {
            presenter.loadData(it)
        }
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        TextModelImpl.destroyInstance()
    }

    private fun initView() {
        et_single_text.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                datas.value = p0.toString()
                key?.let {
                    presenter.update(datas, it,listItemType)
                }
            }
        })
    }

    override fun initModule(entity: TextEntity) {
        datas = entity
        if (entity.title.trim().isEmpty()) tv_single_text_title.visibility = View.GONE else tv_single_text_title.text = entity.title
        if (entity.hint.trim().isEmpty()) et_single_text.hint = "" else et_single_text.hint = entity.hint
    }
}
