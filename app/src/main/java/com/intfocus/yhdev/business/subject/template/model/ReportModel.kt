package com.intfocus.yhdev.business.subject.template.model

/**
 * @author liuruilin
 * @data 2017/11/10
 * @describe
 */
interface ReportModel {
    fun update(url: String): Boolean
    fun available(uuid: String): Boolean
    fun insertDb(uuid: String, config: String)
}