package com.example.homework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBase extends SQLiteOpenHelper {


    public DataBase(Context context) {
        super(context, Constants.DATA_BASE_NAME, null, Constants.DATA_BASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("CREATE TABLE " + Constants.TABLE_SCENARIOS + " (" +
                    Constants.COLUMN_CASE_ID + " INTEGER PRIMARY KEY, " +
                    Constants.COLUMN_ID + " INTEGER, " +
                    Constants.COLUMN_TEXT + " TEXT);");

         db.execSQL("CREATE TABLE " + Constants.TABLE_CASE + " (" +
                    Constants.COLUMN_CASE_TEXT + " TEXT, " +
                    Constants.COLUMN_CASE_IMAGE + " TEXT, " +
                    Constants.TABLE_CASE_COLUMN_ID + " INTEGER PRIMARY KEY, " +
                    Constants.COLUMN_FIRST_ANSWER_TEXT + " TEXT, " +
                    Constants.COLUMN_FIRST_ANSWER_ID + " INTEGER, " +
                    Constants.COLUMN_FIRST_ANSWER_CASE_ID + " INTEGER, " +
                    Constants.COLUMN_SECOND_ANSWER_TEXT + " TEXT, " +
                    Constants.COLUMN_SECOND_ANSWER_ID + " INTEGER, " +
                    Constants.COLUMN_SECOND_ANSWER_CASE_ID + " INTEGER);");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
