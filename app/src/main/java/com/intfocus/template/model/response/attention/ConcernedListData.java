package com.intfocus.template.model.response.attention;

import java.util.List;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/26 下午1:54
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class ConcernedListData {

    /**
     * updated_at : 1513574854
     * main_data_id : 34563567
     * main_data_name : 河北||二号店
     * title : 我关注的水果
     * concerned_data : [{"concern_item_id":"12341234","concern_item_name":"车厘子","concern_item_data":[{"real_time":true,"real_time_api":"","state":{"color":"2"},"main_data":{"name":"差异","data":343,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":2345,"format":"int","percentage":1}},{"real_time":false,"state":{"color":"2"},"main_data":{"name":"新增商品数","data":923459,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}}]},{"concern_item_id":"00534567","concern_item_name":"车厘6子","concern_item_data":[{"real_time":true,"state":{"color":"34"},"main_data":{"name":"差异","data":6345,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":32452345,"format":"int","percentage":1}},{"real_time":false,"state":{"color":"345"},"main_data":{"name":"新增商品数","data":234,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}}]}]
     */

    private String updated_at;
    private String main_data_id;
    private String main_data_name;
    private String title;
    private List<ConcernedDataBean> concerned_data;

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getMain_data_id() {
        return main_data_id;
    }

    public void setMain_data_id(String main_data_id) {
        this.main_data_id = main_data_id;
    }

    public String getMain_data_name() {
        return main_data_name;
    }

    public void setMain_data_name(String main_data_name) {
        this.main_data_name = main_data_name;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<ConcernedDataBean> getConcerned_data() {
        return concerned_data;
    }

    public void setConcerned_data(List<ConcernedDataBean> concerned_data) {
        this.concerned_data = concerned_data;
    }

    public static class ConcernedDataBean {
        /**
         * concern_item_id : 12341234
         * concern_item_name : 车厘子
         * concern_item_data : [{"real_time":true,"real_time_api":"","state":{"color":"2"},"main_data":{"name":"差异","data":343,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":2345,"format":"int","percentage":1}},{"real_time":false,"state":{"color":"2"},"main_data":{"name":"新增商品数","data":923459,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}}]
         */

        private String concern_item_id;
        private String concern_item_name;
        private List<ConcernItemDataBean> concern_item_data;

        public String getConcern_item_id() {
            return concern_item_id;
        }

        public void setConcern_item_id(String concern_item_id) {
            this.concern_item_id = concern_item_id;
        }

        public String getConcern_item_name() {
            return concern_item_name;
        }

        public void setConcern_item_name(String concern_item_name) {
            this.concern_item_name = concern_item_name;
        }

        public List<ConcernItemDataBean> getConcern_item_data() {
            return concern_item_data;
        }

        public void setConcern_item_data(List<ConcernItemDataBean> concern_item_data) {
            this.concern_item_data = concern_item_data;
        }

        public static class ConcernItemDataBean {
            /**
             * real_time : true
             * real_time_api :
             * state : {"color":"2"}
             * main_data : {"name":"差异","data":343,"format":"int","percentage":1}
             * sub_data : {"name":"对比数据","data":2345,"format":"int","percentage":1}
             */

            private boolean real_time;
            private String real_time_api;
            private StateBean state;
            private MainDataBean main_data;
            private SubDataBean sub_data;

            public boolean isReal_time() {
                return real_time;
            }

            public void setReal_time(boolean real_time) {
                this.real_time = real_time;
            }

            public String getReal_time_api() {
                return real_time_api;
            }

            public void setReal_time_api(String real_time_api) {
                this.real_time_api = real_time_api;
            }

            public StateBean getState() {
                return state;
            }

            public void setState(StateBean state) {
                this.state = state;
            }

            public MainDataBean getMain_data() {
                return main_data;
            }

            public void setMain_data(MainDataBean main_data) {
                this.main_data = main_data;
            }

            public SubDataBean getSub_data() {
                return sub_data;
            }

            public void setSub_data(SubDataBean sub_data) {
                this.sub_data = sub_data;
            }

            public static class StateBean {
                /**
                 * color : 2
                 */

                private String color;

                public String getColor() {
                    return color;
                }

                public void setColor(String color) {
                    this.color = color;
                }
            }

            public static class MainDataBean {
                /**
                 * name : 差异
                 * data : 343
                 * format : int
                 * percentage : 1
                 */

                private String name;
                private int data;
                private String format;
                private int percentage;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getData() {
                    return data;
                }

                public void setData(int data) {
                    this.data = data;
                }

                public String getFormat() {
                    return format;
                }

                public void setFormat(String format) {
                    this.format = format;
                }

                public int getPercentage() {
                    return percentage;
                }

                public void setPercentage(int percentage) {
                    this.percentage = percentage;
                }
            }

            public static class SubDataBean {
                /**
                 * name : 对比数据
                 * data : 2345
                 * format : int
                 * percentage : 1
                 */

                private String name;
                private int data;
                private String format;
                private int percentage;

                public String getName() {
                    return name;
                }

                public void setName(String name) {
                    this.name = name;
                }

                public int getData() {
                    return data;
                }

                public void setData(int data) {
                    this.data = data;
                }

                public String getFormat() {
                    return format;
                }

                public void setFormat(String format) {
                    this.format = format;
                }

                public int getPercentage() {
                    return percentage;
                }

                public void setPercentage(int percentage) {
                    this.percentage = percentage;
                }
            }
        }
    }
}
