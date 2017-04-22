package info.androidhive.loginandregistration.helper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class SQLiteHandlerJobType extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerJobType.class.getSimpleName();

    //All static variables
    //Database varsion
    private static final int DATABASE_VERSION = 1;

    //Database name
    private static final String DATABASE_NAME = "android_api";

    //Table name
    private static final String TABLE_JOB_TYPE = "s_job";

    //Table column names
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";

    public SQLiteHandlerJobType(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creating table
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_JOB_TYPE_TABLE = "CREATE TABLE " + TABLE_JOB_TYPE + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NAME + " TEXT" + ")";
        db.execSQL(CREATE_JOB_TYPE_TABLE);

        Log.d(TAG, "Database table created");
    }

    //Upgrading table
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //drop older table if exists
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOB_TYPE);

        //creates the table again
        onCreate(db);
    }

    public List<String> getJobType() {
        List<String> jobTypeList = new ArrayList<>();
        //select query
        String selectQuery = "SELECT name FROM " + TABLE_JOB_TYPE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        //looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                jobTypeList.add(cursor.getString(1));
            } while (cursor.moveToNext());
        }

        //closing connection
        cursor.close();
        db.close();

        //return job types
        return  jobTypeList;
    }
}
