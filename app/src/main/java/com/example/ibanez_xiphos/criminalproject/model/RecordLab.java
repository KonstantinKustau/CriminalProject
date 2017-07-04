package com.example.ibanez_xiphos.criminalproject.model;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.example.ibanez_xiphos.criminalproject.database.RecordBaseHelper;
import com.example.ibanez_xiphos.criminalproject.database.RecordCursorWrapper;
import com.example.ibanez_xiphos.criminalproject.database.RecordDbSchema.RecordTable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecordLab {
    private static RecordLab sRecordLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static RecordLab get(Context context){
        if (sRecordLab == null){
            sRecordLab = new RecordLab(context);
        }
        return sRecordLab;
    }

    private RecordLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new RecordBaseHelper(mContext).getWritableDatabase();
    }

    public void updateRecord(Record r){
        String uuidString = r.getId().toString();
        ContentValues values = getContentValues(r);
        mDatabase.update(RecordTable.NAME, values,RecordTable.Cols.UUID + " = ?",
                new String[]{uuidString});
    }

    public void addRecord(Record r) {
        ContentValues values = getContentValues(r);
        mDatabase.insert(RecordTable.NAME, null, values);
    }

    public void removeRecord(Record r){
        mDatabase.delete(RecordTable.NAME, RecordTable.Cols.UUID + " = ?", new String[]{r.getId().toString()});
        File photoFile = getPhotoFile(r);
        if (photoFile != null && photoFile.exists()){
            photoFile.delete();
        }
    }

    public List<Record> getRecords(){
        List<Record> records = new ArrayList<Record>();
        RecordCursorWrapper cursor = queryRecords(null, null);

        try{
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                records.add(cursor.getRecord());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }
        return records;
    }

    public Record getRecord(UUID id){
        RecordCursorWrapper cursor = queryRecords(RecordTable.Cols.UUID + " = ?", new String[]{id.toString()});
        try{
            if (cursor.getCount() == 0){
                return null;
            }

            cursor.moveToFirst();
            return cursor.getRecord();
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Record record){
        ContentValues values = new ContentValues();
        values.put(RecordTable.Cols.UUID, record.getId().toString());
        values.put(RecordTable.Cols.TITLE, record.getTitle());
        values.put(RecordTable.Cols.DATE, record.getDate().getTime());
        values.put(RecordTable.Cols.SOLVED, record.isSolved() ? 1 : 0);
        values.put(RecordTable.Cols.CONTACT, record.getContact());

        return values;
    }

    public File getPhotoFile(Record record){
        File externalFilesDir = mContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if (externalFilesDir == null){
            return null;
        }

        return new File(externalFilesDir, record.getPhotoFilename());
    }

    private RecordCursorWrapper queryRecords(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(RecordTable.NAME, null, whereClause, whereArgs, null, null, null);
        return new RecordCursorWrapper(cursor);
    }
}
