package com.intfocus.yonghuitest.login;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.intfocus.yonghuitest.R;
import com.intfocus.yonghuitest.base.BaseActivity;
import com.intfocus.yonghuitest.dashboard.DashboardActivity;
import com.intfocus.yonghuitest.login.bean.Device;
import com.intfocus.yonghuitest.login.bean.DeviceRequest;
import com.intfocus.yonghuitest.login.bean.NewUser;
import com.intfocus.yonghuitest.net.ApiException;
import com.intfocus.yonghuitest.net.CodeHandledSubscriber;
import com.intfocus.yonghuitest.net.RetrofitUtil;
import com.intfocus.yonghuitest.util.ActionLogUtil;
import com.intfocus.yonghuitest.util.ApiHelper;
import com.intfocus.yonghuitest.util.FileUtil;
import com.intfocus.yonghuitest.util.K;
import com.intfocus.yonghuitest.util.ToastUtils;
import com.intfocus.yonghuitest.util.URLs;
import com.pgyersdk.update.PgyUpdateManager;

import org.OpenUDID.OpenUDID_manager;
import org.json.JSONObject;

public class LoginActivity extends BaseActivity {
    public String kFromActivity = "from_activity";         // APP 启动标识
    public String kSuccess = "success";               // 用户登录验证结果
    private EditText usernameEditText, passwordEditText;
    private String userNum, userPass;
    private View mLinearUsernameBelowLine;
    private View mLinearPasswordBelowLine;
    private LinearLayout mLlEtUsernameClear;
    private LinearLayout mLlEtPasswordClear;
    private DeviceRequest mDeviceRequest;
    private JSONObject mUserJSON;
    private SharedPreferences mUserSP;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);

        // 使背景填满整个屏幕,包括状态栏
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//        }

        ApiHelper.getAMapLocation(mAppContext);

        /*
         *  如果是从触屏界面过来，则直接进入主界面如
         *  不是的话，相当于直接启动应用，则检测是否有设置锁屏
         */
//        if (intent.hasExtra(kFromActivity) && intent.getStringExtra(kFromActivity).equals("ConfirmPassCodeActivity")) {
//            intent = new Intent(LoginActivity.this, DashboardActivity.class);
//            intent.putExtra(kFromActivity, intent.getStringExtra(kFromActivity));
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            LoginActivity.this.startActivity(intent);
//
//            finish();
//        } else {
            /*
             *  检测版本更新
             *    1. 与锁屏界面互斥；取消解屏时，返回登录界面，则不再检测版本更新；
             *    2. 原因：如果解屏成功，直接进入MainActivity,会在BaseActivity#finishLoginActivityWhenInMainAcitivty中结束LoginActivity,若此时有AlertDialog，会报错误:Activity has leaked window com.android.internal.policy.impl.PhoneWindow$DecorView@44f72ff0 that was originally added here
             */
        checkPgyerVersionUpgrade(LoginActivity.this, false);
//        }

        setContentView(R.layout.activity_login_new);

        usernameEditText = (EditText) findViewById(R.id.etUsername);
        passwordEditText = (EditText) findViewById(R.id.etPassword);
        mLinearUsernameBelowLine = findViewById(R.id.linearUsernameBelowLine);
        mLinearPasswordBelowLine = findViewById(R.id.linearPasswordBelowLine);
        mLlEtUsernameClear = (LinearLayout) findViewById(R.id.ll_etUsername_clear);
        mLlEtPasswordClear = (LinearLayout) findViewById(R.id.ll_etPassword_clear);

        // 初始化监听
        initListener();


        /*
         *  基本目录结构
         */
//        makeSureFolder(mAppContext, K.kSharedDirName);
//        makeSureFolder(mAppContext, K.kCachedDirName);

        /*
         * 显示记住用户名称
         */
        usernameEditText.setText(mUserSP.getString("user_num", ""));

        /*
         *  当用户系统不在我们支持范围内时,发出警告。
         */
        if (Build.VERSION.SDK_INT > K.kMaxSdkVersion || Build.VERSION.SDK_INT < K.kMinSdkVersion) {
            showVersionWarring();
        }

//        View v = new View(this);
//        actionSubmit(v);

        /*
         * 检测登录界面，版本是否升级
         */
        checkVersionUpgrade(assetsPath);
    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        // 忘记密码监听
        findViewById(R.id.forgetPasswordTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });
        // 注册监听
        findViewById(R.id.applyRegistTv).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtils.INSTANCE.show(LoginActivity.this, "请到数据化运营平台申请开通账号", R.color.co11_syr);
            }
        });

        // 用户名输入框 焦点监听 隐藏/显示 清空按钮

        usernameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditTextFocusUnderLineColor(hasFocus, mLinearUsernameBelowLine);
                if (usernameEditText.getText().length() > 0 && hasFocus) {
                    mLlEtUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtUsernameClear.setVisibility(View.GONE);
                }
            }
        });

        // 用户名输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mLlEtUsernameClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtUsernameClear.setVisibility(View.GONE);
                }

            }
        });

        // 清空用户名 按钮 监听
        mLlEtUsernameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameEditText.setText("");
            }
        });

        // 密码输入框 焦点监听 隐藏/显示 清空按钮
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                changeEditTextFocusUnderLineColor(hasFocus, mLinearPasswordBelowLine);
                if (passwordEditText.getText().length() > 0 && hasFocus) {
                    mLlEtPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtPasswordClear.setVisibility(View.GONE);
                }
            }
        });

        // 密码输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mLlEtPasswordClear.setVisibility(View.VISIBLE);
                } else {
                    mLlEtPasswordClear.setVisibility(View.GONE);
                }

            }
        });

        // 密码输入框 回车 监听
        passwordEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
//                    actionSubmit(v);
                    hideKeyboard();
                }
                return false;
            }
        });

        // 清空密码 按钮 监听
        mLlEtPasswordClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                passwordEditText.setText("");
            }
        });

        // 背景布局 触摸 监听
        findViewById(R.id.login_layout).setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                return false;
            }
        });
    }
//
//    private void hideKeyBoard() {
//        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(passwordEditText.getWindowToken(), 0);
//        imm.hideSoftInputFromWindow(usernameEditText.getWindowToken(), 0);
//    }

    /**
     * 改变 EditText 正在编辑/不在编辑 下划线颜色
     *
     * @param hasFocus
     * @param underLineView
     */
    private void changeEditTextFocusUnderLineColor(boolean hasFocus, View underLineView) {
        if (hasFocus) {
            underLineView.setBackgroundColor(getResources().getColor(R.color.co1_syr));
        } else {
            underLineView.setBackgroundColor(getResources().getColor(R.color.co9_syr));
        }
    }

    protected void onResume() {
        mMyApp.setCurrentActivity(this);
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        super.onResume();
    }

    protected void onDestroy() {
        mWebView = null;
        user = null;
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        mMyApp.setCurrentActivity(null);
        finish();
        System.exit(0);
    }

    /*
     * 系统版本警告
     */
    private void showVersionWarring() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("温馨提示")
                .setMessage(String.format("本应用不支持当前系统版本【Android %s】,强制使用可能会出现异常喔,给您带来的不便深表歉意,我们会尽快适配的!", Build.VERSION.RELEASE))
                .setPositiveButton("退出应用", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mMyApp.setCurrentActivity(null);
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("继续运行", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 返回 LoginActivity
                    }
                });
        builder.show();
    }

    /*
     * 登录按钮点击事件
     */
    public void actionSubmit(View v) {
        try {
            userNum = usernameEditText.getText().toString();
            userPass = passwordEditText.getText().toString();

//            userNum = "13162726850";
//            userPass = "1";

            mUserSP.edit().putString("user_num", userNum).commit();

            if (userNum.isEmpty() || userPass.isEmpty()) {
                ToastUtils.INSTANCE.show(LoginActivity.this, "请输入用户名与密码");
                return;
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    hideKeyboard();
                    mProgressDialog = ProgressDialog.show(LoginActivity.this, "稍等", "验证用户信息...");
                }
            });


            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            // 上传设备信息
            uploadDeviceInformation(packageInfo);

            mUserSP = getApplicationContext().getSharedPreferences("UserBean", MODE_PRIVATE);
            mUserSP.edit().putString(K.kAppVersion, String.format("a%s", packageInfo.versionName)).commit();
            mUserSP.edit().putString("os_version", "android" + Build.VERSION.RELEASE).commit();
            mUserSP.edit().putString("device_info", android.os.Build.MODEL).commit();

            // 登录验证
            RetrofitUtil.getHttpService().userLogin(userNum, URLs.MD5(userPass))
                    .compose(new RetrofitUtil.CommonOptions<NewUser>())
                    .subscribe(new CodeHandledSubscriber<NewUser>() {

                        @Override
                        public void onCompleted() {
                        }

                        /**
                         * 登录请求失败
                         * @param apiException
                         */
                        @Override
                        public void onError(ApiException apiException) {
                            if (mProgressDialog != null)
                                mProgressDialog.dismiss();
                            try {
                                logParams = new JSONObject();
                                logParams.put(URLs.kAction, "unlogin");
                                logParams.put(URLs.kUserName, userNum + "|;|" + userPass);
                                logParams.put(URLs.kObjTitle, apiException.getDisplayMessage());
                                ActionLogUtil.actionLoginLog(mAppContext, logParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ToastUtils.INSTANCE.show(getApplicationContext(), apiException.getDisplayMessage());
                        }

                        /**
                         * 登录成功
                         * @param data 返回的数据
                         */
                        @Override
                        public void onBusinessNext(NewUser data) {
                            upLoadDevice();
                            loginSuccess();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            if (mProgressDialog != null) mProgressDialog.dismiss();
            toast(e.getLocalizedMessage());
        }
    }

    @NonNull
    private void uploadDeviceInformation(PackageInfo packageInfo) {
        mDeviceRequest = new DeviceRequest();
        mDeviceRequest.setUser_num(userNum);
        DeviceRequest.DeviceBean deviceBean = new DeviceRequest.DeviceBean();
        deviceBean.setUuid(OpenUDID_manager.getOpenUDID());
        deviceBean.setOs(Build.MODEL);
        deviceBean.setName(Build.MODEL);
        deviceBean.setOs_version(Build.VERSION.RELEASE);
        deviceBean.setPlatform("android");
        mDeviceRequest.setDevice(deviceBean);
        mDeviceRequest.setApp_version(packageInfo.versionName);
        mDeviceRequest.setBrowser(new WebView(this).getSettings().getUserAgentString());
    }

    /**
     * 上传设备信息
     */
    private void upLoadDevice() {
        RetrofitUtil.getHttpService().deviceUpLoad(mDeviceRequest)
                .compose(new RetrofitUtil.CommonOptions<Device>())
                .subscribe(new CodeHandledSubscriber<Device>() {
                    @Override
                    public void onError(ApiException apiException) {
                        ToastUtils.INSTANCE.show(getApplicationContext(), apiException.getDisplayMessage());
                    }

                    /**
                     * 上传设备信息成功
                     * @param data 返回的数据
                     */
                    @Override
                    public void onBusinessNext(Device data) {
                        ActionLogUtil.pushDeviceToken(getApplicationContext(), data.getMResult().getDevice_uuid());

                    }

                    @Override
                    public void onCompleted() {
                    }
                });
    }

    /**
     * 登录成功后处理的逻辑
     */
    private void loginSuccess() {
        mUserSP.edit().putBoolean(URLs.kIsLogin, true).commit();

        // 判断是否包含推送信息，如果包含 登录成功直接跳转推送信息指定页面
        if (getIntent().hasExtra("msgData")) {
            Bundle msgData = getIntent().getBundleExtra("msgData");
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("msgData", msgData);
            LoginActivity.this.startActivity(intent);
        } else {

            // 检测用户空间，版本是否升级
            assetsPath = FileUtil.dirPath(mAppContext, K.kHTMLDirName);
            checkVersionUpgrade(assetsPath);

            // 跳转至主界面
            Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LoginActivity.this.startActivity(intent);
        }
       /*
        * 用户行为记录, 单独异常处理，不可影响用户体验
        */
        try {
            logParams = new JSONObject();
            logParams.put("action", "登录");
            ActionLogUtil.actionLog(mAppContext, logParams);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
        finish();
    }

    /**
     * 隐藏软件盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
