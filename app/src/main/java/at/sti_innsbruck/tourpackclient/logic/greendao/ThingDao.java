package at.sti_innsbruck.tourpackclient.logic.greendao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import de.greenrobot.dao.AbstractDao;
import de.greenrobot.dao.Property;
import de.greenrobot.dao.internal.DaoConfig;

import at.sti_innsbruck.tourpackclient.logic.greendao.Thing;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "THING".
*/
public class ThingDao extends AbstractDao<Thing, String> {

    public static final String TABLENAME = "THING";

    /**
     * Properties of entity Thing.<br/>
     * Can be used for QueryBuilder and for referencing column names.
    */
    public static class Properties {
        public final static Property Thing_id = new Property(0, String.class, "thing_id", true, "THING_ID");
        public final static Property Content = new Property(1, String.class, "content", false, "CONTENT");
    };


    public ThingDao(DaoConfig config) {
        super(config);
    }
    
    public ThingDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(SQLiteDatabase db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"THING\" (" + //
                "\"THING_ID\" TEXT PRIMARY KEY NOT NULL UNIQUE ," + // 0: thing_id
                "\"CONTENT\" TEXT);"); // 1: content
    }

    /** Drops the underlying database table. */
    public static void dropTable(SQLiteDatabase db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"THING\"";
        db.execSQL(sql);
    }

    /** @inheritdoc */
    @Override
    protected void bindValues(SQLiteStatement stmt, Thing entity) {
        stmt.clearBindings();
        stmt.bindString(1, entity.getThing_id());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(2, content);
        }
    }

    /** @inheritdoc */
    @Override
    public String readKey(Cursor cursor, int offset) {
        return cursor.getString(offset + 0);
    }    

    /** @inheritdoc */
    @Override
    public Thing readEntity(Cursor cursor, int offset) {
        Thing entity = new Thing( //
            cursor.getString(offset + 0), // thing_id
            cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1) // content
        );
        return entity;
    }
     
    /** @inheritdoc */
    @Override
    public void readEntity(Cursor cursor, Thing entity, int offset) {
        entity.setThing_id(cursor.getString(offset + 0));
        entity.setContent(cursor.isNull(offset + 1) ? null : cursor.getString(offset + 1));
     }
    
    /** @inheritdoc */
    @Override
    protected String updateKeyAfterInsert(Thing entity, long rowId) {
        return entity.getThing_id();
    }
    
    /** @inheritdoc */
    @Override
    public String getKey(Thing entity) {
        if(entity != null) {
            return entity.getThing_id();
        } else {
            return null;
        }
    }

    /** @inheritdoc */
    @Override    
    protected boolean isEntityUpdateable() {
        return true;
    }
    
}
