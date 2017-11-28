package com.intfocus.syp_template.model.entity;

import com.intfocus.syp_template.model.response.filter.MenuItem;

import java.util.List;

/**
 * @author liuruilin
 * @data 2017/11/28
 * @describe
 */

public class GsonCoverBean {


    /**
     * type : location
     * display : 纽约|一号店
     * data : [{"id":"","name":"纽约","data":[{"id":"","name":"一号店","data":[]},{"id":"","name":"二号店","data":[]}]}]
     */

    private String type;
    private String display;
    private List<MenuItem> data;

    public static class MenuItem {
        /**
         * id :
         * name : 纽约
         * data : [{"id":"","name":"一号店","data":[]},{"id":"","name":"二号店","data":[]}]
         */

        private String id;
        private String name;
        private List<MenuItem> data;
    }
}
