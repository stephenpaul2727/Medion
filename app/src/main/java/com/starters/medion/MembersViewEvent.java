package com.starters.medion;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.starters.medion.constants.config;
import com.starters.medion.contract.EventsContract;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.model.Delid;
import com.starters.medion.model.Eid;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.net.ssl.HttpsURLConnection;

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
    Delid delid;
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
                if(memlocs.getText().toString().isEmpty())
                {
                    Toast.makeText(MembersViewEvent.this,"Location is still unpicked by admin of this group!",Toast.LENGTH_LONG).show();
                }
                else {
                    String[] parts = memlocs.getText().toString().split(",");
                    Uri gmmIntentUri = Uri.parse("geo:" + parts[0] + "," + parts[1] + "?q=" + Uri.encode("Decided Location!"));

//                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                    Intent intent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                    intent.setPackage("com.google.android.apps.maps");
                    startActivity(intent);
                }
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
                                SharedPreferences prefer = getApplicationContext().getSharedPreferences(config.SHARED_PREF, 0);
                                String regId = prefer.getString("regId", null);
                                new HttpAsyncTask().execute(regId+"!"+eventId,"https://whispering-everglades-62915.herokuapp.com/serv/delmember");

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

    public static String POST(String stringURL, Delid eid){
        String result="";
        try {
            Log.d("POST","reached!");
            // 1. create URL
            URL url = new URL(stringURL);

            // 2. create connection to given URL
            URLConnection connection = url.openConnection();
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(false);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());

            // 3. build jsonObject
            JSONObject eventIDJson = new JSONObject();
            eventIDJson.accumulate("id", eid.getId());

            // 4. convert JSONObject to JSON to String and send json content
            out.write(eventIDJson.toString());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result = inputLine;
            in.close();

        }catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private String res;
        @Override
        protected String doInBackground(String... args) {
                delid = new Delid();
            String userphonenum=null;
            try {
                FileInputStream f = MembersViewEvent.this.openFileInput("login_details_file");
                BufferedReader br = new BufferedReader( new InputStreamReader(f));
                String line;
                while((line = br.readLine())!=null)
                {
                    userphonenum = line;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
                delid.setId(userphonenum+"!"+eventId);
                res= POST(args[1], delid);
                return res;
            }


        @Override
        protected void onPostExecute(String s) {
            String res= s;
            Toast.makeText(MembersViewEvent.this,res,Toast.LENGTH_LONG).show();
            EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
            SQLiteDatabase db = eventsDbhelper.getWritableDatabase();
            db.delete(EventsContract.EventsEntry.TABLE_NAME,EventsContract.EventsEntry.COLUMN_NAME_EVENTID+"=?",new String[]{eventId});
            db.close();
            eventsDbhelper.close();
            Intent intent = new Intent(MembersViewEvent.this,Home.class);
            startActivity(intent);
        }
    }





}
