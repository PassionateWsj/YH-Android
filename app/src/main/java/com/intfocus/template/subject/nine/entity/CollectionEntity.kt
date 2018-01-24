package com.intfocus.template.subject.nine.entity

import java.io.Serializable

/**
 * @author liuruilin
 * @data 2017/10/31
 * @describe
 */
class CollectionEntity : Serializable {

    /**
     * id : 1000
     * title : 问题反馈
     * content : [{"name":"App","parts":[{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]},{"name":"PAA","parts":[{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]}]
     * description :
     * created_at : 2017-11-01T00:14:57.000+08:00
     * updated_at : 2017-11-06T18:24:42.000+08:00
     */

    var id: Int = 0
    var title: String? = null
    var description: String? = null
    var created_at: String? = null
    var updated_at: String? = null
    var content: List<ContentBean>? = null

    class ContentBean : Serializable {
        /**
         * name : App
         * parts : [{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]
         */

        var name: String? = null
        var parts: ArrayList<Content>? = null
//
//        class PartsBean : Serializable {
//            /**
//             * type : single_text
//             * key : test_01
//             * is_show : 1
//             * is_list : 1
//             * is_filter : 0
//             * is_must : 1
//             * config : {"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}
//             */
//
//            var type: String? = null
//            var key: String? = null
//            var is_show: Int = 0
//            var is_list: Int = 0
//            var is_filter: Int = 0
//            var is_must: Int = 0
//            var config: String? = null
//        }
    }
}
