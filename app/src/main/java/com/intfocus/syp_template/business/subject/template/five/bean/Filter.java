package com.intfocus.syp_template.business.subject.template.five.bean;

import java.util.List;

/**
 * Created by CANC on 2017/4/19.
 */

public class Filter {
    public String name;
    public List<FilterItem> items;
    public boolean isSelected = false;//是否打开过滤条件
    public boolean isAllcheck = true;
}
