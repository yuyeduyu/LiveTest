package com.anye.greendao.gen;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.ascend.wangfeng.locationbyhand.bean.dbBean.NoteDo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "NOTE_DO".
*/
public class NoteDoDao extends AbstractDao<NoteDo, Long> {

    public static final String TABLENAME = "NOTE_DO";

    /**
     * Properties of entity NoteDo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Note = new Property(1, String.class, "note", false, "NOTE");
        public final static Property Mac = new Property(2, String.class, "mac", false, "MAC");
        public final static Property Ring = new Property(3, boolean.class, "ring", false, "RING");
    };


    public NoteDoDao(DaoConfig config) {
        super(config);
    }
    
    public NoteDoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"NOTE_DO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"NOTE\" TEXT," + // 1: note
                "\"MAC\" TEXT," + // 2: mac
                "\"RING\" INTEGER NOT NULL );"); // 3: ring
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"NOTE_DO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, NoteDo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(2, note);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(3, mac);
        }
        stmt.bindLong(4, entity.getRing() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, NoteDo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
 
        String note = entity.getNote();
        if (note != null) {
            stmt.bindString(2, note);
        }
 
        String mac = entity.getMac();
        if (mac != null) {
            stmt.bindString(3, mac);
        }
        stmt.bindLong(4, entity.getRing() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public NoteDo readEntity(Cursor cursor, int offset) {
        NoteDo entity = new NoteDo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1), // note
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // mac
            cursor.getShort(offset + 3) != 0 // ring
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, NoteDo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setNote(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
        entity.setMac(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setRing(cursor.getShort(offset + 3) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(NoteDo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(NoteDo entity) {
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