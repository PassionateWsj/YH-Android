package com.intfocus.template.subject.nine.module.image

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.intfocus.template.R
import com.intfocus.template.SYPApplication
import com.intfocus.template.constant.Params
import com.intfocus.template.constant.Params.REQUEST_CODE_CHOOSE
import com.intfocus.template.dashboard.feedback.FeedbackInputActivity
import com.intfocus.template.ui.BaseModuleFragment
import com.intfocus.template.util.GifSizeFilter
import com.intfocus.template.util.ImageUtil
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.module_image.*

/**
 * @author liuruilin
 * @data 2017/11/3
 * @describe
 */
class ImageFragment : BaseModuleFragment(), ImageModuleContract.View, ImageDisplayAdapter.ImageItemClickListener {
    override lateinit var presenter: ImageModuleContract.Presenter
    private var param: String? = null
    private var key: String? = null
    private var listItemType: Int = 0
    var rootView: View? = null
    lateinit var datas: ImageEntity
    lateinit var adapter: ImageDisplayAdapter
    private var mSelected: List<Uri>? = null

    companion object {

        private val LIST_ITEM_TYPE = "list_item_type"

        fun newInstance(param: String?, key: String?, listItemType: Int): ImageFragment {
            val fragment = ImageFragment()
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
            rootView = inflater.inflate(R.layout.module_image, container, false)
        }
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        param?.let { presenter.loadData(it) }
    }

    override fun onDestroy() {
        super.onDestroy()
        ImageModelImpl.destroyInstance()
    }

    private fun initView() {
        val linearLayoutManager = LinearLayoutManager(activity)
        linearLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_image_display.layoutManager = linearLayoutManager
    }

    override fun initModule(entity: ImageEntity) {
        datas = entity
        adapter = ImageDisplayAdapter(activity!!, this, entity.limit)
        tv_image_title.text = entity.title
        rv_image_display.adapter = adapter
    }

    override fun addImage(maxNum: Int) {
        Matisse.from(this)
                .choose(MimeType.allOf())
                .countable(true)
                .maxSelectable(maxNum)
                .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(
                        resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(GlideEngine())
                .forResult(FeedbackInputActivity.REQUEST_CODE_CHOOSE)
    }

    override fun deleteImage(pos: Int) {
        adapter.deleteImageWithPos(pos)
        updateImgData(adapter.mData)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data)
            adapter.setData(mSelected)
            updateImgData(mSelected)
        }
    }

    private fun updateImgData(data: List<Uri>?) {
        val imagePathList: MutableList<String> = arrayListOf()
        data?.mapTo(imagePathList) { ImageUtil.handleImageOnKitKat(it, SYPApplication.globalContext) }
        datas.value = imagePathList
        key?.let {
            presenter.update(datas, it, listItemType)
        }
    }
}
