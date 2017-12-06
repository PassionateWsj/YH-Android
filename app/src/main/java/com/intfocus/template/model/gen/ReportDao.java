package com.intfocus.template.model.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.intfocus.template.model.entity.Report;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "REPORT".
*/
public class ReportDao extends AbstractDao<Report, Long> {

    public static final String TABLENAME = "REPORT";

    /**
     * Properties of entity Report.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uuid = new Property(1, String.class, "uuid", false, "UUID");
        public final static Property Name = new Property(2, String.class, "name", false, "NAME");
        public final static Property Index = new Property(3, int.class, "index", false, "INDEX");
        public final static Property Page_title = new Property(4, String.class, "page_title", false, "PAGE_TITLE");
        public final static Property Type = new Property(5, String.class, "type", false, "TYPE");
        public final static Property Config = new Property(6, String.class, "config", false, "CONFIG");
    }


    public ReportDao(DaoConfig config) {
        super(config);
    }
    
    public ReportDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"REPORT\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"UUID\" TEXT," + // 1: uuid
                "\"NAME\" TEXT," + // 2: name
                "\"INDEX\" INTEGER NOT NULL ," + // 3: index
                "\"PAGE_TITLE\" TEXT," + // 4: page_title
                "\"TYPE\" TEXT," + // 5: type
                "\"CONFIG\" TEXT);"); // 6: config
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"REPORT\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Report entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindLong(4, entity.getIndex());
 
        String page_title = entity.getPage_title();
        if (page_title != null) {
            stmt.bindString(5, page_title);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        String config = entity.getConfig();
        if (config != null) {
            stmt.bindString(7, config);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Report entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String uuid = entity.getUuid();
        if (uuid != null) {
            stmt.bindString(2, uuid);
        }
 
        String name = entity.getName();
        if (name != null) {
            stmt.bindString(3, name);
        }
        stmt.bindLong(4, entity.getIndex());
 
        String page_title = entity.getPage_title();
        if (page_title != null) {
            stmt.bindString(5, page_title);
        }
 
        String type = entity.getType();
        if (type != null) {
            stmt.bindString(6, type);
        }
 
        String config = entity.getConfig();
        if (config != null) {
            stmt.bindString(7, config);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Report readEntity(Cursor cursor, int offset) {
        Report entity = new Report( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // uuid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // name
            cursor.getInt(offset + 3), // index
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // page_title
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // type
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6) // config
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Report entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUuid(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setName(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setIndex(cursor.getInt(offset + 3));
        entity.setPage_title(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setType(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setConfig(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Report entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Report entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(Report entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
