package com.anye.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import com.ascend.wangfeng.locationbyhand.bean.dbBean.Log;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.internal.DaoConfig;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "LOG".
*/
public class LogDao extends AbstractDao<Log, Long> {

    public static final String TABLENAME = "LOG";

    /**
     * Properties of entity Log.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Mac = new Property(1, String.class, "mac", false, "MAC");
        public final static Property Distance = new Property(2, int.class, "distance", false, "DISTANCE");
        public final static Property Latitude = new Property(3, double.class, "latitude", false, "LATITUDE");
        public final static Property Longitude = new Property(4, double.class, "longitude", false, "LONGITUDE");
        public final static Property Note = new Property(5, String.class, "note", false, "NOTE");
        public final static Property Ltime = new Property(6, long.class, "ltime", false, "LTIME");
    };


    public LogDao(DaoConfig config) {
        super(config);
    }
    
    public LogDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"LOG\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"MAC\" TEXT," + // 1: mac
                "\"DISTANCE\" INTEGER NOT NULL ," + // 2: distance
                "\"LATITUDE\" REAL NOT NULL ," + // 3: latitude
                "\"LONGITUDE\" REAL NOT NULL ," + // 4: longitude
                "\"NOTE\" TEXT," + // 5: note
                "\"LTIME\" INTEGER NOT NULL );"); // 6: ltime
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"LOG\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, Log entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
        stmt.bindLong(3, entity.getDistance());
        stmt.bindDouble(4, entity.getLatitude());
        stmt.bindDouble(5, entity.getLongitude());
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(6, note);
        }
        stmt.bindLong(7, entity.getLtime());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, Log entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(2, mac);
        }
        stmt.bindLong(3, entity.getDistance());
        stmt.bindDouble(4, entity.getLatitude());
        stmt.bindDouble(5, entity.getLongitude());
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(6, note);
        }
        stmt.bindLong(7, entity.getLtime());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public Log readEntity(Cursor cursor, int offset) {
        Log entity = new Log( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // mac
            cursor.getInt(offset + 2), // distance
            cursor.getDouble(offset + 3), // latitude
            cursor.getDouble(offset + 4), // longitude
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // note
            cursor.getLong(offset + 6) // ltime
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, Log entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setMac(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setDistance(cursor.getInt(offset + 2));
        entity.setLatitude(cursor.getDouble(offset + 3));
        entity.setLongitude(cursor.getDouble(offset + 4));
        entity.setNote(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setLtime(cursor.getLong(offset + 6));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(Log entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(Log entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
