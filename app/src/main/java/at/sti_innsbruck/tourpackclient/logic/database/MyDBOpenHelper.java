package at.sti_innsbruck.tourpackclient.logic.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import at.sti_innsbruck.tourpackclient.Constants;
import at.sti_innsbruck.tourpackclient.logic.greendao.DaoMaster;

/**
 * Created by Marcus on 15.05.2016.
 */
public class MyDBOpenHelper extends DaoMaster.OpenHelper {
    public MyDBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
    }

    /**
     * Creates the underlying database with the SQL_CREATE_TABLE queries from
     * the contract classes to create the tables and initialize the data.
     * The onCreate is triggered the first time someone tries to access
     * the database with the getReadableDatabase or
     * getWritableDatabase methods.
     *
     * @param db the database being accessed and that should be created.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i(Constants.DB.TAG, "Create DB-Schema (version "+Integer.toString(DaoMaster.SCHEMA_VERSION)+")");
        super.onCreate(db);
        final SQLiteDatabase fdb = db;

    }


    /**
     * This method must be implemented if your application is upgraded and must
     * include the SQL query to upgrade the database from your old to your new
     * schema.
     *
     * TODO update the schema version in the greendaGenerator
     *
     * @param db the database being upgraded.
     * @param oldVersion the current version of the database before the upgrade.
     * @param newVersion the version of the database after the upgrade.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(Constants.DB.TAG, "Update DB-Schema to version: " + Integer.toString(oldVersion) + "->" + Integer.toString(newVersion));
        switch (oldVersion) {

              /*  case 1:

                    break;
                case 2:
                    db.execSQL(SQL_UPGRADE_2To3);
                    break;
                default:
                //add columns
                 db.execSQL("ALTER TABLE media ADD COLUMN 'FB_ID' TEXT");
                   //update entry
                    UPDATE COMPANY SET ADDRESS = 'Texas' WHERE ID = 6
                    break;
                    */
        }
    }

}