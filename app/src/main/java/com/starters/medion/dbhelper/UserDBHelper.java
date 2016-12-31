package com.starters.medion.dbhelper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

class UserDBHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "User.db";
    private static final String USER_TABLE_NAME = "UserInfo";
    private static final String USER_MOBILE_NUMBER = "number";
    private static final String USER_PASSWORD = "password";
    private static final String USER_VERIFIED = "verified";
    private static final String TEXT_TYPE = " TEXT";
    private Context context;
    private static final String COMMA_SEP = ",";
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + USER_TABLE_NAME;

    public UserDBHelper(Context context)
    {
        super(context, DATABASE_NAME , null, 1);
        this.setContext(context);
    }

    public Context getContext(){
        return this.context;

    }
    private void setContext(Context con){
        this.context = con;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + USER_TABLE_NAME + "(" +
                USER_MOBILE_NUMBER+ " INTEGER PRIMARY KEY," +
                USER_PASSWORD + TEXT_TYPE + COMMA_SEP +
                USER_VERIFIED + TEXT_TYPE+
                " )");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean insertUser(String phone,String pass,String ver)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("INSERT INTO " + USER_TABLE_NAME + "VALUES(" +
                phone+","+pass+","+ver+ " )");
        return true;
    }

    public boolean updateUser (String phone, String pass, String ver) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("number", phone);
        contentValues.put("password", pass);
        contentValues.put("verified", ver);
        db.update(USER_TABLE_NAME, contentValues, "number = "+phone,null);
        return true;
    }

    public Cursor getData(String num) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from "+ USER_TABLE_NAME+" where number="+num+"", null );
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * from " + USER_TABLE_NAME, null);
    }
}
