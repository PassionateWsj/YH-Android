package com.intfocus.template.login;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.intfocus.template.R;
import com.intfocus.template.general.net.ApiException;
import com.intfocus.template.general.net.CodeHandledSubscriber;
import com.intfocus.template.general.net.RetrofitUtil;
import com.intfocus.template.listener.NoDoubleClickListener;
import com.intfocus.template.model.response.BaseResult;
import com.intfocus.template.ui.BaseActivity;
import com.intfocus.template.util.ActionLogUtil;
import com.intfocus.template.util.ToastUtils;

public class ForgetPasswordActivity extends BaseActivity {

    private ImageButton mBackBtn;
    private EditText mEtEmployeeId;
    private TextView mBtnSubmit;
    private EditText mEtEmployeePhoneNum;
    private ProgressDialog mRequestDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_forget_password);
        initView();
        initListener();
    }

    /**
     * 初始化视图
     */
    private void initView() {
        mBackBtn = (ImageButton) findViewById(R.id.ibtn_find_pwd_back);
        mEtEmployeePhoneNum = (EditText) findViewById(R.id.et_find_pwd_employee_phone_num);
        mBtnSubmit = (TextView) findViewById(R.id.tv_btn_find_pwd_submit);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        mBackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        mBtnSubmit.setOnClickListener(new NoDoubleClickListener() {
            @Override
            public void onNoDoubleClick(View v) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                }
                String mobile = mEtEmployeePhoneNum.getText().toString();
                if (mobile.length() == 11) {

                    // 发起 post 请求
                    startPost(mobile);

                    ActionLogUtil.actionLog("重置密码");
                } else {
                    ToastUtils.INSTANCE.show(ForgetPasswordActivity.this, "请输入正确的手机号");
                }
            }
        });
    }

    /**
     * 发起 post 请求
     * @param mobile
     */
    public void startPost(String mobile) {
        mRequestDialog = ProgressDialog.show(this, "稍等", "正在重置密码...");
        RetrofitUtil.getHttpService(getApplicationContext()).resetPwd(mobile,"mobile",mobile)
                .compose(new RetrofitUtil.CommonOptions<BaseResult>())
                .subscribe(new CodeHandledSubscriber<BaseResult>() {
                    @Override
                    public void onError(ApiException apiException) {
                        mRequestDialog.dismiss();
                        showErrorMsg(apiException.getDisplayMessage());
                    }

                    @Override
                    public void onBusinessNext(BaseResult data) {
                        showErrorMsg(data.getMessage());
                    }

                    @Override
                    public void onCompleted() {
                        mRequestDialog.dismiss();
                    }
                });

    }

    /**
     * 显示错误信息
     *
     * @param message
     */
    public void showErrorMsg(String message) {
        View view = LayoutInflater.from(this).inflate(R.layout.popup_forget_pwd_notice, null);
        final PopupWindow popupWindow = new PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true);
        TextView tv_forget_pwd_notice_content = (TextView) view.findViewById(R.id.tv_forget_pwd_notice_content);
        view.findViewById(R.id.tv_forget_pwd_notice_confirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        tv_forget_pwd_notice_content.setText(message);

        View parent = LayoutInflater.from(this).inflate(R.layout.activity_new_forget_password, null);
        popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);

    }
}
