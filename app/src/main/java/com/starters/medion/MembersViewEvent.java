package com.starters.medion;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by stephenpaul on 18/12/16.
 */

public class MembersViewEvent extends AppCompatActivity{


    private TextView eventid;
    private TextView eventname;
    private TextView eventdate;
    private TextView eventadmin;
    private TextView eventime;
    private TextView eventmembers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_view_event_layout);
        eventname = (TextView)findViewById(R.id.eventname);
        eventid = (TextView)findViewById(R.id.eventid);
        eventdate = (TextView)findViewById(R.id.eventdate);
        eventime = (TextView)findViewById(R.id.eventtime);
        eventmembers = (TextView)findViewById(R.id.eventmembers);
        eventadmin = (TextView)findViewById(R.id.eventadmin);




        Bundle b = getIntent().getExtras();
        if(b != null){
            String eventId = b.getString("id");
            String eventName = b.getString("eventname");
            String eventAdmin=b.getString("admin");
            String date = b.getString("date");
            String time = b.getString("time");
            String members = b.getString("members");
            System.out.println("IDID:" + eventId);
            System.out.println("Name:" + eventName);
            System.out.println("admin:" + eventAdmin);
            System.out.println("date:" + date);
            System.out.println("time:" + time);
            System.out.println("members:" + members);
            eventid.setText(eventId);
            eventname.setText(eventName);
            eventdate.setText(date);
            eventime.setText(time);
            eventmembers.setText(members);
            eventadmin.setText(eventAdmin);


        }

    }
}
