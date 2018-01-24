package com.intfocus.template.model.response.attention;

import java.util.List;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:09
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class Test3 {

    /**
     * code : 200
     * message : success
     * data : {"updated_at":"1512574854","main_data_id":"9555","main_data_name":"高淳悦达广场店","attention_data":[{"attention_item_id":"00134567","attention_item_name":"车厘子","attention_item_data":[{"title":"差异","value":"+34,253","rate":"62.5%","colorIndex":"2"},{"title":"新增商品数","value":"99","rate":"-0.5%","colorIndex":"1"},{"title":"缺货商品数","value":"102","rate":"10%","colorIndex":"3"},{"title":"负毛利商品数量","value":"78","rate":"9.7%","colorIndex":"0"}]},{"attention_item_id":"04334537","attention_item_name":"车厘子1","attention_item_data":[{"title":"差异","value":"+51,253","rate":"32.5%","colorIndex":"2"},{"title":"新增商品数","value":"79","rate":"-1.5%","colorIndex":"3"},{"title":"缺货商品数","value":"102","rate":"20%","colorIndex":"0"},{"title":"负毛利商品数量","value":"48","rate":"19.7%","colorIndex":"1"}]},{"attention_item_id":"00534567","attention_item_name":"车厘6子","attention_item_data":[{"title":"差异","value":"+51,253","rate":"32.5%","colorIndex":"2"},{"title":"新增商品数","value":"79","rate":"-1.5%","colorIndex":"3"},{"title":"缺货商品数","value":"102","rate":"20%","colorIndex":"0"},{"title":"负毛利商品数量","value":"48","rate":"19.7%","colorIndex":"1"}]}]}
     */

    private int code;
    private String message;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * updated_at : 1512574854
         * main_data_id : 9555
         * main_data_name : 高淳悦达广场店
         * attention_data : [{"attention_item_id":"00134567","attention_item_name":"车厘子","attention_item_data":[{"title":"差异","value":"+34,253","rate":"62.5%","colorIndex":"2"},{"title":"新增商品数","value":"99","rate":"-0.5%","colorIndex":"1"},{"title":"缺货商品数","value":"102","rate":"10%","colorIndex":"3"},{"title":"负毛利商品数量","value":"78","rate":"9.7%","colorIndex":"0"}]},{"attention_item_id":"04334537","attention_item_name":"车厘子1","attention_item_data":[{"title":"差异","value":"+51,253","rate":"32.5%","colorIndex":"2"},{"title":"新增商品数","value":"79","rate":"-1.5%","colorIndex":"3"},{"title":"缺货商品数","value":"102","rate":"20%","colorIndex":"0"},{"title":"负毛利商品数量","value":"48","rate":"19.7%","colorIndex":"1"}]},{"attention_item_id":"00534567","attention_item_name":"车厘6子","attention_item_data":[{"title":"差异","value":"+51,253","rate":"32.5%","colorIndex":"2"},{"title":"新增商品数","value":"79","rate":"-1.5%","colorIndex":"3"},{"title":"缺货商品数","value":"102","rate":"20%","colorIndex":"0"},{"title":"负毛利商品数量","value":"48","rate":"19.7%","colorIndex":"1"}]}]
         */

        private String updated_at;
        private String main_data_id;
        private String main_data_name;
        private List<AttentionDataBean> attention_data;

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

        public List<AttentionDataBean> getAttention_data() {
            return attention_data;
        }

        public void setAttention_data(List<AttentionDataBean> attention_data) {
            this.attention_data = attention_data;
        }

        public static class AttentionDataBean {
            /**
             * attention_item_id : 00134567
             * attention_item_name : 车厘子
             * attention_item_data : [{"title":"差异","value":"+34,253","rate":"62.5%","colorIndex":"2"},{"title":"新增商品数","value":"99","rate":"-0.5%","colorIndex":"1"},{"title":"缺货商品数","value":"102","rate":"10%","colorIndex":"3"},{"title":"负毛利商品数量","value":"78","rate":"9.7%","colorIndex":"0"}]
             */

            private String attention_item_id;
            private String attention_item_name;
            private List<AttentionItemDataBean> attention_item_data;

            public String getAttention_item_id() {
                return attention_item_id;
            }

            public void setAttention_item_id(String attention_item_id) {
                this.attention_item_id = attention_item_id;
            }

            public String getAttention_item_name() {
                return attention_item_name;
            }

            public void setAttention_item_name(String attention_item_name) {
                this.attention_item_name = attention_item_name;
            }

            public List<AttentionItemDataBean> getAttention_item_data() {
                return attention_item_data;
            }

            public void setAttention_item_data(List<AttentionItemDataBean> attention_item_data) {
                this.attention_item_data = attention_item_data;
            }

            public static class AttentionItemDataBean {
                /**
                 * title : 差异
                 * value : +34,253
                 * rate : 62.5%
                 * colorIndex : 2
                 */

                private String title;
                private String value;
                private String rate;
                private String colorIndex;

                public String getTitle() {
                    return title;
                }

                public void setTitle(String title) {
                    this.title = title;
                }

                public String getValue() {
                    return value;
                }

                public void setValue(String value) {
                    this.value = value;
                }

                public String getRate() {
                    return rate;
                }

                public void setRate(String rate) {
                    this.rate = rate;
                }

                public String getColorIndex() {
                    return colorIndex;
                }

                public void setColorIndex(String colorIndex) {
                    this.colorIndex = colorIndex;
                }
            }
        }
    }
}
