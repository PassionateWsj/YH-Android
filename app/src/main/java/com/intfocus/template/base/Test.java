package com.intfocus.template.base;

import java.util.List;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/15 下午3:38
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class Test {

    /**
     * id : 1000
     * title : 问题反馈
     * content : [{"name":"App","parts":[{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]},{"name":"PAA","parts":[{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]}]
     * description :
     * created_at : 2017-11-01T00:14:57.000+08:00
     * updated_at : 2017-11-06T18:24:42.000+08:00
     */

    private int id;
    private String title;
    private String description;
    private String created_at;
    private String updated_at;
    private List<ContentBean> content;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public List<ContentBean> getContent() {
        return content;
    }

    public void setContent(List<ContentBean> content) {
        this.content = content;
    }

    public static class ContentBean {
        /**
         * name : App
         * parts : [{"type":"single_text","key":"test_01","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}},{"type":"drop_options","key":"test_02","is_show":1,"is_list":1,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题类型","sub_title":"选择你要反馈的问题类型","hint":"","options":["显示错误","数据错误","应用闪退"],"value":"显示错误"}},{"type":"multi_text","key":"test_03","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"element_type":"","title":"问题修改及改进意见","sub_title":"选择你要反馈的问题类型","hint":"请描述您遇到的问题(1-500字)","value":""}},{"type":"upload_images","key":"test_04","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"页面截图(最多3张)","sub_title":"","hint":"","limit":3,"value":["",""]}},{"type":"submit","key":"test_05","is_show":1,"is_list":0,"is_filter":0,"is_must":1,"config":{"title":"","sub_title":"","hint":"","value":"提交"}}]
         */

        private String name;
        private List<PartsBean> parts;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<PartsBean> getParts() {
            return parts;
        }

        public void setParts(List<PartsBean> parts) {
            this.parts = parts;
        }

        public static class PartsBean {
            /**
             * type : single_text
             * key : test_01
             * is_show : 1
             * is_list : 1
             * is_filter : 0
             * is_must : 1
             * config : {"element_type":"","title":"问题反馈标题","sub_title":"","hint":"问题简单描述","value":""}
             */

            private String type;
            private String key;
            private int is_show;
            private int is_list;
            private int is_filter;
            private int is_must;
            private ConfigBean config;

            public String getType() {
                return type;
            }

            public void setType(String type) {
                this.type = type;
            }

            public String getKey() {
                return key;
            }

            public void setKey(String key) {
                this.key = key;
            }

            public int getIs_show() {
                return is_show;
            }

            public void setIs_show(int is_show) {
                this.is_show = is_show;
            }

            public int getIs_list() {
                return is_list;
            }

            public void setIs_list(int is_list) {
                this.is_list = is_list;
            }

            public int getIs_filter() {
                return is_filter;
            }

            public void setIs_filter(int is_filter) {
                this.is_filter = is_filter;
            }

            public int getIs_must() {
                return is_must;
            }

            public void setIs_must(int is_must) {
                this.is_must = is_must;
            }

            public ConfigBean getConfig() {
                return config;
            }

            public void setConfig(ConfigBean config) {
                this.config = config;
            }

            public static class ConfigBean {
                /**
                 * element_type :
                 * title : 问题反馈标题
                 * sub_title :
                 * hint : 问题简单描述
                 * value :
                 */

                private String element_type;
                private String title;
                private String sub_title;
                private String hint;
                private String value;

                public String getElement_type() {
                    return element_type;
                }

                public void setElement_type(String element_type) {
                    this.element_type = element_type;
                }

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getSub_title() {
                    return sub_title;
                }

                public void setSub_title(String sub_title) {
                    this.sub_title = sub_title;
                }

                public String getHint() {
                    return hint;
                }

                public void setHint(String hint) {
                    this.hint = hint;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }
            }
        }
    }
}
