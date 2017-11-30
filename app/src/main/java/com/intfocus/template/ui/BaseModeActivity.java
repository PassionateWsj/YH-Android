package com.intfocus.template.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.intfocus.template.R;
import com.intfocus.template.subject.one.ModeImpl;
import com.intfocus.template.util.LoadingUtils;
import com.intfocus.template.util.PageLinkManage;
import com.zbl.lib.baseframe.core.ActManager;

import org.xutils.x;

/**
 * 基类界面
 * @author liuruilin
 */
public abstract class BaseModeActivity extends AppCompatActivity {
    protected Context ctx;

    /**
     * 布局根View
     */
    protected FrameLayout rootView;

    /**
     * Header的根布局
     */
    protected ViewStub stubHeader;

    /**
     * 左边的TextView
     */
    protected TextView tvLeft;
    protected TextView tvTitle;
    protected LinearLayout topRightLayout;

    protected PopupWindow popupWindow;

    /**
     * 容器
     */
    protected FrameLayout flContainer;
    //    protected ProgressBar progressBar;
    private Dialog loadingDialog;

    public View mContentView;
    //TODO =====================================界面处理=====================================================

    public int setLayoutRes() {
        return 0;
    }

    public boolean isFollowTheme() {
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().getDecorView();
        ctx = this.getApplicationContext();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.frame_baseui);

        stubHeader = findViewById(R.id.ll_baseUI_title);
        rootView = findViewById(R.id.rootView);
        int contentID = setLayoutRes();
        if (contentID != 0 && contentID != -1) {
            mContentView = LayoutInflater.from(this).inflate(contentID, null);
        }
        flContainer = findViewById(R.id.fl_baseUI_container);
        if (mContentView != null) {
            flContainer.addView(mContentView);
        }

        onCreateFinish(savedInstanceState);
    }

    public void onCreateFinish(Bundle savedInstanceState) {
    }

    /**
     * 基类View的点击事件
     */
    private BaseUIonClick viewListener = new BaseUIonClick();

    /**
     * 实例化标题栏
     */
    public void initHeader() {
        stubHeader.inflate();
        tvLeft = findViewById(R.id.tv_baseUI_back);
        tvLeft.setOnClickListener(viewListener);
        tvTitle = findViewById(R.id.tv_baseUI_title);
        topRightLayout = findViewById(R.id.ll_baseUI_title_RightView);
        topRightLayout.setOnClickListener(viewListener);
    }

    class BaseUIonClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.tv_baseUI_back:
                    onBack();
                    break;

                case R.id.ll_baseUI_title_RightView:
                    showComplaintsPopWindow(v);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * 返回键回掉方法
     */
    protected void onBack() {
        PageLinkManage.INSTANCE.pageBackIntent(BaseModeActivity.this);
        ModeImpl.destroyInstance();
        ActManager.getActManager().finishActivity();
    }

    @Override
    public void onBackPressed() {
        onBack();
    }

    /**
     * 显示菜单
     *
     * @param clickView
     */
    void showComplaintsPopWindow(View clickView) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.pop_menu_v2, null);
        x.view().inject(this, contentView);
        //设置弹出框的宽度和高度
        popupWindow = new PopupWindow(contentView,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        // 取得焦点
        popupWindow.setFocusable(true);
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(new BitmapDrawable());
        //点击外部消失
        popupWindow.setOutsideTouchable(true);
        //设置可以点击
        popupWindow.setTouchable(true);
        popupWindow.showAsDropDown(clickView);
    }

    /**
     * 设置标题栏右边TextView的文字
     *
     * @param src
     */
    public void setACTitle(String src) {
        tvTitle.setText(src);
    }

    //TODO -------------------设置返回键状态------------------------------

    /**
     * 设置标题栏右边TextView的图片
     *
     * @param drawable
     */
    public void setLeftTVSrc(int drawable) {
        Drawable nav_up = ContextCompat.getDrawable(ctx, drawable);
        nav_up.setBounds(0, 0, nav_up.getMinimumWidth(),
                nav_up.getMinimumHeight());
        tvLeft.setCompoundDrawables(nav_up, null, null, null);
    }

    /**
     * 设置标题栏右边TextView的文字
     *
     * @param src
     */
    public void setLeftTVSrc(String src) {
        tvLeft.setText(src);
    }

    /**
     * @return 取得标题栏右边的TextView
     */
    public TextView getLeftTV() {
        return tvLeft;
    }
    //TODO -------------------设置返回键状态------------------------------

    /**
     * 隐藏软件盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(rootView.getWindowToken(), 0);
    }

    /**
     * 显示软件盘
     */
    public void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(rootView, 0);
    }

    protected void showDialog(Context context) {
        loadingDialog = LoadingUtils.createLoadingDialog(context, false);
        loadingDialog.show();
    }

    protected void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        rootView.removeAllViews();
    }
}
