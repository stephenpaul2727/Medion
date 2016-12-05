package com.starters.medion.dbtasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.starters.medion.contract.EventsContract.EventsEntry;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.utils.ContextGetter;

/**
 * Created by Ashish on 12/1/2016.
 */
public class ReadTask extends AsyncTask {

    private Context context;

    public ReadTask(Context context){
        this.context = context;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        EventsDbhelper mDbHelper = new EventsDbhelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                EventsEntry._ID,
                EventsEntry.COLUMN_NAME_EVENTNAME,
                EventsEntry.COLUMN_NAME_DATE,
                EventsEntry.COLUMN_NAME_TIME,
                EventsEntry.COLUMN_NAME_MEMBERS,
                EventsEntry.COLUMN_NAME_LOCATION
        };

// Filter results WHERE "title" = 'My Title'
        String selection = EventsEntry.COLUMN_NAME_EVENTNAME + " = ?";
        String[] selectionArgs = { "Fun Event" };

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                EventsEntry._ID + " DESC";

        Cursor c = db.query(
                EventsEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                sortOrder                                 // The sort order
        );
        c.moveToFirst();
        String eventName = c.getString(
                c.getColumnIndexOrThrow(EventsEntry.COLUMN_NAME_EVENTNAME)
        );
        System.out.println("ENEJLKLKLK:"+eventName);
        return null;
    }
}
