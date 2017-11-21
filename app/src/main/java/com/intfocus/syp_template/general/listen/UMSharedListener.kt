package com.intfocus.syp_template.general.listen

import android.util.Log
import com.umeng.socialize.UMShareListener
import com.umeng.socialize.bean.SHARE_MEDIA

/**
 * @author liuruilin
 * @data 2017/11/20
 * @describe
 */
class UMSharedListener: UMShareListener {
    override fun onStart(platform: SHARE_MEDIA) {
        //分享开始的回调
    }

    override fun onResult(platform: SHARE_MEDIA) {
        Log.d("plat", "platform" + platform)
    }

    override fun onError(platform: SHARE_MEDIA, t: Throwable?) {
        if (t != null) {
            Log.d("throw", "throw:" + t.message)
        }
    }

    override fun onCancel(platform: SHARE_MEDIA) {
        Log.d("throw", "throw:" + " 分享取消了")
    }
}
