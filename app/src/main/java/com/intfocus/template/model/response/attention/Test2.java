package com.intfocus.template.model.response.attention;

import com.intfocus.template.subject.one.entity.Filter;
import com.intfocus.template.subject.one.entity.SingleValue;

import java.util.List;

/**
 * ****************************************************
 * author jameswong
 * created on: 17/12/18 下午5:08
 * e-mail: PassionateWsj@outlook.com
 * name:
 * desc:
 * ****************************************************
 */
public class Test2 {

    /**
     * code : 200
     * message : success
     * data : {"updated_at":"1513574854","main_data_id":"34562345","main_data_name":"河北一号店","main_attention_data":[{"state":{"color":"2"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"3"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}],"attentioned_data":[{"attention_item_id":"00134567","attention_item_name":"车厘子","attention_item_data":[{"state":{"color":"5"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"4"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"0"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]},{"attention_item_id":"00534567","attention_item_name":"车厘6子","attention_item_data":[{"state":{"color":"5"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"2"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"4"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]}],"attention_list":[{"attention_item_id":"00134567","attention_item_name":"车厘子"},{"attention_item_id":"00133467","attention_item_name":"车厘子1"},{"attention_item_id":"61134567","attention_item_name":"车1234厘子"},{"attention_item_id":"00132467","attention_item_name":"车厘子3"},{"attention_item_id":"00642567","attention_item_name":"车厘子4"},{"attention_item_id":"00534567","attention_item_name":"车厘6子"},{"attention_item_id":"02434567","attention_item_name":"车7厘子"},{"attention_item_id":"00564567","attention_item_name":"车2厘子"}],"filter":{"display":"河北||一号店","data":[{"id":"3456","name":"河北","data":[{"id":"34562345","name":"一号店","data":[]},{"id":"34563567","name":"二号店","data":[]}]}]}}
     */

    private int code;
    private String message;
    private DataBeanXX data;

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

    public DataBeanXX getData() {
        return data;
    }

    public void setData(DataBeanXX data) {
        this.data = data;
    }

    public static class DataBeanXX {
        /**
         * updated_at : 1513574854
         * main_data_id : 34562345
         * main_data_name : 河北一号店
         * main_attention_data : [{"state":{"color":"2"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"3"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]
         * attentioned_data : [{"attention_item_id":"00134567","attention_item_name":"车厘子","attention_item_data":[{"state":{"color":"5"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"4"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"0"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]},{"attention_item_id":"00534567","attention_item_name":"车厘6子","attention_item_data":[{"state":{"color":"5"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"2"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"4"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]}]
         * attention_list : [{"attention_item_id":"00134567","attention_item_name":"车厘子"},{"attention_item_id":"00133467","attention_item_name":"车厘子1"},{"attention_item_id":"61134567","attention_item_name":"车1234厘子"},{"attention_item_id":"00132467","attention_item_name":"车厘子3"},{"attention_item_id":"00642567","attention_item_name":"车厘子4"},{"attention_item_id":"00534567","attention_item_name":"车厘6子"},{"attention_item_id":"02434567","attention_item_name":"车7厘子"},{"attention_item_id":"00564567","attention_item_name":"车2厘子"}]
         * filter : {"display":"河北||一号店","data":[{"id":"3456","name":"河北","data":[{"id":"34562345","name":"一号店","data":[]},{"id":"34563567","name":"二号店","data":[]}]}]}
         */

        private String updated_at;
        private String main_data_id;
        private String main_data_name;
        private Filter filter;
        private List<SingleValue> main_attention_data;
        private List<AttentionedDataBean> attentioned_data;
        private List<AttentionListBean> attention_list;

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

        public List<SingleValue> getMain_attention_data() {
            return main_attention_data;
        }

        public void setMain_attention_data(List<SingleValue> main_attention_data) {
            this.main_attention_data = main_attention_data;
        }

        public Filter getFilter() {
            return filter;
        }

        public void setFilter(Filter filter) {
            this.filter = filter;
        }

        public List<AttentionedDataBean> getAttentioned_data() {
            return attentioned_data;
        }

        public void setAttentioned_data(List<AttentionedDataBean> attentioned_data) {
            this.attentioned_data = attentioned_data;
        }

        public List<AttentionListBean> getAttention_list() {
            return attention_list;
        }

        public void setAttention_list(List<AttentionListBean> attention_list) {
            this.attention_list = attention_list;
        }


        public static class AttentionedDataBean {
            /**
             * attention_item_id : 00134567
             * attention_item_name : 车厘子
             * attention_item_data : [{"state":{"color":"5"},"main_data":{"name":"差异","data":34253,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":30253,"format":"int","percentage":1}},{"state":{"color":"4"},"main_data":{"name":"新增商品数","data":99,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":80,"format":"int","percentage":1}},{"state":{"color":"0"},"main_data":{"name":"缺货商品数","data":102,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":90,"format":"int","percentage":1}},{"state":{"color":"1"},"main_data":{"name":"负毛利商品数量","data":78,"format":"int","percentage":1},"sub_data":{"name":"对比数据","data":73,"format":"int","percentage":1}}]
             */

            private String attention_item_id;
            private String attention_item_name;
            private List<SingleValue> attention_item_data;

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

            public List<SingleValue> getAttention_item_data() {
                return attention_item_data;
            }

            public void setAttention_item_data(List<SingleValue> attention_item_data) {
                this.attention_item_data = attention_item_data;
            }
        }

        public static class AttentionListBean {
            /**
             * attention_item_id : 00134567
             * attention_item_name : 车厘子
             */

            private String attention_item_id;
            private String attention_item_name;

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
        }
    }
}
