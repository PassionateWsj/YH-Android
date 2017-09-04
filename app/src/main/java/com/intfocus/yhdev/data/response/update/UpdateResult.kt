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
    /**
     * data : {"title":"无发布的版本","version":"0.0.0","build":1000,"download_url":"","upgrade_level":-1,"description":"无发布的版本","assets":[{"file_name":"assets","md5":"e4acc7fbd00fc107c756eebaa365ac00","is_assets":false},{"file_name":"loading","md5":"8bd5c6a91d38848d3160e6c8a462b852","is_assets":true},{"file_name":"fonts","md5":"5901960c857600316c3d141401c3af08","is_assets":true},{"file_name":"icons","md5":"7afa625cca643d01a6b12d80a19d4756","is_assets":true},{"file_name":"images","md5":"65266455bea40469dcb9f022f63ce769","is_assets":true},{"file_name":"javascripts","md5":"e55b643bbde61075119fb25ffc9c8b5d","is_assets":true},{"file_name":"stylesheets","md5":"923b05c441a8cef0daf32ed392aee633","is_assets":true}]}
     * code : 200
     * message : default successfully
     */

    var data: UpdateData? = null

    class UpdateData {
        /**
         * title : 无发布的版本
         * version : 0.0.0
         * build : 1000
         * download_url :
         * upgrade_level : -1
         * description : 无发布的版本
         * assets : [{"file_name":"assets","md5":"e4acc7fbd00fc107c756eebaa365ac00","is_assets":false},{"file_name":"loading","md5":"8bd5c6a91d38848d3160e6c8a462b852","is_assets":true},{"file_name":"fonts","md5":"5901960c857600316c3d141401c3af08","is_assets":true},{"file_name":"icons","md5":"7afa625cca643d01a6b12d80a19d4756","is_assets":true},{"file_name":"images","md5":"65266455bea40469dcb9f022f63ce769","is_assets":true},{"file_name":"javascripts","md5":"e55b643bbde61075119fb25ffc9c8b5d","is_assets":true},{"file_name":"stylesheets","md5":"923b05c441a8cef0daf32ed392aee633","is_assets":true}]
         */

        var title: String? = null
        var version: String? = null
        var build: Int = 0
        var download_url: String? = null
        var upgrade_level: Int = 0
        var description: String? = null
        var assets: List<AssetsBean>? = null

        class AssetsBean {
            /**
             * file_name : assets
             * md5 : e4acc7fbd00fc107c756eebaa365ac00
             * is_assets : false
             */

            var file_name: String? = null
            var md5: String? = null
            var isIs_assets: Boolean = false
        }
    }
}