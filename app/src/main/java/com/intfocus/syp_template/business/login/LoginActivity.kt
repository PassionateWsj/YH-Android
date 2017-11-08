package com.intfocus.syp_template.business.login

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.WebView
import android.widget.Toast
import com.intfocus.syp_template.R
import com.intfocus.syp_template.business.dashboard.DashboardActivity
import com.intfocus.syp_template.business.login.bean.Device
import com.intfocus.syp_template.business.login.bean.DeviceRequest
import com.intfocus.syp_template.business.login.bean.NewUser
import com.intfocus.syp_template.general.base.BaseActivity.kVersionCode
import com.intfocus.syp_template.general.constant.ConfigConstants
import com.intfocus.syp_template.general.constant.ToastColor
import com.intfocus.syp_template.general.data.response.BaseResult
import com.intfocus.syp_template.general.data.response.login.RegisterResult
import com.intfocus.syp_template.general.listen.NoDoubleClickListener
import com.intfocus.syp_template.general.net.ApiException
import com.intfocus.syp_template.general.net.CodeHandledSubscriber
import com.intfocus.syp_template.general.net.RetrofitUtil
import com.intfocus.syp_template.general.util.*
import com.intfocus.syp_template.general.util.K.*
import com.intfocus.syp_template.general.util.URLs.*
import com.pgyersdk.update.PgyUpdateManager
import com.pgyersdk.update.UpdateManagerListener
import kotlinx.android.synthetic.main.activity_login.*
import org.OpenUDID.OpenUDID_manager
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.util.*

/**
 * ****************************************************
 *
 * @author jameswong
 * created on: 17/10/19 下午5:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class LoginActivity : FragmentActivity() {
    private var userNum: String? = null
    private var userPass: String? = null
    private var mDeviceRequest: DeviceRequest? = null
    private var mUserSP: SharedPreferences? = null
    private var mUserSPEdit: SharedPreferences.Editor? = null
    private var mPushSP: SharedPreferences? = null
    private var mProgressDialog: ProgressDialog? = null
    private var logParams = JSONObject()
    private var ctx: Context? = null
    private var assetsPath: String? = null
    private var sharedPath: String? = null
    /**
     * 最短点击间隔时长 ms
     */
    private val MIN_CLICK_DELAY_TIME: Long = 2000
    private var mLastClickTime: Long = 0

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        mPushSP = getSharedPreferences("PushMessage", Context.MODE_PRIVATE)
        mUserSPEdit = mUserSP!!.edit()

        ctx = this

        //设置定位监听
        getLocation()
        assetsPath = FileUtil.dirPath(ctx, K.K_HTML_DIR_NAME)
        sharedPath = FileUtil.sharedPath(ctx)

        setContentView(R.layout.activity_login)
        checkPgyerVersionUpgrade(this@LoginActivity, false)

        initShow()

        // 初始化监听
        initListener()


        // 显示记住用户名称
        etUsername.setText(mUserSP!!.getString("user_num", ""))
    }

    private fun initShow() {
        if (ConfigConstants.ACCOUNT_INPUTTYPE_NUMBER) {
            etUsername.inputType = EditorInfo.TYPE_CLASS_NUMBER
        } else {
            etUsername.inputType = EditorInfo.TYPE_CLASS_TEXT
        }
        if (ConfigConstants.UNABLE_LOGIN_SHOW) {
            ll_unable_login.visibility = View.VISIBLE
        } else {
            ll_unable_login.visibility = View.GONE
        }
    }

    /**
     * 设置定位回调监听
     */
    private fun getLocation() {
        MapUtil.getInstance(this).getAMapLocation { location ->
            if (null != location) {
                val sb = StringBuffer()
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.errorCode == 0) {
                    val mUserSP = ctx!!.getSharedPreferences("UserBean", Context.MODE_PRIVATE)
                    mUserSP.edit().putString("location",
                            String.format("%.6f", location.longitude) + ","
                                    + String.format("%.6f", location.latitude)).apply()

                    sb.append("经    度    : " + location.longitude + "\n")
                    sb.append("纬    度    : " + location.latitude + "\n")
                } else {
                    //定位失败
                    sb.append("错误码:" + location.errorCode + "\n")
                    sb.append("错误信息:" + location.errorInfo + "\n")
                    sb.append("错误描述:" + location.locationDetail + "\n")
                }

                //解析定位结果
                val result = sb.toString()
                Log.i("testlog", result)
            } else {
                Log.i("testlog", "定位失败，loc is null")
            }
        }
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        // 忘记密码监听
        findViewById<View>(R.id.forgetPasswordTv).setOnClickListener {
            val intent = Intent(this@LoginActivity, ForgetPasswordActivity::class.java)
            startActivity(intent)
        }

        // 注册监听
        findViewById<View>(R.id.applyRegistTv).setOnClickListener {
            RetrofitUtil.getHttpService(ctx).getRegister("sypc_000005")
                    .compose(RetrofitUtil.CommonOptions())
                    .subscribe(object : CodeHandledSubscriber<RegisterResult>() {
                        override fun onError(apiException: ApiException) {
                            ToastUtils.show(this@LoginActivity, apiException.displayMessage)
                        }

                        override fun onBusinessNext(data: RegisterResult) {
                            ToastUtils.show(this@LoginActivity, data.data!!)
                        }

                        override fun onCompleted() {}
                    })
        }

        // 用户名输入框 焦点监听 隐藏/显示 清空按钮
        etUsername.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            changeEditTextFocusUnderLineColor(hasFocus, linearUsernameBelowLine)
            if (etUsername.text.isNotEmpty() && hasFocus) {
                ll_etUsername_clear.visibility = View.VISIBLE
            } else {
                ll_etUsername_clear.visibility = View.GONE
            }
        }

        // 用户名输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) = if (s.toString().isNotEmpty()) {
                ll_etUsername_clear.visibility = View.VISIBLE
            } else {
                ll_etUsername_clear.visibility = View.GONE
            }
        })

        // 清空用户名 按钮 监听
        ll_etUsername_clear.setOnClickListener { etUsername.setText("") }

        // 密码输入框 焦点监听 隐藏/显示 清空按钮
        etPassword.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            changeEditTextFocusUnderLineColor(hasFocus, linearPasswordBelowLine)
            if (etPassword.text.isNotEmpty() && hasFocus) {
                ll_etPassword_clear.visibility = View.VISIBLE
            } else {
                ll_etPassword_clear.visibility = View.GONE
            }
        }

        // 密码输入框 文本变化监听
        // 处理 显示/隐藏 清空按钮事件
        etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable) = if (s.toString().isNotEmpty()) {
                ll_etPassword_clear.visibility = View.VISIBLE
            } else {
                ll_etPassword_clear.visibility = View.GONE
            }
        })

        // 密码输入框 回车 监听
        etPassword.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                //                    actionSubmit(v);
                hideKeyboard()
            }
            false
        }

        // 清空密码 按钮 监听
        ll_etPassword_clear.setOnClickListener { etPassword.setText("") }

        // 背景布局 触摸 监听
        findViewById<View>(R.id.login_layout).setOnTouchListener { _, _ ->
            hideKeyboard()
            false
        }

        btn_login.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                actionSubmit(v)
            }
        })

    }

    /**
     * 改变 EditText 正在编辑/不在编辑 下划线颜色
     *
     * @param hasFocus
     * @param underLineView
     */
    private fun changeEditTextFocusUnderLineColor(hasFocus: Boolean, underLineView: View?) {
        if (hasFocus) {
            underLineView!!.setBackgroundColor(ContextCompat.getColor(ctx!!, R.color.co1_syr))
        } else {
            underLineView!!.setBackgroundColor(ContextCompat.getColor(ctx!!, R.color.co9_syr))
        }
    }

    override fun onDestroy() {
        PgyUpdateManager.unregister()
        super.onDestroy()
    }

    /**
     * 返回键监听
     */
    override fun onBackPressed() {
        val toast = Toast.makeText(ctx, "再按一次退出生意人", Toast.LENGTH_SHORT)
        val currentTime = Calendar.getInstance().timeInMillis
        if (currentTime - mLastClickTime > MIN_CLICK_DELAY_TIME) {
            mLastClickTime = currentTime
            toast.show()
            return
        }
        toast.cancel()
        finish()
        System.exit(0)
    }

    /**
     * 登录按钮点击事件
     */
    fun actionSubmit(v: View) {
        try {
            userNum = etUsername.text.toString()
            userPass = etPassword.text.toString()

            mUserSP!!.edit().putString("user_num", userNum).apply()

            if (userNum!!.isEmpty() || userPass!!.isEmpty()) {
                ToastUtils.show(this@LoginActivity, "请输入用户名与密码")
                return
            }

            hideKeyboard()
            mProgressDialog = ProgressDialog.show(this@LoginActivity, "稍等", "验证用户信息...")

            val packageInfo = packageManager.getPackageInfo(packageName, 0)

            // 上传设备信息
            uploadDeviceInformation(packageInfo)

            mUserSPEdit!!.putString(K.K_APP_VERSION, String.format("a%s", packageInfo.versionName))
            mUserSPEdit!!.putString("os_version", "android" + Build.VERSION.RELEASE)
            mUserSPEdit!!.putString("device_info", android.os.Build.MODEL).apply()

            // 登录验证
            RetrofitUtil.getHttpService(ctx).userLogin(userNum, URLs.MD5(userPass), mUserSP!!.getString("location", "0,0"))
                    .compose(RetrofitUtil.CommonOptions())
                    .subscribe(object : CodeHandledSubscriber<NewUser>() {

                        override fun onCompleted() {

                        }

                        /**
                         * 登录请求失败
                         * @param apiException
                         */
                        override fun onError(apiException: ApiException) {
                            mProgressDialog!!.dismiss()
                            try {
                                logParams = JSONObject()
                                logParams.put(URLs.kAction, "unlogin")
                                logParams.put(URLs.kUserName, userNum + "|;|" + userPass)
                                logParams.put(URLs.kObjTitle, apiException.displayMessage)
                                ActionLogUtil.actionLoginLog(ctx, logParams)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            ToastUtils.show(this@LoginActivity, apiException.displayMessage)
                        }

                        /**
                         * 登录成功
                         * @param data 返回的数据
                         */
                        override fun onBusinessNext(data: NewUser) {
                            mUserSPEdit!!.putString("password", URLs.MD5(userPass))
                            upLoadDevice() //上传设备信息

                            mUserSPEdit!!.putBoolean(URLs.kIsLogin, true)
                            mUserSPEdit!!.putString(K_USER_NAME, data.data!!.user_name)
                            mUserSPEdit!!.putString(kGroupId, data.data!!.group_id)
                            mUserSPEdit!!.putString(kRoleId, data.data!!.role_id)
                            mUserSPEdit!!.putString(K_USER_ID, data.data!!.user_id)
                            mUserSPEdit!!.putString(URLs.kRoleName, data.data!!.role_name)
                            mUserSPEdit!!.putString(URLs.kGroupName, data.data!!.group_name)
                            mUserSPEdit!!.putString(kUserNum, data.data!!.user_num)
                            mUserSPEdit!!.putString(K_CURRENT_UI_VERSION, "v2").apply()

                            // 判断是否包含推送信息，如果包含 登录成功直接跳转推送信息指定页面
                            if (intent.hasExtra("msgData")) {
                                val msgData = intent.getBundleExtra("msgData")
                                val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
//                                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                intent.putExtra("msgData", msgData)
                                this@LoginActivity.startActivity(intent)
                            } else {
                                // 检测用户空间，版本是否升级版本是否升级
                                FileUtil.checkVersionUpgrade(ctx, assetsPath, sharedPath)

                                val pageLinkManagerSP = this@LoginActivity.getSharedPreferences("PageLinkManager", Context.MODE_PRIVATE)
                                val pageSaved = pageLinkManagerSP.getBoolean("pageSaved", false)
                                if (pageSaved) {
                                    val objTitle = pageLinkManagerSP.getString("objTitle", "")
                                    val link = pageLinkManagerSP.getString("link", "")
                                    val objectId = pageLinkManagerSP.getString("objectId", "")
                                    val templateId = pageLinkManagerSP.getString("templateId", "")
                                    val objectType = pageLinkManagerSP.getString("objectType", "")
                                    PageLinkManage.pageLink(this@LoginActivity, objTitle, link, objectId, templateId, objectType)
                                } else {
                                    // 跳转至主界面
                                    val intent = Intent(this@LoginActivity, DashboardActivity::class.java)
//                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                    this@LoginActivity.startActivity(intent)
                                }
                            }

                            /*
                            * 用户行为记录, 单独异常处理，不可影响用户体验
                            */
                            try {
                                logParams = JSONObject()
                                logParams.put("action", "登录")
                                ActionLogUtil.actionLog(ctx, logParams)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }

                            mProgressDialog!!.dismiss()
                            finish()
                        }
                    })
        } catch (e: Exception) {
            e.printStackTrace()
            mProgressDialog!!.dismiss()
            ToastUtils.show(this, e.localizedMessage)
        }

    }

    private fun uploadDeviceInformation(packageInfo: PackageInfo) {
        mDeviceRequest = DeviceRequest()
        mDeviceRequest!!.user_num = userNum
        val deviceBean = DeviceRequest.DeviceBean()
        deviceBean.uuid = OpenUDID_manager.getOpenUDID()
        deviceBean.os = Build.MODEL
        deviceBean.name = Build.MODEL
        deviceBean.os_version = Build.VERSION.RELEASE
        deviceBean.platform = "android"
        mDeviceRequest!!.device = deviceBean
        mDeviceRequest!!.app_version = packageInfo.versionName
        mDeviceRequest!!.browser = WebView(this).settings.userAgentString
    }

    /**
     * 上传设备信息
     */
    private fun upLoadDevice() {
        RetrofitUtil.getHttpService(ctx).deviceUpLoad(mDeviceRequest)
                .compose(RetrofitUtil.CommonOptions())
                .subscribe(object : CodeHandledSubscriber<Device>() {
                    override fun onError(apiException: ApiException) {
                        ToastUtils.show(this@LoginActivity, apiException.displayMessage)
                    }

                    /**
                     * 上传设备信息成功
                     * @param data 返回的数据
                     */
                    override fun onBusinessNext(data: Device) {
                        if (data.mResult == null) {
                            return
                        }
                        mUserSPEdit!!.putString("device_uuid", data.mResult!!.device_uuid)
                        mUserSPEdit!!.putBoolean("device_state", data.mResult!!.device_state)
                        mUserSPEdit!!.putString("user_device_id", data.mResult!!.user_device_id.toString()).apply()

                        RetrofitUtil.getHttpService(ctx).putPushToken(data.mResult!!.device_uuid, mPushSP!!.getString("push_token", ""))
                                .compose(RetrofitUtil.CommonOptions())
                                .subscribe(object : CodeHandledSubscriber<BaseResult>() {
                                    override fun onError(apiException: ApiException) {}

                                    override fun onBusinessNext(data: BaseResult) {}

                                    override fun onCompleted() {

                                    }
                                })
                    }

                    override fun onCompleted() {}
                })

    }

    /**
     * 隐藏软件盘
     */
    private fun hideKeyboard() {
        val imm = getSystemService(
                Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(window.decorView.windowToken, 0)
    }

    /**
     * 托管在蒲公英平台，对比版本号检测是否版本更新
     * 对比 build 值，只准正向安装提示
     * 奇数: 测试版本，仅提示
     * 偶数: 正式版本，点击安装更新
     */
    fun checkPgyerVersionUpgrade(activity: Activity, isShowToast: Boolean) {
        PgyUpdateManager.register(activity, "com.intfocus.yh_android.fileprovider", object : UpdateManagerListener() {
            override fun onUpdateAvailable(result: String?) {
                try {
                    val appBean = UpdateManagerListener.getAppBeanFromString(result)

                    if (result == null || result.isEmpty()) {
                        return
                    }

                    val packageInfo = packageManager.getPackageInfo(packageName, 0)
                    val currentVersionCode = packageInfo.versionCode
                    val response = JSONObject(result)
                    val message = response.getString("message")

                    val responseVersionJSON = response.getJSONObject(URLs.kData)
                    val newVersionCode = responseVersionJSON.getInt(kVersionCode)

                    val newVersionName = responseVersionJSON.getString("versionName")
                    LogUtil.d("app update:::", "currentVersionCode : $currentVersionCode , newVersionCode : $newVersionCode")
                    if (currentVersionCode >= newVersionCode) {
                        return
                    }

                    val pgyerVersionPath = String.format("%s/%s", FileUtil.basePath(applicationContext), K.K_PGYER_VERSION_CONFIG_FILE_NAME)
                    FileUtil.writeFile(pgyerVersionPath, result)

                    if (newVersionCode % 2 == 1) {
                        if (isShowToast) {
                            ToastUtils.show(this@LoginActivity, String.format("有发布测试版本%s(%s)", newVersionName, newVersionCode), ToastColor.SUCCESS)
                        }

                        return
                    } else if (HttpUtil.isWifi(activity) && newVersionCode % 10 == 8) {
                        UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL)
                        return
                    }
                    AlertDialog.Builder(activity)
                            .setTitle("版本更新")
                            .setMessage(if (message.isEmpty()) "无升级简介" else message)
                            .setPositiveButton(
                                    "确定"
                            ) { _, _ -> UpdateManagerListener.startDownloadTask(activity, appBean.downloadURL) }
                            .setNegativeButton("下一次"
                            ) { dialog, _ -> dialog.dismiss() }
                            .setCancelable(false)
                            .show()

                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                } catch (e: JSONException) {
                    e.printStackTrace()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }

            override fun onNoUpdateAvailable() {}
        })
    }
}