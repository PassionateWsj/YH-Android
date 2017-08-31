package com.intfocus.yhdev.data.response.update

import com.intfocus.yhdev.data.response.BaseResult

/**
 * ****************************************************
 * author: jameswong
 * created on: 17/08/31 下午2:25
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
class UpdateResult : BaseResult() {
    var data: UpdateData? = null

    class UpdateData {
        /**
         * app_version : 1.0.1
         * is_update : 1
         * description : 版本说明版本说明版本说明版本说明版本说明版本说明版本说明版本说明版本说明
         * download_path : www.下载链接.com
         * assets : [{"file_name":"assets","md5":"","is_assets":false},{"file_name":"javascript","md5":"","is_assets":true}]
         */

        var app_version: String? = null
        var is_update: String? = null
        var description: String? = null
        var download_path: String? = null
        var assets: List<AssetsBean>? = null


        class AssetsBean {
            /**
             * file_name : assets
             * md5 :
             * is_assets : false
             */

            var file_name: String? = null
            var md5: String? = null
            var isIs_assets: Boolean = false
        }
    }
}