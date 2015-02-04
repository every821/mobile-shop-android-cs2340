package com.example.shoppingwithfriends;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class MyDB{

    private MyDatabaseHelper dbHelper;

    private SQLiteDatabase database;

    public final static String TABLE="MyEmployees"; // name of table

    public final static String ID = "id"; // id value for employee
    public final static String COLOR = "color";
    public final static String NAME = "name";  // name of employee
    public final static String USERNAME = "username";
    /**
     *
     * @param context
     */
    public MyDB(Context context){
        dbHelper = new MyDatabaseHelper(context);
    }

    public void open() {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createRecords(String id, String username, int color, String name){
        ContentValues values = new ContentValues();
        values.put(ID, id);
        values.put(USERNAME, username);
        values.put(COLOR, color);
        values.put(NAME, name);
        return database.insert(TABLE, null, values);
    }

    public Cursor selectRecords() {
        String[] cols = new String[] {ID, USERNAME, COLOR, NAME};
        Cursor mCursor = database.query(true, TABLE, cols, null, null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor; // iterate to get each value.
    }
}