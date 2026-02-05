package org.odk.collect.android.dynamicpreload;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * Handles storage for session-based pre-filled data.
 * This data is used to auto-populate form fields when a survey is launched from an external app.
 */
public class SessionDataSQLiteOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "session_data.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_SESSION_DATA = "session_data";
    public static final String COLUMN_KEY = "key";
    public static final String COLUMN_VALUE = "value";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_SESSION_DATA + " (" +
                    COLUMN_KEY + " TEXT PRIMARY KEY, " +
                    COLUMN_VALUE + " TEXT" +
                    ");";

    public SessionDataSQLiteOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SESSION_DATA);
        onCreate(db);
    }

    /**
     * Clears all existing session data and inserts new data from a map.
     */
    public void saveSessionData(Map<String, String> data) {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_SESSION_DATA, null, null);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                ContentValues values = new ContentValues();
                values.put(COLUMN_KEY, entry.getKey());
                values.put(COLUMN_VALUE, entry.getValue());
                db.insert(TABLE_SESSION_DATA, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Retrieves all session data as a Map.
     */
    public Map<String, String> getSessionData() {
        Map<String, String> data = new HashMap<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSION_DATA, new String[]{COLUMN_KEY, COLUMN_VALUE}, null, null, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    data.put(cursor.getString(0), cursor.getString(1));
                } while (cursor.moveToNext());
            }
        } finally {
            cursor.close();
        }
        return data;
    }

    /**
     * Retrieves a single value by key.
     */
    public String getValue(String key) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_SESSION_DATA, new String[]{COLUMN_VALUE}, COLUMN_KEY + "=?", new String[]{key}, null, null, null);
        try {
            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }
        } finally {
            cursor.close();
        }
        return null;
    }
}
