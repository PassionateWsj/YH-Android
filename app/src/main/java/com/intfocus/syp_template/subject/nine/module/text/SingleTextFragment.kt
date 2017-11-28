package com.intfocus.syp_template.subject.nine.module.text

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.syp_template.R
import com.intfocus.syp_template.ui.BaseModuleFragment
import com.intfocus.syp_template.constant.Params
import com.intfocus.syp_template.constant.Params.ARG_PARAM
import kotlinx.android.synthetic.main.module_single_text.*

/**
 * @author liuruilin
 * @data 2017/11/2
 * @describe
 */
class SingleTextFragment : BaseModuleFragment(), TextModuleContract.View {
    private var rootView: View? = null
    private lateinit var datas: TextEntity
    private lateinit var param: String
    private lateinit var key: String
    override lateinit var presenter: TextModuleContract.Presenter

    companion object {
        fun newInstance(param: String?, key: String?): SingleTextFragment {
            val fragment = SingleTextFragment()
            val args = Bundle()
            args.putString(ARG_PARAM, param)
            args.putString(Params.KEY, key)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (arguments != null) {
            param = arguments!!.getString(ARG_PARAM)
            key = arguments!!.getString(Params.KEY)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.module_single_text, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.loadData(param)
        initView()
    }

    override fun onDestroy() {
        super.onDestroy()
        TextModelImpl.destroyInstance()
    }

    private fun initView() {
        et_single_text.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                datas.value = p0.toString()
                presenter.update(datas, key)
            }
        })
    }

    override fun initModule(entity: TextEntity) {
        datas = entity
        if (entity.element_type.trim().isEmpty()) iv_single_text_element.visibility = View.GONE else iv_single_text_element.setImageResource(getElementResourceId(entity.element_type))
        if (entity.title.trim().isEmpty()) tv_single_text_title.visibility = View.GONE else tv_single_text_title.text = entity.title
        if (entity.hint.trim().isEmpty()) et_single_text.hint = "" else et_single_text.hint = entity.hint
    }

    private fun getElementResourceId(element: String): Int {
        when(element) {
            "" -> return 0
        }
        return 0
    }
}
