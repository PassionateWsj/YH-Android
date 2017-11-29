package com.intfocus.template.subject.model

/**
 * @author liuruilin
 * @data 2017/11/10
 * @describe
 */
interface ReportModel {
    fun check(url: String): Boolean
    fun available(uuid: String): Boolean
    fun insert(uuid: String, config: String, type: String, index: Int, page: String)
    fun delete(uuid: String)
    fun download(url: String, outputPath: String): HashMap<String, String>
}
