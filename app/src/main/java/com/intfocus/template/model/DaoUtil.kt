package com.intfocus.template.model

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.intfocus.template.model.gen.*

/**
 * @author liuruilin
 * @data 2017/11/5
 * @describe
 */
object DaoUtil {
    private var daoSession: DaoSession? = null
    private var database: SQLiteDatabase? = null

    /**
     * 初始化数据库
     * 建议放在Application中执行
     */
    fun initDataBase(context: Context) {
        //通过DaoMaster的内部类DevOpenHelper，可得到一个SQLiteOpenHelper对象。
        val devOpenHelper = DaoMaster.DevOpenHelper(context, "shengyiplus.db", null) //数据库名称
        database = devOpenHelper.writableDatabase
        val daoMaster = DaoMaster(database)
        daoSession = daoMaster.newSession()
    }

    fun getDaoSession(): DaoSession? = daoSession

    fun getDatabase(): SQLiteDatabase? = database

    fun getSourceDao(): SourceDao = daoSession!!.sourceDao

    fun getCollectionDao(): CollectionDao = daoSession!!.collectionDao

    fun getReportDao(): ReportDao = daoSession!!.reportDao

    fun getPushMsgDao(): PushMsgBeanDao = daoSession!!.pushMsgBeanDao

    fun getAttentionItemDao(): AttentionItemDao = daoSession!!.attentionItemDao

}
