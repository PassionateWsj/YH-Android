package com.intfocus.template.dashboard.feedback.content

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.PopupWindow
import com.bumptech.glide.Glide
import com.intfocus.template.R
import com.intfocus.template.dashboard.feedback.FeedbackModelImpl
import com.intfocus.template.model.response.mine_page.FeedbackContent
import com.intfocus.template.ui.BaseFragment
import kotlinx.android.synthetic.main.fragment_feedback_content.*

/**
 * @author liuruilin
 * @data 2017/12/6
 * @describe
 */
class FeedbackContentFragment: BaseFragment(), FeedbackContentContract.View, FeedbackContentAdapter.OnImageClickListener {
    override lateinit var presenter: FeedbackContentContract.Presenter
    private var rootView: View? = null
    private var adapter: FeedbackContentAdapter? = null
    private var contentId = 0

    companion object {
        fun getInstance(id: Int): FeedbackContentFragment {
            var fragment = FeedbackContentFragment()
            var args = Bundle()
            args.putInt("id", id)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_feedback_content, container, false)
        }

        if (null != arguments) {
            contentId = arguments!!.getInt("id")
        }
        showDialog(this.context!!)
        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()

        if (this::presenter.isInitialized) {
            presenter.getContent(contentId)
        }
        else {
            presenter = FeedbackContentPresenter(FeedbackModelImpl.getInstance(), this)
            presenter.getContent(contentId)
        }
    }

    fun initView() {
        val mLayoutManager = LinearLayoutManager(this.context)
        mLayoutManager.orientation = LinearLayoutManager.HORIZONTAL
        rv_feedback_images.layoutManager = mLayoutManager
        adapter = FeedbackContentAdapter(this.context, this)
        rv_feedback_images.adapter = adapter
    }

    override fun showContent(data: FeedbackContent) {
        hideLoading()
        tv_feedback_idea.text = data.data!!.content
        if (data.data!!.replies!!.isNotEmpty()) {
            iv_state_3.setImageResource(R.drawable.state_feedback_true)
            tv_feedback_result.text = data.data!!.replies!![0].content
        }
        adapter!!.setData(data.data!!.images)
    }

    override fun onImageClick(link: String) {
        val contentView = LayoutInflater.from(this.context).inflate(R.layout.popup_image, null)
        val ivBigImage = contentView.findViewById<ImageView>(R.id.iv_popup_image)

        //设置弹出框的宽度和高度
        val popupWindow = PopupWindow(contentView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT)
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
        popupWindow.showAtLocation(this.view, Gravity.BOTTOM, 0, contentView.height)

        contentView.setOnClickListener {
            if (popupWindow.isShowing) {
                popupWindow.dismiss()
            }
        }
        Glide.with(this.context)
                .load(link)
                .placeholder(R.drawable.anim_loading_view)
                .into(ivBigImage)
    }

    override fun showNullPage() {

    }
}
