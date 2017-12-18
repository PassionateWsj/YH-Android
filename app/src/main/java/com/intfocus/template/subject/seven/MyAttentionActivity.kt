package com.intfocus.template.subject.seven

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.intfocus.template.R
import com.intfocus.template.constant.Params.STORE
import com.intfocus.template.constant.Params.STORE_ID
import com.intfocus.template.model.response.attention.Test2
import com.intfocus.template.scanner.StoreSelectorActivity
import com.intfocus.template.ui.BaseActivity
import kotlinx.android.synthetic.main.actvity_my_attention.*

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 上午11:14
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class MyAttentionActivity : BaseActivity(), MyAttentionContract.View {

    companion object {
        val REQUEST_CODE_CHOOSE = 0
    }

    override lateinit var presenter: MyAttentionContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.actvity_my_attention)

        MyAttentionPresenter(MyAttentionModelImpl.getInstance(), this)
        presenter.loadData("12341234123")

    }

    override fun onUpdateData(data: Test2) {
        if (data.data.main_attention_data.isNotEmpty()) {

        }
    }

    fun menuOnClicked(view: View) {
        when(view.id) {
            R.id.iv_attention->{

            }
            R.id.iv_attention_filter->{

            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == StoreSelectorActivity.RESULT_CODE_CHOOSE) {
            if (data != null) {
                val mStoreName = data.getStringExtra(STORE)
                val mStoreId = data.getStringExtra(STORE_ID)
//               val mStoreInfoSPEdit.putString(STORE, mStoreName)
//               val mStoreInfoSPEdit.putString(STORE_ID, mStoreId)
//               val mStoreInfoSPEdit.apply()

                tv_banner_title.text = mStoreName
//                anim_loading.visibility = View.VISIBLE
//                model.requestData(barcode, mStoreId)
            }
        }
    }
}