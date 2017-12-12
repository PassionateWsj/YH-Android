package com.intfocus.shengyiplus.scanner

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.intfocus.shengyiplus.R
import com.intfocus.shengyiplus.ConfigConstants
import com.intfocus.shengyiplus.constant.Params.BANNER_NAME
import com.intfocus.shengyiplus.constant.Params.CODE_INFO
import com.intfocus.shengyiplus.constant.Params.CODE_TYPE
import com.intfocus.shengyiplus.constant.Params.LINK
import com.intfocus.shengyiplus.constant.Params.STORE
import com.intfocus.shengyiplus.constant.Params.STORE_ID
import com.intfocus.shengyiplus.model.response.scanner.NearestStoresResult
import com.intfocus.shengyiplus.model.response.scanner.StoreItem
import com.intfocus.shengyiplus.model.OrmDBHelper
import com.intfocus.shengyiplus.listener.NoDoubleClickListener
import com.intfocus.shengyiplus.general.net.ApiException
import com.intfocus.shengyiplus.general.net.CodeHandledSubscriber
import com.intfocus.shengyiplus.general.net.RetrofitUtil
import com.intfocus.shengyiplus.subject.two.WebPageActivity
import com.intfocus.shengyiplus.util.*
import com.taobao.accs.ACCSManager.mContext
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_bar_code_scanner.*
import kotlinx.android.synthetic.main.popup_input_barcode.view.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.sql.SQLException

/**
 * ****************************************************
 * @author jameswong
 * created on: 17/10/19 下午5:43
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class BarCodeScannerActivity : AppCompatActivity(), QRCodeView.Delegate, View.OnClickListener {

    companion object {
        val TAG = "hjjzz"
        val REQUEST_CODE_CHOOSE = 1
    }

    /**
     * 是否开启闪光灯
     */
    private var isLightOn = false
    /**
     * 是否是启动 Activity 的操作
     */
    private var isStartActivity = false
    /**
     * PopupWindow 布局文件
     */
    private var view: View? = null
    /**
     * PopupWindow 实体类
     */
    private var popupWindow: PopupWindow? = null
    /**
     * PopupWindow 闪光灯按钮文本
     */
    private var tvInputBarcodeLight: TextView? = null
    /**
     * PopupWindow 闪光灯按钮
     */
    private var cbInputBarcodeLight: CheckBox? = null
    /**
     * 扫描二维码页面 闪光灯按钮文本
     */
    private var tvBarcodeLight: TextView? = null
    /**
     * 扫描二维码页面 闪光灯按钮按钮
     */
    private var cbBarcodeLight: CheckBox? = null
    /**
     * 最新门店名
     */
    private var nearestStoreName: String? = null
    /**
     * 是否开启定位权限
     */
    private var openPositioningPermissionsFilter = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner)

        initData()
        initView()
        initShow()
        initListener()

        initScanner()
    }

    private fun initScanner() {

        zbarview_barcode_scanner.setDelegate(this)
        zbarview_barcode_scanner.startSpot()

    }

    private fun initData() {
        tv_barcode_local_position.visibility = if (ConfigConstants.SCAN_LOCATION) {
            tv_barcode_local_position.text = "正在定位"
            getLocation()
            View.VISIBLE
        } else {
            View.GONE
        }
    }


    override fun onStart() {
        super.onStart()
        zbarview_barcode_scanner.startCamera()
    }

    override fun onRestart() {
        super.onRestart()
        if (popupWindow!!.isShowing) {
            checkLightStatus(isLightOn, tvInputBarcodeLight!!, cbInputBarcodeLight!!)
        } else {
            checkLightStatus(isLightOn, tvBarcodeLight!!, cbBarcodeLight!!)
            zbarview_barcode_scanner.showScanRect()
            zbarview_barcode_scanner.startSpot()
        }
    }

    override fun onStop() {
        super.onStop()
        isLightOn = false
        zbarview_barcode_scanner.closeFlashlight()
        zbarview_barcode_scanner.stopCamera()
    }

    override fun onDestroy() {
        super.onDestroy()
        zbarview_barcode_scanner.onDestroy()
    }

    /**
     * 初始化视图
     */
    private fun initView() {
        tvBarcodeLight = findViewById(R.id.tv_barcode_light)
        cbBarcodeLight = findViewById(R.id.cb_barcode_light)

        view = LayoutInflater.from(this).inflate(R.layout.popup_input_barcode, null)
        popupWindow = PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        val mTypeFace = Typeface.createFromAsset(this.assets, "ALTGOT2N.TTF")
        view!!.et_input_barcode.typeface = mTypeFace
        tvInputBarcodeLight = view!!.findViewById(R.id.tv_input_barcode_light)
        cbInputBarcodeLight = view!!.findViewById(R.id.cb_input_barcode_light)
    }

    private fun initShow() {
        if (ConfigConstants.SCAN_BY_PHOTO_ALBUM) {
            tv_barcode_gallery.visibility = View.VISIBLE
        } else {
            tv_barcode_gallery.visibility = View.GONE
        }
        if (ConfigConstants.SCAN_BARCODE) {
            view!!.et_input_barcode.inputType = EditorInfo.TYPE_CLASS_NUMBER
        } else {
            view!!.et_input_barcode.inputType = EditorInfo.TYPE_CLASS_TEXT
        }
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
        // 定位按钮
        tv_barcode_local_position.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                initData()
            }
        })
        //手电筒开关
        ll_btn_light_switch.setOnClickListener {
            isLightOn = !isLightOn
            checkLightStatus(isLightOn, tvBarcodeLight!!, cbBarcodeLight!!)
            if (isLightOn) {
                zbarview_barcode_scanner.openFlashlight()
            } else {
                zbarview_barcode_scanner.closeFlashlight()
            }
        }
        //手动输入条码点击样式
        ll_btn_input_bar_code.setOnTouchListener { _, event ->
            when {
                event.action == MotionEvent.ACTION_DOWN -> {
                    tv_input_bar_code.background = ContextCompat.getDrawable(this, R.drawable.btn_manual1)
                    tv_barcode_input.setTextColor(ContextCompat.getColor(this, R.color.co7_syr))
                }
                event.action == MotionEvent.ACTION_UP -> {
                    tv_input_bar_code.background = ContextCompat.getDrawable(this, R.drawable.btn_manual2)
                    tv_barcode_input.setTextColor(ContextCompat.getColor(this, R.color.co10_syr))
                }
            }
            false
        }
        //手动输入条码弹出popupwindow
        ll_btn_input_bar_code.setOnClickListener {
            zbarview_barcode_scanner.stopSpot()
            popupWindow!!.showAsDropDown(barcode_top_reference, 0, 0)
            checkLightStatus(isLightOn, tvInputBarcodeLight!!, cbInputBarcodeLight!!)
        }
        iv_barcode_back.setOnClickListener {
            finish()
        }
        //popupWindows中打开手电筒点击事件
        view!!.ll_btn_input_barcode_light_switch.setOnClickListener {
            isLightOn = !isLightOn
            checkLightStatus(isLightOn, tvInputBarcodeLight!!, cbInputBarcodeLight!!)
            if (isLightOn) {
                zbarview_barcode_scanner.openFlashlight()
            } else {
                zbarview_barcode_scanner.closeFlashlight()
            }
        }
        view!!.btn_input_barcode_confirm.setOnClickListener {
            val trim = view!!.et_input_barcode.text.toString()
            if (trim == "") {
                ToastUtils.show(this, "请输入条码")
                return@setOnClickListener
            }

//            zbarview_barcode_scanner.closeFlashlight()
//            isLightOn = false
            isStartActivity = true
//            checkLightStatus(isLightOn, view!!.tv_input_barcode_light, view!!.cb_input_barcode_light)
            goToUrl(trim, "input", ConfigConstants.kAppCode)

        }

        view!!.iv_input_barcode_back.setOnClickListener {
            checkLightStatus(isLightOn, tvBarcodeLight!!, cbBarcodeLight!!)
            popupWindow!!.dismiss()
            isStartActivity = false
        }
        popupWindow!!.setOnDismissListener {
            if (!isStartActivity) {
//                finish()
//            } else {
                checkLightStatus(isLightOn, tvBarcodeLight!!, cbBarcodeLight!!)
                zbarview_barcode_scanner.showScanRect()
                zbarview_barcode_scanner.startSpot()
            }
        }
    }

    private fun goToUrl(result: String, codeType: String, type: String) {
//        val mUserSP = getSharedPreferences("UserBean", Context.MODE_PRIVATE)
        when {
            type.contains("ruishang") -> {
                var urlString = intent.getStringExtra(LINK)
                // val link = ConfigConstants.kBaseUrl + "/websites/cav/quan.html?" + "uid=" + mUserSP.getString("userNum", "") + "&code=" + result + "&groupName=" + mUserSP.getString(URLs.kGroupName, "") + "&groupId=" + mUserSP.getString(URLs.kGroupId, "")
                val appendParams = String.format("code=%s", result)
                val splitString = if (urlString.contains("?")) "&" else "?"
                urlString = String.format("%s%s%s", urlString, splitString, appendParams)
                PageLinkManage.pageLink(this, "核销奖券", urlString)
            }
            else -> {
                val intent = Intent(this, ScannerResultActivity::class.java)
                intent.putExtra(CODE_INFO, result)
                intent.putExtra(CODE_TYPE, codeType)
                startActivity(intent)
            }
        }
        popupWindow!!.dismiss()
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.tv_barcode_gallery -> {
                Matisse.from(this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(1)
                        .addFilter(GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                        .gridExpectedSize(resources.getDimensionPixelSize(R.dimen.grid_expected_size))
                        .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                        .thumbnailScale(0.85f)
                        .imageEngine(GlideEngine())
                        .forResult(BarCodeScannerActivity.REQUEST_CODE_CHOOSE)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
    }

    override fun onScanQRCodeOpenCameraError() {
        ToastUtils.show(this@BarCodeScannerActivity, "扫描失败，请重新扫描")
        zbarview_barcode_scanner.postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
    }

    override fun onScanQRCodeSuccess(result: String?) {
        LogUtil.d(TAG, "result:" + result)

        if (result == null) {
            ToastUtils.show(this@BarCodeScannerActivity, "扫描失败，请重新扫描")
            zbarview_barcode_scanner.postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
            return
        }
        when {
            ConfigConstants.SCAN_BARCODE && !result.matches(Regex("^\\d*$")) -> {
                ToastUtils.show(this@BarCodeScannerActivity, "暂只支持条码")
                zbarview_barcode_scanner.postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
                return
            }
        }
        val codeType: String = when {
            ConfigConstants.SCAN_BARCODE -> {
                "barcode"
            }
            else -> {
                "qrcode"
            }
        }
        goToUrl(result, codeType, ConfigConstants.kAppCode)
//        val intent = Intent(this, ScannerResultActivity::class.java)
//        intent.putExtra(URLs.kCodeInfo, result)
//        intent.putExtra(URLs.kCodeType, codeType)
//        startActivity(intent)
//        finish()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        zbarview_barcode_scanner.showScanRect()
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            val mProgressDialog = ProgressDialog.show(this, "稍等", "正在扫描")
            zbarview_barcode_scanner.stopSpot()
//            tv_barcode_scanning.visibility = View.VISIBLE
            val picturePath = ImageUtil.handleImageOnKitKat(Matisse.obtainResult(data)[0], this)
            LogUtil.d(TAG, "picturePath:::" + picturePath)
            object : AsyncTask<Void, Void, String>() {
                override fun doInBackground(vararg params: Void): String {
                    return QRCodeDecoder.syncDecodeQRCode(picturePath)
                }

                override fun onPostExecute(result: String) {
                    mProgressDialog.dismiss()
                    if (TextUtils.isEmpty(result) || "" == result) {
                        ToastUtils.show(this@BarCodeScannerActivity, if (ConfigConstants.SCAN_BARCODE) "未发现条形码" else "未发现二维码")
//                        tv_barcode_scanning.visibility = View.GONE
                        zbarview_barcode_scanner.postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
                    } else {
                        onScanQRCodeSuccess(result)
                    }
                }
            }.execute()
        }
    }

    /**
     * 改变手电筒按钮 开/关 样式
     */
    private fun checkLightStatus(status: Boolean, tv: TextView, checkBox: CheckBox) {
        if (status) {
            tv.setTextColor(ContextCompat.getColor(this, R.color.co7_syr))
            tv.text = "关闭手电筒"
        } else {
            tv.setTextColor(ContextCompat.getColor(this, R.color.co10_syr))
            tv.text = "打开手电筒"
        }
        checkBox.isChecked = status
    }

    private fun getLocation() {
        MapUtil.getInstance(this).getAMapLocation { location ->
            if (null != location) {
                val sb = StringBuffer()
                //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
                if (location.errorCode == 0) {
                    RetrofitUtil.getHttpService(this)
                            .getNearestStores(1, 1.0, "" + location.longitude + "," + location.latitude)
                            .compose(RetrofitUtil.CommonOptions<NearestStoresResult>())
                            .subscribe(object : CodeHandledSubscriber<NearestStoresResult>() {
                                override fun onCompleted() {
                                }

                                override fun onError(apiException: ApiException?) {
                                    tv_barcode_local_position.text = "门店获取失败"
                                }

                                override fun onBusinessNext(data: NearestStoresResult?) {
                                    if (data!!.data!!.isNotEmpty()) {
                                        tv_barcode_local_position.text = data.data!![0].store_name!!
                                        nearestStoreName = data.data!![0].store_name!!
                                        contractStore(data.data!![0].store_name!!)
                                    } else {
                                        tv_barcode_local_position.text = "附近没有定位到门店"
                                    }
                                }

                            })
                    sb.append("经    度    : " + location.longitude + "\n")
                    sb.append("纬    度    : " + location.latitude + "\n")
                } else {
                    //定位失败
                    tv_barcode_local_position.text = "定位失败"
                    sb.append("错误码:" + location.errorCode + "\n")
                    sb.append("错误信息:" + location.errorInfo + "\n")
                    sb.append("错误描述:" + location.locationDetail + "\n")
                }

                //解析定位结果
                val result = sb.toString()
                LogUtil.d("testlog", result)
            } else {
                tv_barcode_local_position.text = "定位失败"
                LogUtil.d("testlog", "定位失败，loc is null")
            }
        }

    }

    private fun contractStore(keyWord: String) {
        try {
            val storeItemDao = OrmDBHelper.getInstance(mContext).storeItemDao
            Observable.create(Observable.OnSubscribe<List<StoreItem>> { subscriber ->
                try {
                    val storeItems = storeItemDao.queryBuilder().where().like("name", keyWord).query()
                    subscriber.onNext(storeItems)
                } catch (e: SQLException) {
                    subscriber.onError(e)
                }
            })
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(object : Subscriber<List<StoreItem>>() {
                        override fun onCompleted() {

                        }

                        override fun onError(e: Throwable) {
                            ToastUtils.show(this@BarCodeScannerActivity, e.message!!)
                        }

                        override fun onNext(storeListResult: List<StoreItem>) {
                            if (storeListResult.isNotEmpty()) {
//                                tv_barcode_local_position.text = storeListResult[0].name
                                val mStoreInfoSP = getSharedPreferences("StoreInfo", Context.MODE_PRIVATE)
                                val mStoreInfoSPEdit = mStoreInfoSP.edit()
                                mStoreInfoSPEdit.putString(STORE, storeListResult[0].name)
                                mStoreInfoSPEdit.putString(STORE_ID, storeListResult[0].id)
                                mStoreInfoSPEdit.apply()
                            } else {
                                if (openPositioningPermissionsFilter) {
                                    tv_barcode_local_position.text = nearestStoreName + "(不在权限范围内)"
                                }
                            }
                        }
                    })
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

}

