package com.intfocus.yhdev.bean

import com.intfocus.yhdev.dashboard.mine.bean.PushMessageBean
import java.io.Serializable

/**
 * Created by liuruilin on 2017/7/14.
 */
class PushMessage :Serializable{
    /**{
     *  "msg_id":"ul42751150157385157100",
     *  "display_type":"notification",
     *  "random_min":0,
     *  "body":{
     *      "ticker":"消息中心(7851)",
     *      "title":"消息中心(7851)",
     *      "text":"野竹分青霭，飞泉挂碧峰",
     *      "sound":"default",
     *      "play_sound":"true",
     *      "play_lights":"true",
     *      "play_vibrate":"true",
     *      "after_open":"go_custom",
     *      "custom":{
     *          "type":"report",
     *          "title":"第二集群销售额",
     *          "url":"/mobile/v2/group/%@/template/4/report/8",
     *          "obj_id":8,
     *          "obj_type":1,
     *          "debug_timestamp":"2017-08-01 15:50:51 +0800"
     *      }
     *  }
     *}
     */

    /**
     * msg_id : ul42751150157385157100
     * display_type : notification
     * random_min : 0
     * body : {"ticker":"消息中心(7851)","title":"消息中心(7851)","text":"野竹分青霭，飞泉挂碧峰","sound":"default","play_sound":"true","play_lights":"true","play_vibrate":"true","after_open":"go_custom","custom":{"type":"report","title":"第二集群销售额","url":"/mobile/v2/group/%@/template/4/report/8","obj_id":8,"obj_type":1,"debug_timestamp":"2017-08-01 15:50:51 +0800"}}
     */

    var msg_id: String? = null
    var display_type: String? = null
    var random_min: Int = 0
    var body: BodyBean? = null

    class BodyBean :Serializable{
        /**
         * ticker : 消息中心(7851)
         * title : 消息中心(7851)
         * text : 野竹分青霭，飞泉挂碧峰
         * sound : default
         * play_sound : true
         * play_lights : true
         * play_vibrate : true
         * after_open : go_custom
         * custom : {"type":"report","title":"第二集群销售额","url":"/mobile/v2/group/%@/template/4/report/8","obj_id":8,"obj_type":1,"debug_timestamp":"2017-08-01 15:50:51 +0800"}
         */

        var ticker: String? = null
        var title: String? = null
        var text: String? = null
        var sound: String? = null
        var play_sound: String? = null
        var play_lights: String? = null
        var play_vibrate: String? = null
        var after_open: String? = null
        var custom: PushMessageBean? = null

//        class CustomBean {
//            /**
//             * type : report
//             * title : 第二集群销售额
//             * url : /mobile/v2/group/%@/template/4/report/8
//             * obj_id : 8
//             * obj_type : 1
//             * debug_timestamp : 2017-08-01 15:50:51 +0800
//             */
//
//            var type: String? = null
//            var title: String? = null
//            var url: String? = null
//            var obj_id: Int = 0
//            var obj_type: Int = 0
//            var debug_timestamp: String? = null
//        }
    }
}

