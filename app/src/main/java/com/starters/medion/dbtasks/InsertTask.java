package com.starters.medion.dbtasks;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.starters.medion.contract.EventsContract;
import com.starters.medion.contract.EventsContract.EventsEntry;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.utils.ContextGetter;
/**
 * Created by Ashish on 12/1/2016.
 */
public class InsertTask extends AsyncTask {
    private Context context;

    public InsertTask(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        EventsDbhelper mDbHelper = new EventsDbhelper(context);



        SQLiteDatabase db = mDbHelper.getWritableDatabase();

// Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        /*values.put(EventsEntry.COLUMN_NAME_EVENTNAME, "hello");
        values.put(EventsEntry.COLUMN_NAME_DATE, "03/21/2017");
        values.put(EventsEntry.COLUMN_NAME_TIME, "10 PM");
        values.put(EventsEntry.COLUMN_NAME_MEMBERS, "8123616740");
        values.put(EventsEntry.COLUMN_NAME_LOCATION, "blah");*/


        values.put(EventsEntry.COLUMN_NAME_EVENTNAME, params[0].toString());
        values.put(EventsEntry.COLUMN_NAME_DATE, params[1].toString());
        values.put(EventsEntry.COLUMN_NAME_TIME, params[2].toString());
        values.put(EventsEntry.COLUMN_NAME_MEMBERS, params[3].toString());
        values.put(EventsEntry.COLUMN_NAME_LOCATION, "blah");
// Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(EventsEntry.TABLE_NAME, null, values);
        return null;
    }
}
