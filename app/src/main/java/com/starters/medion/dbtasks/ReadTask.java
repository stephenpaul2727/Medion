package com.starters.medion.dbtasks;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.starters.medion.MembersViewEvent;
import com.starters.medion.R;
import com.starters.medion.ViewEvent;
import com.starters.medion.adapter.EventsAdapter;
import com.starters.medion.contract.EventsContract;
import com.starters.medion.contract.EventsContract.EventsEntry;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.model.Event;
//import com.starters.medion.utils.ContextGetter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish on 12/1/2016.
 */
public class ReadTask extends AsyncTask<Object, Event, Object>  {

    private Context context;
    private EventsAdapter eventsAdapter;
    private Activity activity;
    private ListView listView;
    private Event event;
    private EventsDbhelper mDbHelper;
    private String eventName, date, time, ID, admin,members;

    public ReadTask(Context context){
        this.context = context;
        this.activity = (Activity) context;
    }

    @Override
    protected Object doInBackground(Object[] params) {


        mDbHelper = new EventsDbhelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        eventsAdapter = new EventsAdapter(context, R.layout.display_event_info);

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
       // EventsDbhelper db = new EventsDbhelper(this);
        Cursor c = mDbHelper.getListContents();
        Cursor cursor = db.query(
                EventsEntry.TABLE_NAME,                     // The table to query
                projection,                               // The columns to return
                null,                                // The columns for the WHERE clause
                null,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                 // The sort order
        );
        //c.moveToFirst();
        //String eventName = c.getString(
        //        c.getColumnIndexOrThrow(EventsEntry.COLUMN_NAME_EVENTNAME)
        //);
        //System.out.println("ENEJLKLKLK:"+eventName);
        listView = (ListView) activity.findViewById(R.id.displaylistview);

        while(c.moveToNext()){
            eventName = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_EVENTNAME));
            date = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_DATE));
            time = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_TIME));
            ID = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_EVENTID));
            admin = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_ADMIN));
            members = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_MEMBERS));
            System.out.println(EventsEntry._ID);
            event = new Event();
            event.setEventName(eventName);
            event.setEventDate(date);
            event.setEventTime(time);
            event.setEventId(ID);
            event.setAdmin(admin);
            event.setMemberList(members);
            publishProgress(event);
        }

        return null;
    }


    protected void onProgressUpdate(Event... values) {
        eventsAdapter.add(values[0]);
    }

    public ListView getListView() {
        return listView;
    }

    @Override
    protected void onPostExecute(Object o) {
        listView.setAdapter(eventsAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView ins=(TextView)view.findViewById(R.id.t_eventname);
                String [] parts =ins.getText().toString().split(" ");
                System.out.println("clicked event name is "+parts[0]);
                Cursor c = mDbHelper.getListContents();
                while(c.moveToNext())
                {
                    String currentevent= c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_EVENTNAME));
                    if(currentevent.equals(parts[0]))
                    {
                        System.out.println("inside you ass");
                        eventName = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_EVENTNAME));
                        date = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_DATE));
                        time = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_TIME));
                        ID = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_EVENTID));
                        admin = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_ADMIN));
                        members = c.getString(c.getColumnIndex(EventsEntry.COLUMN_NAME_MEMBERS));
                        System.out.println(EventsEntry._ID);
                        event = new Event();
                        event.setEventName(eventName);
                        event.setEventDate(date);
                        event.setEventTime(time);
                        event.setEventId(ID);
                        event.setAdmin(admin);
                        event.setMemberList(members);
                        publishProgress(event);
                    }
                }
                if(event.getAdmin().equals("ADMIN")) {
                    Intent viewEvent = new Intent(context, ViewEvent.class);
                    viewEvent.putExtra("eventname", event.getEventName());
                    viewEvent.putExtra("id", event.getEventId());
                    viewEvent.putExtra("admin", event.getAdmin());
                    viewEvent.putExtra("date", event.getEventDate());
                    viewEvent.putExtra("time", event.getEventTime());
                    viewEvent.putExtra("members", event.getMemberList());
                    context.startActivity(viewEvent);
                }
                else
                {
                    Intent viewEvent = new Intent(context, MembersViewEvent.class);
                    viewEvent.putExtra("eventname", event.getEventName());
                    viewEvent.putExtra("id", event.getEventId());
                    viewEvent.putExtra("admin", event.getAdmin());
                    viewEvent.putExtra("date", event.getEventDate());
                    viewEvent.putExtra("time", event.getEventTime());
                    viewEvent.putExtra("members", event.getMemberList());
                    context.startActivity(viewEvent);
                }
            }
        });

    }
}
