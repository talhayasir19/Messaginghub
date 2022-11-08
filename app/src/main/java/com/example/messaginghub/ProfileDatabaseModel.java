package com.example.messaginghub;

import com.android.car.ui.toolbar.Tab;

public class ProfileDatabaseModel{
    public static final String TABLE_NAME="Profiledata";
    public static final String KEY_USER_ID="Userid";
    public static final String KEY_USERNAME="Username";
    public static final String KEY_PHONE_NO="Phoneno";
    public static final String KEY_IMGID="Imageid";
    public static final String CREATE_TABLE= "CREATE TABLE " + TABLE_NAME +
            " ( " +
            KEY_USER_ID + " INTEGER PRIMARY KEY," +
            KEY_USERNAME + " TEXT," +
            KEY_PHONE_NO + " TEXT,"+
            KEY_IMGID + " TEXT"+" ) ";

    public static final String DROP_TABLE="DROP TABLE IF EXISTS "+TABLE_NAME;
    public static final String SELECT_DATA="SELECT * FROM " + TABLE_NAME;

    public ProfileDatabaseModel() {
    }

    public String Username;
    public String Phoneno;
    public String Imgid;
    public String getImgid() {
        return Imgid;
    }

    public void setImgid(String imgid) {
        Imgid = imgid;
    }

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPhoneno() {
        return Phoneno;
    }

    public void setPhoneno(String phoneno) {
        Phoneno = phoneno;
    }

    public ProfileDatabaseModel(String username,String phoneno,String imageid) {
        Username = username;
        Phoneno = phoneno;
        Imgid=imageid;
    }
}
