package info.androidhive.loginandregistration.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.HashMap;

public class SQLiteHandlerJobs extends SQLiteOpenHelper {

    private static final String TAG = SQLiteHandlerJobs.class.getSimpleName();

    // All Static variables
    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "android_api";

    // Jobs table name
    private static final String TABLE_JOBS = "l_jobs";

    // Jobs table columns names
    private static final String KEY_ID = "id";
    private static final String KEY_SESSION_ID = "sessionid";
    private static final String KEY_CONTRACTOR = "contractor";
    private static final String KEY_CITY = "city";
    private static final String KEY_STREET = "street";
    private static final String KEY_JOB_TYPE = "job_type";
    private static final String KEY_DATE_START = "datestart";

    public SQLiteHandlerJobs(Context context) { super(context, DATABASE_NAME, null, DATABASE_VERSION); }

    // creating tables
    @Override
    public void onCreate (SQLiteDatabase db) {
        String CREATE_JOBS_TABLE = "CREATE TABLE " + TABLE_JOBS +"("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_SESSION_ID + " TEXT,"
                + KEY_CONTRACTOR + " TEXT," + KEY_CITY + " TEXT," + KEY_STREET + " TEXT,"
                + KEY_JOB_TYPE + " TEXT," + KEY_DATE_START + " TEXT" + ")";
        db.execSQL(CREATE_JOBS_TABLE);

        Log.d(TAG, "Database tables created");
    }

    // upgrading database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_JOBS);

        // create tables again
        onCreate(db);
    }

    /**
     * Storing job details in database
     */
    public void addJob(String sessionid, String contractor, String city, String street, String job_type, String datestart) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_SESSION_ID, sessionid);
        values.put(KEY_CONTRACTOR, contractor);
        values.put(KEY_CITY, city);
        values.put(KEY_STREET, street);
        values.put(KEY_JOB_TYPE, job_type);
        values.put(KEY_DATE_START, datestart);

        // insterting row
        long id = db.insert(TABLE_JOBS, null, values);
        db.close(); // closing database connection

        Log.d(TAG, "New job insterted into sqlite: " + id);
    }

    /**
     * Geting jobs data from database
     */
    public HashMap<String, String> getJobDetails() {
        HashMap<String, String> job = new HashMap<String, String>();
        String selectQuery = "SELECT * FROM " + TABLE_JOBS;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // moves to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            job.put("sessionid", cursor.getString(1));
            job.put("contractor", cursor.getString(2));
            job.put("city", cursor.getString(3));
            job.put("street", cursor.getString(4));
            job.put("job_type", cursor.getString(5));
            job.put("datestart", cursor.getString(6));
        }
        cursor.close();
        db.close();
        // return job
        Log.d(TAG, "Fetcing job from Sqlite: " + job.toString());

        return job;
    }

    /**
     * Re create database delete all tables and create them again
     */
    public void deleteJobs() {
        SQLiteDatabase db = this.getWritableDatabase();
        // Delete all rows
        db.delete(TABLE_JOBS, null, null);
        db.close();

        Log.d(TAG, "Deleted all job info from sqlite");
    }
}
