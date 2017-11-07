package com.intfocus.hx.business.subject.template.one.banner;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.intfocus.hx.R;
import com.intfocus.hx.general.base.BaseModeFragment;
import com.intfocus.hx.business.subject.template.one.TemplateOneActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

/**
 * 模板一标题栏 负责显示标题、日期、帮助说明
 */
public class ModularOneUnitBannerModeFragment extends BaseModeFragment {
    private static final String ARG_PARAM = "param";
    private String mParam;

    private View rootView;

    @ViewInject(R.id.tv_mdrp_unit_banner_title)
    private TextView tv_title;

    @ViewInject(R.id.tv_mdrp_unit_banner_time)
    private TextView tv_time;

    @ViewInject(R.id.imgb_mdrp_unit_banner_info)
    private ImageButton imgb_info;
    private String info;
    private PopupWindow popupWindow;
    private TextView tv_name;
    private TextView tv_count;
    private ImageButton imgbtn_close;

    public ModularOneUnitBannerModeFragment() {
    }

    public static ModularOneUnitBannerModeFragment newInstance(String param) {
        ModularOneUnitBannerModeFragment fragment = new ModularOneUnitBannerModeFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM, param);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam = getArguments().getString(ARG_PARAM);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.fragment_banner, container, false);
            x.view().inject(this, rootView);
            initPopup(container);
            bindData();
        }
        return rootView;
    }

    private void initPopup(ViewGroup container) {
        View contentView = LayoutInflater.from(act).inflate(R.layout.item_bannerinfo, container,false);
        tv_name = contentView.findViewById(R.id.tv_name_bannerInfo);
        tv_count = contentView.findViewById(R.id.tv_count_bannerInfo);
        imgbtn_close = contentView.findViewById(R.id.imgBtn_ColsPopupWindow_bannerInfo);
        imgbtn_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (popupWindow != null) {
                    popupWindow.dismiss();
                }
            }
        });

        popupWindow = new PopupWindow(contentView);
        //设置PopupWindow的宽和高,必须设置,否则不显示内容(也可用PopupWindow的构造方法设置宽高)
        popupWindow.setWidth(LinearLayout.LayoutParams.MATCH_PARENT);
        popupWindow.setHeight(LinearLayout.LayoutParams.MATCH_PARENT);
        //当需要点击返回键,或者点击空白时,需要设置下面两句代码.
        //如果有背景，则会在contentView外面包一层PopupViewContainer之后作为mPopupView，如果没有背景，则直接用contentView作为mPopupView。
        //而这个PopupViewContainer是一个内部私有类，它继承了FrameLayout，在其中重写了Key和Touch事件的分发处理
        popupWindow.setFocusable(true);
        popupWindow.setOutsideTouchable(false);
        popupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        //设置PopupWindow进入和退出动画
        popupWindow.setAnimationStyle(R.style.anim_popup_bottombar);
    }

    private void bindData() {
        try {
            JSONObject jsonObject = new JSONObject(mParam);
            if (jsonObject.has("title")) {
                String name = jsonObject.getString("title");
                if (!name.isEmpty()) {
                    tv_title.setText(name);
                } else {
                    tv_title.setVisibility(View.GONE);
                }
            }

            if (jsonObject.has("date")) {
                String date = jsonObject.getString("date");
                if (date.length() > 0) {
                    tv_time.setText(date);
                }
            }

            if (jsonObject.has("info")) {
                this.info = jsonObject.getString("info");
                tv_count.setText(Html.fromHtml(info));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Event(R.id.imgb_mdrp_unit_banner_info)
    private void onViewClick(View view) {
        //设置PopupWindow显示的位置
        TemplateOneActivity activity = (TemplateOneActivity) getActivity();
        popupWindow.showAsDropDown(activity.actionbar);
    }
}
