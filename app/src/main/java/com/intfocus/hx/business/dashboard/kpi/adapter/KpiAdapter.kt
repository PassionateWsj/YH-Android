package com.intfocus.hx.business.dashboard.mine.adapter

import android.content.Context
import android.graphics.Typeface
import android.support.v4.view.ViewPager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.LinearLayout
import android.widget.TextView
import com.intfocus.hx.R
import com.intfocus.hx.business.dashboard.kpi.adapter.MyViewPagerAdapter
import com.intfocus.hx.business.dashboard.kpi.bean.KpiBean
import com.intfocus.hx.business.dashboard.kpi.bean.NoticeBoardRequest
import com.intfocus.hx.business.dashboard.mine.bean.InstituteDataBean
import com.intfocus.hx.general.bean.DashboardItemBean
import com.intfocus.hx.general.view.AutoScrollViewPager
import com.yonghui.homemetrics.utils.Utils
import org.greenrobot.eventbus.EventBus
import java.util.*

/**
 * Created by CANC on 2017/6/12.
 */
class KpiAdapter(val context: Context,
                 private var data: List<KpiBean>?,
                 var listener: HomePageListener) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    //当前显示的公告位置
    var i = 0
   private val UNKNOWN: Int = -1   //未知样式
   private val VIEW_PAGER: Int = UNKNOWN + 1//轮播图
   private val TEXT_SWITCHER: Int = VIEW_PAGER + 1//跳动文字
   private val OPERATIONAL_WARNING: Int = TEXT_SWITCHER + 1//经营预警
   private val BUSINESS_OVERVIEW: Int = OPERATIONAL_WARNING + 1//生意概况
   private val HOME_BOTTOM: Int = BUSINESS_OVERVIEW + 1// Bottom

    var inflater = LayoutInflater.from(context)
    fun setData(data: List<KpiBean>?) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = getMyItemViewType(data!![position].index!!)

    private fun getMyItemViewType(itemType: Int): Int = when {
        VIEW_PAGER == itemType -> VIEW_PAGER
        TEXT_SWITCHER == itemType -> TEXT_SWITCHER
        BUSINESS_OVERVIEW == itemType -> BUSINESS_OVERVIEW
        OPERATIONAL_WARNING == itemType -> OPERATIONAL_WARNING
        HOME_BOTTOM == itemType -> HOME_BOTTOM
        else -> UNKNOWN
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val contentView: View
        val viewHolder: RecyclerView.ViewHolder
        when (viewType) {
            VIEW_PAGER -> {
                contentView = inflater.inflate(R.layout.item_kpi_viewpager, parent, false)
                viewHolder = ViewPagerHolder(contentView)
            }
            TEXT_SWITCHER -> {
                contentView = inflater.inflate(R.layout.item_kpi_text_switcher, parent, false)
                viewHolder = TextSwitcherHolder(contentView)
            }
            OPERATIONAL_WARNING -> {
                contentView = inflater.inflate(R.layout.item_kpi_operational_waring_recyclerview, parent, false)
                viewHolder = OperationalWarningHolder(contentView)
            }
            BUSINESS_OVERVIEW -> {
                contentView = inflater.inflate(R.layout.item_kpi_business_overview_recycler_view, parent, false)
                viewHolder = BusinessOverviewHolder(contentView)
            }
            HOME_BOTTOM -> {
                contentView = inflater.inflate(R.layout.item_kpi_bottom, parent, false)
                viewHolder = HomeBottomHolder(contentView)
            }
            else -> {
                contentView = inflater.inflate(R.layout.item_kpi_unknow, parent, false)
                viewHolder = UNKnowHolder(contentView)
            }
        }
        return viewHolder
    }

    override fun getItemCount(): Int = if (data == null) 0 else data!!.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val homeData = data!![position]
        when (holder) {
            is ViewPagerHolder -> {
                if (homeData.data!!.isNotEmpty()) {
                    val views: MutableList<View>? = ArrayList()
                    views!!.clear()
                    val mTypeface = Typeface.createFromAsset(context.assets, "ALTGOT2N.TTF")
                    for (themeItem in homeData.data!!.iterator()) {
                        val contentView = View.inflate(context, R.layout.fragment_number_one, null)
                        val tvNumberOneNumber = contentView.findViewById<TextView>(R.id.tv_number_one_number)
                        val tvNumberOneTitle = contentView.findViewById<TextView>(R.id.tv_number_one_title)
                        val tvNumberOneUnit = contentView.findViewById<TextView>(R.id.tv_number_one_unit)
                        val tvNumberOneSubTitle = contentView.findViewById<TextView>(R.id.tv_number_one_sub_title)
                        val tvNumberOneSub = contentView.findViewById<TextView>(R.id.tv_number_one_sub)
                        val rlKpiNumberOne = contentView.findViewById<LinearLayout>(R.id.rl_kpi_number_one)

                        tvNumberOneTitle.text = themeItem.memo2
                        val number = themeItem.data!!.high_light!!.number
                        tvNumberOneNumber.text = formatNumber(number)
                        tvNumberOneNumber.typeface = mTypeface
                        tvNumberOneUnit.text = "(" + themeItem.unit + ")"
                        tvNumberOneSubTitle.text = themeItem.memo1
                        tvNumberOneSub.text = themeItem.data!!.high_light!!.compare
                        rlKpiNumberOne.setOnClickListener {
                            EventBus.getDefault().post(DashboardItemBean(themeItem.obj_link!!, themeItem.obj_title!!,
                                    themeItem.obj_id!!, themeItem.template_id!!, "1"))
                        }
                        views.add(contentView)
                    }
                    val myViewPagerAdapter = MyViewPagerAdapter(views, context)
                    holder.viewPager.currentItem = views.size - Integer.MAX_VALUE / 2 % views.size + Integer.MAX_VALUE / 2
                    holder.viewPager.adapter = myViewPagerAdapter
                    holder.layoutDot.removeAllViews()
                    if (views.size > 0) {
                        holder.viewPager.startAutoScroll()
                        for (i in views.indices) {
                            val point = View(context)
                            point.setBackgroundResource(R.drawable.point_background)
                            val size = Utils.dipDimensionInteger(context, 8.0f)
                            val param = LinearLayout.LayoutParams(size, size)
                            param.rightMargin = size / 2
                            point.layoutParams = param
                            point.isEnabled = i == 0
                            holder.layoutDot.addView(point)
                            //图片数量少于等于1，隐藏圆点
                            if (views.size <= 1) {
                                point.visibility = View.GONE
                            } else {
                                point.visibility = View.VISIBLE
                            }
                        }
                    } else {
                        holder.layoutDot.removeAllViews()
                        holder.viewPager.stopAutoScroll()
                    }
                    holder.viewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                        override fun onPageScrolled(i: Int, v: Float, i1: Int) {

                        }

                        override fun onPageSelected(i: Int) {
                            val realPosition = myViewPagerAdapter.getRealPosition(i)
                            for (index in views.indices) {
                                holder.layoutDot.getChildAt(index).isEnabled = index != realPosition
                            }
                        }

                        override fun onPageScrollStateChanged(i: Int) {

                        }
                    })
                }
            }
            is TextSwitcherHolder -> {
                holder.tvNotice.text = ""
                val itemsTLAP1 = homeData.data
                if (itemsTLAP1 != null && itemsTLAP1.isNotEmpty()) {
                    val translateAnimation = TranslateAnimation(0.0f, 0.0f, 0.0f, 0.0f)
                    translateAnimation.duration = 3000
                    translateAnimation.repeatCount = 10000
                    translateAnimation.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {
                            if (i < itemsTLAP1.size) {
                                holder.tvNotice.text = itemsTLAP1[i].title
                            } else {
                                i = 0
                                holder.tvNotice.text = itemsTLAP1[i].title
                            }
                            i++
                        }

                        override fun onAnimationEnd(animation: Animation) {}

                        override fun onAnimationRepeat(animation: Animation) {
                            if (i < itemsTLAP1.size) {
                                holder.tvNotice.text = itemsTLAP1[i].title
                            } else {
                                i = 0
                                holder.tvNotice.text = itemsTLAP1[i].title
                            }
                            i++
                        }
                    })
                    holder.tvNotice.startAnimation(translateAnimation)
                    holder.tvNotice.setOnClickListener {
                        EventBus.getDefault().post(NoticeBoardRequest(true))
                    }
                }
            }
            is OperationalWarningHolder -> {
                val operationalWarningAdapter = OperationalWarningAdapter(context, homeData.data)
                holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                holder.recyclerView.adapter = operationalWarningAdapter
            }
            is BusinessOverviewTitleHolder -> {
            }
            is BusinessOverviewHolder -> {
                val businessOverViewAdapter = BusinessOverViewAdapter(context, homeData.data)
                holder.recyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                holder.recyclerView.adapter = businessOverViewAdapter
            }
            is UNKnowHolder -> {
            }
        }
    }

    /**
     * 轮播图
     */
    class ViewPagerHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var viewPager: AutoScrollViewPager = itemView.findViewById(R.id.vp_icons)
        var layoutDot: LinearLayout = itemView.findViewById(R.id.layout_dot)
    }

    /**
     * 跳动文字
     */
    class TextSwitcherHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvNotice: TextView = itemView.findViewById(R.id.tv_notice)

    }

    /**
     *经营预警Title
     */
    class OperationalWarningTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     *经营预警
     */
    class OperationalWarningHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }

    /**
     *生意概况TItle
     */
    class BusinessOverviewTitleHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    /**
     *生意概况
     */
    class BusinessOverviewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var recyclerView: RecyclerView = itemView.findViewById(R.id.recycler_view)
    }

    class HomeBottomHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
    class UNKnowHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    interface HomePageListener {
        fun itemClick(instituteDataBean: InstituteDataBean)
    }

    fun formatNumber(number: String): String {
        var mNumber = number
        if (mNumber.contains("")) {
            mNumber = mNumber.replace("0+?$".toRegex(), "")//去掉多余的0
            mNumber = mNumber.replace("[.]$".toRegex(), "")//如最后一位是.则去掉
        }
        return mNumber
    }
}
