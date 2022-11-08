package com.example.messaginghub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DBhelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME="University";
    public static final int DATABASE_VERSION=1;
    public static DBhelper instance;

    private DBhelper(@Nullable Context context) {
        super(context,DATABASE_NAME,null,DATABASE_VERSION);
    }

    public static DBhelper getInstance(Context context){
        if (instance==null){
            instance=new DBhelper(context);
        }
        return instance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(ProfileDatabaseModel.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         if(oldVersion!=newVersion){
             db.execSQL(ProfileDatabaseModel.DROP_TABLE);
             db.execSQL(ProfileDatabaseModel.CREATE_TABLE);
         }
    }
    public Boolean add_data(ProfileDatabaseModel data){
      SQLiteDatabase database=instance.getWritableDatabase();
        ContentValues contentValues=new ContentValues();
        contentValues.put(ProfileDatabaseModel.KEY_USERNAME,data.getUsername());
        contentValues.put(ProfileDatabaseModel.KEY_PHONE_NO,data.getPhoneno());
        contentValues.put(ProfileDatabaseModel.KEY_IMGID,data.getImgid());
        database.insert(ProfileDatabaseModel.TABLE_NAME,null,contentValues);
        return true;
    }
    public ProfileDatabaseModel fetch_data(){
        ProfileDatabaseModel data=new ProfileDatabaseModel();
        SQLiteDatabase sqLiteDatabase=getReadableDatabase();
        Cursor cursor=sqLiteDatabase.rawQuery(ProfileDatabaseModel.SELECT_DATA,null);
        cursor.moveToFirst();
          cursor.moveToLast();
          int index= cursor.getColumnIndex(ProfileDatabaseModel.KEY_USERNAME);
           data.setUsername(cursor.getString(index));
           index=cursor.getColumnIndex(ProfileDatabaseModel.KEY_PHONE_NO);
           data.setPhoneno(cursor.getString(index));
        index=cursor.getColumnIndex(ProfileDatabaseModel.KEY_IMGID);
        data.setImgid(cursor.getString(index));
        return data;
    }
    public void deletetable(){
        SQLiteDatabase database=instance.getWritableDatabase();
        database.execSQL(ProfileDatabaseModel.DROP_TABLE);
    }
}
