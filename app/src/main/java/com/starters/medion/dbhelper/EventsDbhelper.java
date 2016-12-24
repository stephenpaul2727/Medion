package com.starters.medion.dbhelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.CalendarContract;
import android.content.ContentValues;

import com.starters.medion.contract.EventsContract.EventsEntry;
/**
 * Created by Ashish on 12/1/2016.
 */
public class EventsDbhelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 2;
    private Context context;
    public static final String DATABASE_NAME = "Eventinf.db";
    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + EventsEntry.TABLE_NAME + " (" +
                    EventsEntry._ID + " INTEGER PRIMARY KEY," +
                    EventsEntry.COLUMN_NAME_EVENTID + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_EVENTNAME + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_DATE + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_TIME + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_MEMBERS + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_ADMIN + TEXT_TYPE + COMMA_SEP +
                    EventsEntry.COLUMN_NAME_LOCATION + TEXT_TYPE +" )";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + EventsEntry.TABLE_NAME;
    public EventsDbhelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.setContext(context);
    }


    public Context getContext(){
        return this.context;

    }
    public void setContext(Context con){
        this.context = con;
    }
    public void onCreate(SQLiteDatabase db){
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    public boolean updateContact (String id, String name, String date, String time, String members,String location,String admin) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("eventid", id);
        contentValues.put("name", name);
        contentValues.put("date", date);
        contentValues.put("time", time);
        contentValues.put("members", members);
        contentValues.put("location",location);
        contentValues.put("admin",admin);
        db.update(EventsEntry.TABLE_NAME, contentValues, "eventid = "+id,null);
        return true;
    }

    public Cursor getData(String id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+EventsEntry.TABLE_NAME+" where eventid="+id, null );
        return res;
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public boolean addData(String item){
        return false;
    }

    public Cursor getListContents(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor data = db.rawQuery("SELECT * from " + EventsEntry.TABLE_NAME, null);
        return data;
    }


}
