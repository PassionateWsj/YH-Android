package com.intfocus.template.model.response.mine_page

import com.google.gson.annotations.SerializedName
import com.intfocus.template.model.response.BaseResult

/**
 * @author liuruilin
 * @data 2017/12/1
 * @describe
 */
class FeedbackList: BaseResult() {

    /**
     * code : 200
     * message : 获取数据列表成功
     * current_page : 0
     * page_size : 15
     * total_page : 15
     * data : [{"id":210,"title":"生意人问题反馈","content":"测试","images":["/images/feedback-210-48cb2c983ce84020baf0b8f7893642df.png"],"replies":[{"id":1,"content":"测试","images":["/images/feedback-reply-d0d653c4ba9e4f01bb4b76d9a1bb389f.png","/images/feedback-reply-65a01382393f4114ae99092370e1191e.png"],"created_at":"2017-11-17 17:30:30"}],"user_num":"13564379606","app_version":"0.0.2","platform":"ios","platform_version":"iphoneos","progress_state":0,"progress_content":null,"publicly":false,"created_at":"2017-08-15 10:55:49"}]
     */
    var current_page: Int = 0
    var page_size: Int = 0
    var total_page: Int = 0
    @SerializedName("data")
    var data: List<Data>? = null
    
    class Data {
        /**
         * id : 210
         * title : 生意人问题反馈
         * content : 测试
         * images : ["/images/feedback-210-48cb2c983ce84020baf0b8f7893642df.png"]
         * replies : [{"id":1,"content":"测试","images":["/images/feedback-reply-d0d653c4ba9e4f01bb4b76d9a1bb389f.png","/images/feedback-reply-65a01382393f4114ae99092370e1191e.png"],"created_at":"2017-11-17 17:30:30"}]
         * user_num : 13564379606
         * app_version : 0.0.2
         * platform : ios
         * platform_version : iphoneos
         * progress_state : 0
         * progress_content : null
         * publicly : false
         * created_at : 2017-08-15 10:55:49
         */

        var id: Int = 0
        var title: String? = null
        var content: String? = null
        var user_num: String? = null
        var app_version: String? = null
        var platform: String? = null
        var platform_version: String? = null
        var progress_state: Int = 0
        var progress_content: Any? = null
        var publicly: Boolean = false
        var created_at: String? = null
        var images: List<String>? = null
        var replies: List<Replies>? = null

        class Replies {
            /**
             * id : 1
             * content : 测试
             * images : ["/images/feedback-reply-d0d653c4ba9e4f01bb4b76d9a1bb389f.png","/images/feedback-reply-65a01382393f4114ae99092370e1191e.png"]
             * created_at : 2017-11-17 17:30:30
             */
            var id: Int = 0
            var content: String? = null
            var created_at: String? = null
            var images: List<String>? = null
        }
    }
}
