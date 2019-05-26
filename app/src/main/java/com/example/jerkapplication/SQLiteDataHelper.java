package com.example.jerkapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class SQLiteDataHelper extends SQLiteOpenHelper {

    // データーベースのバージョン
    private static final int DATABASE_VERSION = 1;
    private static final String MAIN_TABLE_NAME = "jerk_table";
    private static final String CREATE_MAIN_TABLE_NAME = "CREATE TABLE " + MAIN_TABLE_NAME + "( id INTEGER PRIMARY KEY, hz int, data_date VARCHAR(255) ) ";
    private static final String DATA_TABLE_NAME = "data_table";
    private static final String CREATE_DATA_TABLE_NAME = "CREATE TABLE " + DATA_TABLE_NAME + " ( id INTEGER PRIMARY KEY, data_date VARCHAR(255), x  DOUBLE, y  DOUBLE, z  DOUBLE ) ";


    SQLiteDataHelper(Context context) {
        super(context, MAIN_TABLE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                CREATE_MAIN_TABLE_NAME
        );
        sqLiteDatabase.execSQL(
                CREATE_DATA_TABLE_NAME
        );
        System.out.println("DB_create!");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
