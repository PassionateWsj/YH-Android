package com.intfocus.yhdev.scanner

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.CheckBox
import android.widget.PopupWindow
import android.widget.RelativeLayout
import android.widget.TextView
import cn.bingoogolapple.qrcode.core.QRCodeView
import com.intfocus.yhdev.R
import com.intfocus.yhdev.data.response.scanner.NearestStoresResult
import com.intfocus.yhdev.data.response.scanner.StoreItem
import com.intfocus.yhdev.db.OrmDBHelper
import com.intfocus.yhdev.net.ApiException
import com.intfocus.yhdev.net.CodeHandledSubscriber
import com.intfocus.yhdev.net.RetrofitUtil
import com.intfocus.yhdev.util.*
import com.taobao.accs.ACCSManager.mContext
import com.zhihu.matisse.Matisse
import com.zhihu.matisse.MimeType
import com.zhihu.matisse.engine.impl.GlideEngine
import com.zhihu.matisse.filter.Filter
import kotlinx.android.synthetic.main.activity_bar_code_scanner_v2.*
import kotlinx.android.synthetic.main.popup_input_barcode.view.*
import rx.Observable
import rx.Subscriber
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers
import java.sql.SQLException


class BarCodeScannerActivity : AppCompatActivity(), QRCodeView.Delegate, View.OnClickListener {

    companion object {
        val TAG = "hjjzz"
        val REQUEST_CODE_CHOOSE = 1
    }

    private var isLightOn = false
    private var isStartActivity = false

    private var view: View? = null
    private var popupWindow: PopupWindow? = null
    private var tvInputBarcodeLight: TextView? = null
    private var cbInputBarcodeLight: CheckBox? = null
    private var tvBarcodeLight: TextView? = null
    private var cbBarcodeLight: CheckBox? = null
    private var nearestStoreName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bar_code_scanner_v2)

        initData()
        initView()
        initListener()

        zbarview_barcode_scanner.setDelegate(this)
        zbarview_barcode_scanner.startSpot()
    }

    private fun initData() {
        tv_barcode_local_position.text = "正在定位"
        getLocation()
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
        tvBarcodeLight = findViewById(R.id.tv_barcode_light) as TextView
        cbBarcodeLight = findViewById(R.id.cb_barcode_light) as CheckBox

        view = LayoutInflater.from(this).inflate(R.layout.popup_input_barcode, null)
        popupWindow = PopupWindow(view, RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT, true)
        popupWindow!!.setBackgroundDrawable(BitmapDrawable())
        val mTypeFace = Typeface.createFromAsset(this.assets, "ALTGOT2N.TTF")
        view!!.et_input_barcode.typeface = mTypeFace
        tvInputBarcodeLight = view!!.findViewById(R.id.tv_input_barcode_light) as TextView
        cbInputBarcodeLight = view!!.findViewById(R.id.cb_input_barcode_light) as CheckBox
    }

    /**
     * 初始化监听器
     */
    private fun initListener() {
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
                    tv_input_bar_code.background = resources.getDrawable(R.drawable.btn_manual1)
                    tv_barcode_input.setTextColor(resources.getColor(R.color.co7_syr))
                }
                event.action == MotionEvent.ACTION_UP -> {
                    tv_input_bar_code.background = resources.getDrawable(R.drawable.btn_manual2)
                    tv_barcode_input.setTextColor(resources.getColor(R.color.co10_syr))
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
            val intent = Intent(this, ScannerResultActivity::class.java)
            intent.putExtra(URLs.kCodeInfo, trim)
            intent.putExtra(URLs.kCodeType, "input")
            startActivity(intent)
            popupWindow!!.dismiss()

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
            R.id.tv_barcode_local_position -> {
                initData()
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
        Log.i(TAG, "result:" + result)
//        Toast.makeText(this, result, Toast.LENGTH_SHORT).show()
        val toIntOrNull = result!!.toLongOrNull()
        if (toIntOrNull == null) {
            ToastUtils.show(this@BarCodeScannerActivity, "暂时只支持条形码")
            zbarview_barcode_scanner.postDelayed({ zbarview_barcode_scanner.startSpot() }, 2000)
            return
        }
        val intent = Intent(this, ScannerResultActivity::class.java)
        intent.putExtra(URLs.kCodeInfo, result)
        intent.putExtra(URLs.kCodeType, "barcode")
        startActivity(intent)
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
                        ToastUtils.show(this@BarCodeScannerActivity, "未发现条形码")
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
            tv.setTextColor(resources.getColor(R.color.co7_syr))
            tv.text = "关闭手电筒"
        } else {
            tv.setTextColor(resources.getColor(R.color.co10_syr))
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
                                    tv_barcode_local_position.text = "定位失败"
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

    private fun contractStore(keyWord: String) {
        try {
            val storeItemDao = OrmDBHelper.getInstance(mContext).storeItemDao
            Observable.create(Observable.OnSubscribe<List<StoreItem>> { subscriber ->
                try {
                    val storeItems = storeItemDao.queryBuilder().where().like("name", "$keyWord").query()
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

                        }

                        override fun onNext(storeListResult: List<StoreItem>) {
                            if (storeListResult.isNotEmpty()) {
//                                tv_barcode_local_position.text = storeListResult[0].name
                                val mStoreInfoSP = getSharedPreferences("StoreInfo", Context.MODE_PRIVATE)
                                val mStoreInfoSPEdit = mStoreInfoSP.edit()
                                mStoreInfoSPEdit.putString(URLs.kStore, storeListResult[0].name)
                                mStoreInfoSPEdit.putString(URLs.kStoreIds, storeListResult[0].id)
                                mStoreInfoSPEdit.apply()
                            } else {
                                tv_barcode_local_position.text = nearestStoreName + "(不在权限范围内)"
                            }
                        }
                    })
        } catch (e: SQLException) {
            e.printStackTrace()
        }

    }

}

