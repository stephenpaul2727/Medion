package com.starters.medion.utils;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.starters.medion.R;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.dbtasks.ReadTask;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ashish on 12/4/2016.
 */
public class ViewEventsList extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.display_events_layout);
        List nameList = new ArrayList<>();
        List dateList = new ArrayList<>();
        List timeList = new ArrayList<>();
        ListView listView = (ListView) findViewById(R.id.displaylistview);
        ReadTask read = new ReadTask(this);
        read.execute();
       /* EventsDbhelper db = new EventsDbhelper(this);
        Cursor c = db.getListContents();
        while(c.moveToNext()){
            nameList.add(c.getString(1));
            dateList.add(c.getString(2));
            timeList.add(c.getString(3));
            ListAdapter listAdapter = new ArrayAdapter<>(this, R.layout.display_event_info,R.id.t_eventname, nameList);
            listView.setAdapter(listAdapter);
        }*/


    }

}
