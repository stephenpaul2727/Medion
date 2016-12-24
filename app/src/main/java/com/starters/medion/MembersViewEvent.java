package com.starters.medion;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.starters.medion.contract.EventsContract;
import com.starters.medion.dbhelper.EventsDbhelper;

/**
 * Created by stephenpaul on 18/12/16.
 */

public class MembersViewEvent extends AppCompatActivity{


    private TextView eventid;
    private TextView eventname;
    private TextView eventdate;
    private TextView eventadmin;
    private TextView eventime;
    private ImageButton cancelEvent;
    private TextView memlocs;
    private ImageButton pickPlace;
    private EventsDbhelper mDbhelper;
    private ImageButton currentMembers;
    private String mem;
    private String eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.member_view_event_layout);
        eventname = (TextView)findViewById(R.id.members_eventname);
        eventid = (TextView)findViewById(R.id.members_eventid);
        eventdate = (TextView)findViewById(R.id.members_eventdate);
        eventime = (TextView)findViewById(R.id.members_eventtime);
        eventadmin = (TextView)findViewById(R.id.members_eventadmin);
        memlocs = (TextView)findViewById(R.id.members_location);
        pickPlace = (ImageButton) findViewById(R.id.members_pickplace);
        cancelEvent = (ImageButton)findViewById(R.id.cantcome);
        currentMembers = (ImageButton)findViewById(R.id.members_addMembers);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            eventId = b.getString("id");
            String eventName = b.getString("eventname");
            String eventAdmin = b.getString("admin");
            String date = b.getString("date");
            String time = b.getString("time");
            String members = b.getString("members");
            String locs = b.getString("latlongs");

            System.out.println("IDID:" + eventId);
            System.out.println("Name:" + eventName);
            System.out.println("admin:" + eventAdmin);
            System.out.println("date:" + date);
            System.out.println("time:" + time);
            System.out.println("members:" + members);

        }
            mDbhelper = new EventsDbhelper(this);
                //means this is the view part not the add contact part.
                Cursor rs = mDbhelper.getData(eventId);
        if(rs!=null)
        {
                rs.moveToFirst();

                String nam = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_EVENTNAME));
                String dat = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_DATE));
                String tim = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_TIME));
                String loc = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_LOCATION));
                mem = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS));
                String adm = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_ADMIN));

                if (!rs.isClosed())  {
                    rs.close();
                }


            eventid.setText(eventId);
            eventname.setText(nam);
            eventdate.setText(dat);
            eventime.setText(tim);
            eventadmin.setText(adm);
            memlocs.setText(loc);


        }

        pickPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String [] parts =memlocs.getText().toString().split(",");
                Uri gmmIntentUri = Uri.parse("geo:"+parts[0]+","+parts[1]+"?q=" + Uri.encode("Decided Location!"));

//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            }
        });

        cancelEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder cancelbuilder = new android.app.AlertDialog.Builder(MembersViewEvent.this);
                        cancelbuilder.setTitle("Cancel Invitation?");
                        cancelbuilder.setMessage("Are you sure you can't make it? last chance.!");
                        cancelbuilder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
                                SQLiteDatabase db = eventsDbhelper.getWritableDatabase();
                                db.delete(EventsContract.EventsEntry.TABLE_NAME,EventsContract.EventsEntry.COLUMN_NAME_EVENTID+"=?",new String[]{eventId});
                                db.close();
                                eventsDbhelper.close();
                                Intent intent = new Intent(MembersViewEvent.this,Home.class);
                                startActivity(intent);
                            }
                        });
                        cancelbuilder.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();

                            }
                        });
                        android.app.AlertDialog dialog = cancelbuilder.create();
                        dialog.show();
            }
        });

        currentMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Instantiate an AlertDialog.Builder with its constructor
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(MembersViewEvent.this);

                // 2. Chain together various setter methods to set the dialog characteristics
                builder.setMessage(mem)
                        .setTitle("Current Members");

                // 3. Get the AlertDialog from create()
                android.app.AlertDialog dialog = builder.create();
                dialog.show();
            }
        });


    }
}
