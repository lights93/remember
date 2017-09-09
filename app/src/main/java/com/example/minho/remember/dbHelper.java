package com.example.minho.remember;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class dbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "myapp.db";
    private static final int DATABASE_VERSION = 1;

    public dbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Log.i("Test","super");
    }
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE myapp (path TEXT, type TEXT, date INTEGER, time INTEGER PRIMARY KEY);");
        Log.i("Test","Create");
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS myapp");
        Log.i("Test","Delete");

        onCreate(db);
    }
}
