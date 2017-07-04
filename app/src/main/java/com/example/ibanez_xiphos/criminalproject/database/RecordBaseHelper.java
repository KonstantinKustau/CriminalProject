package com.example.ibanez_xiphos.criminalproject.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class RecordBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "recordBase.db";

    public RecordBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL("create table " + RecordDbSchema.RecordTable.NAME + "(" + "_id integer primary key autoincrement, " +
        RecordDbSchema.RecordTable.Cols.UUID + "," +
        RecordDbSchema.RecordTable.Cols.TITLE + "," +
        RecordDbSchema.RecordTable.Cols.DATE + "," +
        RecordDbSchema.RecordTable.Cols.SOLVED + "," +
        RecordDbSchema.RecordTable.Cols.CONTACT + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){

    }
}
