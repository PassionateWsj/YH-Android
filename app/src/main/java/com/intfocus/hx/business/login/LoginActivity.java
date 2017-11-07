package com.intfocus.hx.business.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.intfocus.hx.R;
import com.intfocus.hx.business.dashboard.DashboardActivity;
import com.intfocus.hx.business.login.bean.Device;
import com.intfocus.hx.business.login.bean.DeviceRequest;
import com.intfocus.hx.business.login.bean.NewUser;
import com.intfocus.hx.general.constant.ConfigConstants;
import com.intfocus.hx.general.constant.ToastColor;
import com.intfocus.hx.general.data.response.BaseResult;
import com.intfocus.hx.general.data.response.login.RegisterResult;
import com.intfocus.hx.general.listen.NoDoubleClickListener;
import com.intfocus.hx.general.net.ApiException;
import com.intfocus.hx.general.net.CodeHandledSubscriber;
import com.intfocus.hx.general.net.RetrofitUtil;
import com.intfocus.hx.general.util.ActionLogUtil;
import com.intfocus.hx.general.util.FileUtil;
import com.intfocus.hx.general.util.HttpUtil;
import com.intfocus.hx.general.util.K;
import com.intfocus.hx.general.util.LogUtil;
import com.intfocus.hx.general.util.MapUtil;
import com.intfocus.hx.general.util.ToastUtils;
import com.intfocus.hx.general.util.URLs;
import com.pgyersdk.javabean.AppBean;
import com.pgyersdk.update.PgyUpdateManager;
import com.pgyersdk.update.UpdateManagerListener;

import org.OpenUDID.OpenUDID_manager;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;

import static com.intfocus.hx.general.base.BaseActivity.kVersionCode;
import static com.intfocus.hx.general.util.K.K_CURRENT_UI_VERSION;
import static com.intfocus.hx.general.util.K.K_USER_ID;
import static com.intfocus.hx.general.util.K.K_USER_NAME;
import static com.intfocus.hx.general.util.URLs.kGroupId;
import static com.intfocus.hx.general.util.URLs.kRoleId;
import static com.intfocus.hx.general.util.URLs.kUserNum;

/**
 * ****************************************************
 *
 * @author jameswong
 *         created on: 17/10/19 下午5:43
 *         e-mail: PassionateWsj@outlook.com
 *         name:
 *         desc:
 *         ****************************************************
 */
public class LoginActivity extends FragmentActivity {
    public String kFromActivity = "from_activity";         // APP 启动标识
    public String kSuccess = "success";                    // 用户登录验证结果
    private EditText usernameEditText, passwordEditText;
    private String userNum, userPass;
    private View mLinearUsernameBelowLine;
    private View mLinearPasswordBelowLine;
    private LinearLayout mLlEtUsernameClear;
    private LinearLayout mLlEtPasswordClear;
    private Button mBtnLogin;
    private DeviceRequest mDeviceRequest;
    private SharedPreferences mUserSP;
    private SharedPreferences.Editor mUserSPEdit;
    private SharedPreferences mPushSP;
    private ProgressDialog mProgressDialog;
    private JSONObject logParams = new JSONObject();
    private Context ctx;
    private String assetsPath, sharedPath;
    /**
     * 最短点击间隔时长 ms
     */
    private long MIN_CLICK_DELAY_TIME = 2000;
    private long mLastClickTime;

    @Override
    @SuppressLint("SetJavaScriptEnabled")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE);
        mPushSP = getSharedPreferences("PushMessage", Context.MODE_PRIVATE);
        mUserSPEdit = mUserSP.edit();

        ctx = this;

        //设置定位监听
        getLocation();
        assetsPath = FileUtil.dirPath(ctx, K.K_HTML_DIR_NAME);
        sharedPath = FileUtil.sharedPath(ctx);

        setContentView(R.layout.activity_login);
        checkPgyerVersionUpgrade(LoginActivity.this, false);


        usernameEditText = findViewById(R.id.etUsername);
        passwordEditText = findViewById(R.id.etPassword);
        mLinearUsernameBelowLine = findViewById(R.id.linearUsernameBelowLine);
        mLinearPasswordBelowLine = findViewById(R.id.linearPasswordBelowLine);
        mLlEtUsernameClear = findViewById(R.id.ll_etUsername_clear);
        mLlEtPasswordClear = findViewById(R.id.ll_etPassword_clear);
        mBtnLogin = findViewById(R.id.btn_login);

        if (ConfigConstants.ACCOUNT_INPUTTYPE_NUMBER) {
            usernameEditText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);
        } else {
            usernameEditText.setInputType(EditorInfo.TYPE_CLASS_TEXT);
        }

        // 初始化监听
        initListener();

        /*
         * 显示记住用户名称
         */
        usernameEditText.setText(mUserSP.getString("user_num", ""));
    }

    /**
     * 设置定位回调监听
     */
    private void getLocation() {
        MapUtil.getInstance(this).getAMapLocation(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation location) {
                if (null != location) {
                    StringBuffer sb = new StringBuffer();
                    //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                    if (location.getErrorCode() == 0) {
                        SharedPreferences mUserSP = ctx.getSharedPreferences("UserBean", Context.MODE_PRIVATE);
                        mUserSP.edit().putString("location",
                                String.format("%.6f", location.getLongitude()) + ","
                                        + String.format("%.6f", location.getLatitude())).apply();

                        sb.append("经    度    : " + location.getLongitude() + "\n");
                        sb.append("纬    度    : " + location.getLatitude() + "\n");
                    } else {
                        //定位失败
                        sb.append("错误码:" + location.getErrorCode() + "\n");
                        sb.append("错误信息:" + location.getErrorInfo() + "\n");
                        sb.append("错误描述:" + location.getLocationDetail() + "\n");
                    }

                    //解析定位结果
                    String result = sb.toString();
                    Log.i("testlog", result);
                } else {
                    Log.i("testlog", "定位失败，loc is null");
                }
            }
        });
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
                RetrofitUtil.getHttpService(ctx).getRegister("sypc_000005")
                        .compose(new RetrofitUtil.CommonOptions<RegisterResult>())
                        .subscribe(new CodeHandledSubscriber<RegisterResult>() {
                            @Override
                            public void onError(ApiException apiException) {
                                ToastUtils.INSTANCE.show(LoginActivity.this, apiException.getDisplayMessage());
                            }

                            @Override
                            public void onBusinessNext(RegisterResult data) {
                                ToastUtils.INSTANCE.show(LoginActivity.this, data.getData());
                            }

                            @Override
                            public void onCompleted() {
                            }
                        });
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

        mBtnLogin.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                actionSubmit(v);
            }
        });

    }

    /**
     * 改变 EditText 正在编辑/不在编辑 下划线颜色
     *
     * @param hasFocus
     * @param underLineView
     */
    private void changeEditTextFocusUnderLineColor(boolean hasFocus, View underLineView) {
        if (hasFocus) {
            underLineView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.co1_syr));
        } else {
            underLineView.setBackgroundColor(ContextCompat.getColor(ctx, R.color.co9_syr));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        PgyUpdateManager.unregister();
        super.onDestroy();
    }

    /**
     * 返回键监听
     */
    @Override
    public void onBackPressed() {
        Toast toast = Toast.makeText(ctx, "再按一次退出生意人", Toast.LENGTH_SHORT);
        long currentTime = Calendar.getInstance().getTimeInMillis();
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
            mLastClickTime = currentTime;
            toast.show();
            return;
        }
        toast.cancel();
        finish();
        System.exit(0);
    }

    /**
     * 登录按钮点击事件
     */
    public void actionSubmit(View v) {
        try {
            userNum = usernameEditText.getText().toString();
            userPass = passwordEditText.getText().toString();

            mUserSP.edit().putString("user_num", userNum).apply();

            if (userNum.isEmpty() || userPass.isEmpty()) {
                ToastUtils.INSTANCE.show(LoginActivity.this, "请输入用户名与密码");
                return;
            }

            hideKeyboard();
            mProgressDialog = ProgressDialog.show(LoginActivity.this, "稍等", "验证用户信息...");

            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);

            // 上传设备信息
            uploadDeviceInformation(packageInfo);

            mUserSPEdit.putString(K.K_APP_VERSION, String.format("a%s", packageInfo.versionName));
            mUserSPEdit.putString("os_version", "android" + Build.VERSION.RELEASE);
            mUserSPEdit.putString("device_info", android.os.Build.MODEL).apply();

            // 登录验证
            RetrofitUtil.getHttpService(ctx).userLogin(userNum, URLs.MD5(userPass), mUserSP.getString("location", "0,0"))
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
                            mProgressDialog.dismiss();
                            try {
                                logParams = new JSONObject();
                                logParams.put(URLs.kAction, "unlogin");
                                logParams.put(URLs.kUserName, userNum + "|;|" + userPass);
                                logParams.put(URLs.kObjTitle, apiException.getDisplayMessage());
                                ActionLogUtil.actionLoginLog(ctx, logParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ToastUtils.INSTANCE.show(LoginActivity.this, apiException.getDisplayMessage());
                        }

                        /**
                         * 登录成功
                         * @param data 返回的数据
                         */
                        @Override
                        public void onBusinessNext(NewUser data) {
                            mUserSPEdit.putString("password", URLs.MD5(userPass));
                            upLoadDevice(); //上传设备信息

                            mUserSPEdit.putBoolean(URLs.kIsLogin, true);
                            mUserSPEdit.putString(K_USER_NAME, data.getData().getUser_name());
                            mUserSPEdit.putString(kGroupId, data.getData().getGroup_id());
                            mUserSPEdit.putString(kRoleId, data.getData().getRole_id());
                            mUserSPEdit.putString(K_USER_ID, data.getData().getUser_id());
                            mUserSPEdit.putString(URLs.kRoleName, data.getData().getRole_name());
                            mUserSPEdit.putString(URLs.kGroupName, data.getData().getGroup_name());
                            mUserSPEdit.putString(kUserNum, data.getData().getUser_num());
                            mUserSPEdit.putString(K_CURRENT_UI_VERSION, "v2").apply();

                            // 判断是否包含推送信息，如果包含 登录成功直接跳转推送信息指定页面
                            if (getIntent().hasExtra("msgData")) {
                                Bundle msgData = getIntent().getBundleExtra("msgData");
                                Intent intent = new Intent(LoginActivity.this, DashboardActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                intent.putExtra("msgData", msgData);
                                LoginActivity.this.startActivity(intent);
                            } else {
                                // 检测用户空间，版本是否升级版本是否升级
                                FileUtil.checkVersionUpgrade(ctx, assetsPath, sharedPath);

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
                                ActionLogUtil.actionLog(ctx, logParams);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mProgressDialog.dismiss();
                            finish();
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            mProgressDialog.dismiss();
            ToastUtils.INSTANCE.show(this, e.getLocalizedMessage());
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
        RetrofitUtil.getHttpService(ctx).deviceUpLoad(mDeviceRequest)
                .compose(new RetrofitUtil.CommonOptions<Device>())
                .subscribe(new CodeHandledSubscriber<Device>() {
                    @Override
                    public void onError(ApiException apiException) {
                        ToastUtils.INSTANCE.show(LoginActivity.this, apiException.getDisplayMessage());
                    }

                    /**
                     * 上传设备信息成功
                     * @param data 返回的数据
                     */
                    @Override
                    public void onBusinessNext(Device data) {
                        if (data.getMResult() == null) {
                            return;
                        }
                        mUserSPEdit.putString("device_uuid", data.getMResult().getDevice_uuid());
                        mUserSPEdit.putBoolean("device_state", data.getMResult().getDevice_state());
                        mUserSPEdit.putString("user_device_id", String.valueOf(data.getMResult().getUser_device_id())).apply();

                        RetrofitUtil.getHttpService(ctx).putPushToken(data.getMResult().getDevice_uuid(), mPushSP.getString("push_token", ""))
                                .compose(new RetrofitUtil.CommonOptions<BaseResult>())
                                .subscribe(new CodeHandledSubscriber<BaseResult>() {
                                    @Override
                                    public void onError(ApiException apiException) {
                                    }

                                    @Override
                                    public void onBusinessNext(BaseResult data) {
                                    }

                                    @Override
                                    public void onCompleted() {

                                    }
                                });
                    }

                    @Override
                    public void onCompleted() {
                    }
                });

    }

    /**
     * 隐藏软件盘
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }

    /**
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     */
    public void checkPgyerVersionUpgrade(final Activity activity, final boolean isShowToast) {
        PgyUpdateManager.register(activity, "com.intfocus.yh_android.fileprovider", new UpdateManagerListener() {
            @Override
            public void onUpdateAvailable(final String result) {
                try {
                    final AppBean appBean = getAppBeanFromString(result);

                    if (result == null || result.isEmpty()) {
                        return;
                    }

                    PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int currentVersionCode = packageInfo.versionCode;
                    JSONObject response = new JSONObject(result);
                    String message = response.getString("message");

                    JSONObject responseVersionJSON = response.getJSONObject(URLs.kData);
                    int newVersionCode = responseVersionJSON.getInt(kVersionCode);

                    String newVersionName = responseVersionJSON.getString("versionName");
                    LogUtil.d("app update:::", "currentVersionCode : " + currentVersionCode + " , newVersionCode : " + newVersionCode);
                    if (currentVersionCode >= newVersionCode) {
                        return;
                    }

                    String pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(getApplicationContext()), K.K_PGYER_VERSION_CONFIG_FILE_NAME);
                    FileUtil.writeFile(pgyerVersionPath, result);

                    if (newVersionCode % 2 == 1) {
                        if (isShowToast) {
                            ToastUtils.INSTANCE.show(LoginActivity.this, String.format("有发布测试版本%s(%s)", newVersionName, newVersionCode), ToastColor.SUCCESS);
                        }

                        return;
                    } else if (HttpUtil.isWifi(activity) && newVersionCode % 10 == 8) {
                        startDownloadTask(activity, appBean.getDownloadURL());
                        return;
                    }
                    new AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(message.isEmpty() ? "无升级简介" : message)
                            .setPositiveButton(
                                    "确定",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            startDownloadTask(activity, appBean.getDownloadURL());
                                        }
                                    })
                            .setNegativeButton("下一次",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    })
                            .setCancelable(false)
                            .show();

                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNoUpdateAvailable() {
            }
        });
    }
}
