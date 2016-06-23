package at.sti_innsbruck.tourpackclient.logic.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import at.sti_innsbruck.tourpackclient.Constants;
import at.sti_innsbruck.tourpackclient.logic.greendao.DaoMaster;
import at.sti_innsbruck.tourpackclient.logic.greendao.DaoSession;

/**
 * Created by Marcus on 15.04.2016.
 */
public class DBHelper {
    private SQLiteDatabase _db = null;
    private DaoSession _session = null;
    private Context context = null;

    private DaoMaster getMaster() {
        if (_db == null) {
            _db = getDatabase(Constants.DB.NAME, false);
        }
        return new DaoMaster(_db);
    }

    public void closeDB(){
        if(_db != null){
            _db.close();
        }
    }

    public DaoSession getSession(Context context, boolean newSession) {
        this.context = context;
        if (newSession) {
            return getMaster().newSession();
        }
        if (_session == null) {
            _session = getMaster().newSession();
        }
        return _session;
    }

    private synchronized SQLiteDatabase getDatabase(String name, boolean readOnly) {
        String s = "getDB(" + name + ",readonly=" + (readOnly ? "true" : "false") + ")";
        try {
            readOnly = false;
            Log.i(Constants.DB.TAG, s);
            SQLiteOpenHelper helper = new MyDBOpenHelper(context, name, null);
            if (readOnly) {
                return helper.getReadableDatabase();
            } else {
                return helper.getWritableDatabase();
            }
        } catch (Exception ex) {
            Log.e(Constants.DB.TAG, s, ex);
            return null;
        } catch (Error err) {
            Log.e(Constants.DB.TAG, s, err);
            return null;
        }
    }

}
