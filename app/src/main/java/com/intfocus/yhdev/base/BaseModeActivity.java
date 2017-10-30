package com.intfocus.yhdev.base;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.intfocus.yhdev.R;
import com.intfocus.yhdev.constant.Permissions;
import com.intfocus.yhdev.util.LoadingUtils;
import com.intfocus.yhdev.util.ToastUtils;
import com.zbl.lib.baseframe.core.AbstractActivity;
import com.zbl.lib.baseframe.core.ActManager;
import com.zbl.lib.baseframe.core.Subject;

import org.xutils.x;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * 基类界面
 */
public abstract class BaseModeActivity<T extends Subject> extends AbstractActivity<T> implements EasyPermissions.PermissionCallbacks {
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


    private String TAG = com.intfocus.yhdev.base.BaseActivity.class.getSimpleName();


    /**
     * 定位所需权限数组
     */
    public static final String[] PERMISSIONS_LOCATION = {Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE};
    /**
     * 二维码扫描权限
     */
    public static final String[] PERMISSIONS_CAMERA = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,};
    private boolean isShowHint;


    //TODO ======================================权限处理====================================================


    private ArrayMap<Integer, PermissionCallback> mPermissionCallbacks = new ArrayMap<>();
    public View mContentView;

    /**
     * 权限回调接口
     */
    public interface PermissionCallback {
        /**
         * 成功获取权限
         */
        void hasPermission();

        /**
         * 没有权限
         *
         * @param hasPermanentlyDenied 是否点击不再询问，被设置为永久拒绝权限
         */
        void noPermission(Boolean hasPermanentlyDenied);
    }

    /**
     * 请求权限操作
     *
     * @param rationale             请求权限提示语
     * @param permissionRequestCode 权限requestCode
     * @param perms                 申请的权限列表
     * @param callback              权限结果回调
     */
    public void performCodeWithPermission(@NonNull String rationale,
                                          final int permissionRequestCode, @NonNull String[] perms, @NonNull PermissionCallback callback) {
        if (EasyPermissions.hasPermissions(this, perms)) {
            callback.hasPermission();
        } else {
            mPermissionCallbacks.put(permissionRequestCode, callback);
            EasyPermissions.requestPermissions(this, rationale, permissionRequestCode, perms);
        }
    }

    /**
     * 跳转设置弹框 建议在权限被设置为不在询问时弹出 提示用户前往设置页面打开权限
     *
     * @param tips 提示信息
     */
    protected void alertAppSetPermission(String tips) {
        new AppSettingsDialog.Builder(this, tips)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .build()
                .show();
    }

    /**
     * 跳转设置弹框 建议在权限被设置为不在询问时弹出 提示用户前往设置页面打开权限
     *
     * @param tips        提示信息
     * @param requestCode 页面返回时onActivityResult的requestCode
     */
    public void alertAppSetPermission(String tips, int requestCode) {
        new AppSettingsDialog.Builder(this, tips)
                .setTitle(getString(R.string.permission_deny_again_title))
                .setPositiveButton(getString(R.string.permission_deny_again_positive))
                .setNegativeButton(getString(R.string.permission_deny_again_nagative), null)
                .setRequestCode(requestCode)
                .build()
                .show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        PermissionCallback callback = mPermissionCallbacks.get(requestCode);
        if (callback != null) {
            callback.hasPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        PermissionCallback callback = mPermissionCallbacks.get(requestCode);
        if (callback != null) {
            if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
                callback.noPermission(true);
            } else {
                callback.noPermission(false);
            }
        }
    }

    //TODO =====================================界面处理=====================================================

    @Override
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

        stubHeader = (ViewStub) findViewById(R.id.ll_baseUI_title);

        rootView = (FrameLayout) findViewById(R.id.rootView);
//        progressBar = (ProgressBar) findViewById(R.id.progressBar_baseUI);

        int contentID = setLayoutRes();
        if (contentID != 0 && contentID != -1) {
            mContentView = LayoutInflater.from(this).inflate(contentID, null);
        }
        flContainer = (FrameLayout) findViewById(R.id.fl_baseUI_container);
        if (mContentView != null) {
            flContainer.addView(mContentView);
        }

        onCreateFinish(savedInstanceState);
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
        tvLeft = (TextView) findViewById(R.id.tv_baseUI_back);
        tvLeft.setOnClickListener(viewListener);
        tvTitle = (TextView) findViewById(R.id.tv_baseUI_title);
        topRightLayout = (LinearLayout) findViewById(R.id.ll_baseUI_title_RightView);
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
        ActManager.getActManager().finishActivity();
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
        Drawable nav_up = getResources().getDrawable(drawable);
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
     * 动态权限申请
     */
    public void requestLocation() {
        requestLocation(true);
    }

    /**
     * 动态权限申请
     */
    public void requestLocation(boolean isHint) {
        this.isShowHint = isHint;
        performCodeWithPermission("请允许定位权限", Permissions.ACCESS_COARSE_LOCATION_CODE, PERMISSIONS_LOCATION,
                new PermissionCallback() {
                    @Override
                    public void hasPermission() {
                        if (isShowHint) {
                            ToastUtils.INSTANCE.show(ctx, "定位中……");
                        }
//                        startLocation();
                    }

                    @Override
                    public void noPermission(Boolean hasPermanentlyDenied) {
                        if (hasPermanentlyDenied) {
                            //只是提供跳转系统设置的提示 系统返回后不做检查处理
//                            alertAppSetPermission(getString(R.string.permission_storage_deny_again));

                            //如果需要跳转系统设置页后返回自动再次检查和执行业务
                            alertAppSetPermission(getString(R.string.permission_loaction_deny_again), 1);
                        }
                    }
                });
    }

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
