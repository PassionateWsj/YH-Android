package com.intfocus.template.dashboard.workbox

/**
 * Created by liuruilin on 2017/7/28.
 */
class WorkBoxItem {

    /**
     * id : 151
     * name : 门店赛马成绩
     * report_title : 赛马成绩查询
     * category : 1
     * group_name : 赛马专题
     * link_path : /mobile/v2/group/%@/template/2/report/103
     * template_id : 2
     * obj_id : 103
     * obj_title : 赛马成绩查询
     * obj_link : /mobile/v2/group/%@/template/2/report/103
     * publicly : 0
     * icon : icon-default.png
     * icon_link : http://yonghui-test.idata.mobi/images/icon-default.png
     * group_id : 151
     * health_value : 0
     * group_order : null
     * item_order : null
     * created_at : 2017-08-13T15:09:36.000+08:00
     */

    var id: Int = 0
    var name: String? = null
    var report_title: String? = null
    var category: String? = null
    var group_name: String? = null
    var link_path: String? = null
    var template_id: String? = null
    var obj_id: String? = null
    var obj_title: String? = null
    var obj_link: String? = null
    var publicly: Boolean = false
    var icon: String? = null
    var icon_link: String? = null
    var group_id: Int = 0
    var health_value: Int = 0
    var group_order: Any? = null
    var item_order: Any? = null
    var created_at: String? = null
    var params_mapping: HashMap<String,String>? = null
//    var params_mapping: ParamsMappingBean? = null
    class ParamsMappingBean {
        /**
         * user_num : 80584332
         * user_name : 汪浩楠
         * email :
         * mobile : 18701065179
         * user_id : 10016
         * status : true
         * group_id : 1389
         * group_name : 大区(北京东二区)门店(清河店)商行(全部)
         * role_id : 44
         * role_name : 小店合伙人
         */

        var user_num: String? = null
        var user_name: String? = null
        var email: String? = null
        var mobile: String? = null
        var user_id: String? = null
        var status: String? = null
        var group_id: String? = null
        var group_name: String? = null
        var role_id: String? = null
        var role_name: String? = null
    }

}
