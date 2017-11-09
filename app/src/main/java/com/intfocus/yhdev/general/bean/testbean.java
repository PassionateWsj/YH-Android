package com.intfocus.yhdev.general.bean;

/**
 * Created by liuruilin on 2017/6/12.
 */

public class testbean {

    /**
     * id : 151
     * category : 1
     * group_name : 赛马专题
     * template_id : 2
     * obj_id : 103
     * obj_title : 赛马成绩查询
     * obj_link : /mobile/v2/group/%@/template/2/report/103
     * publicly : false
     * icon : icon-default.png
     * icon_link : http://yonghui-test.idata.mobi/images/icon-default.png
     * params_mapping : {"user_num":"80584332","user_name":"汪浩楠","email":"","mobile":"18701065179","user_id":"10016","status":"true","group_id":"1389","group_name":"大区(北京东二区)门店(清河店)商行(全部)","role_id":"44","role_name":"小店合伙人"}
     * created_at : 2017-08-13T15:09:36.000+08:00
     */

    private int id;
    private String category;
    private String group_name;
    private String template_id;
    private String obj_id;
    private String obj_title;
    private String obj_link;
    private boolean publicly;
    private String icon;
    private String icon_link;

    private String created_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getGroup_name() {
        return group_name;
    }

    public void setGroup_name(String group_name) {
        this.group_name = group_name;
    }

    public String getTemplate_id() {
        return template_id;
    }

    public void setTemplate_id(String template_id) {
        this.template_id = template_id;
    }

    public String getObj_id() {
        return obj_id;
    }

    public void setObj_id(String obj_id) {
        this.obj_id = obj_id;
    }

    public String getObj_title() {
        return obj_title;
    }

    public void setObj_title(String obj_title) {
        this.obj_title = obj_title;
    }

    public String getObj_link() {
        return obj_link;
    }

    public void setObj_link(String obj_link) {
        this.obj_link = obj_link;
    }

    public boolean isPublicly() {
        return publicly;
    }

    public void setPublicly(boolean publicly) {
        this.publicly = publicly;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getIcon_link() {
        return icon_link;
    }

    public void setIcon_link(String icon_link) {
        this.icon_link = icon_link;
    }


    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    private ParamsMappingBean params_mapping;
    public ParamsMappingBean getParams_mapping() {
        return params_mapping;
    }

    public void setParams_mapping(ParamsMappingBean params_mapping) {
        this.params_mapping = params_mapping;
    }
    public static class ParamsMappingBean {
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

        private String user_num;
        private String user_name;
        private String email;
        private String mobile;
        private String user_id;
        private String status;
        private String group_id;
        private String group_name;
        private String role_id;
        private String role_name;

        public String getUser_num() {
            return user_num;
        }

        public void setUser_num(String user_num) {
            this.user_num = user_num;
        }

        public String getUser_name() {
            return user_name;
        }

        public void setUser_name(String user_name) {
            this.user_name = user_name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getUser_id() {
            return user_id;
        }

        public void setUser_id(String user_id) {
            this.user_id = user_id;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getGroup_id() {
            return group_id;
        }

        public void setGroup_id(String group_id) {
            this.group_id = group_id;
        }

        public String getGroup_name() {
            return group_name;
        }

        public void setGroup_name(String group_name) {
            this.group_name = group_name;
        }

        public String getRole_id() {
            return role_id;
        }

        public void setRole_id(String role_id) {
            this.role_id = role_id;
        }

        public String getRole_name() {
            return role_name;
        }

        public void setRole_name(String role_name) {
            this.role_name = role_name;
        }
    }
}
