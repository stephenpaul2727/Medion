package com.starters.medion;

import android.*;
import android.Manifest;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonRectangle;
import com.starters.medion.contract.EventsContract;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.model.Eid;
import com.starters.medion.model.Event;
import com.starters.medion.dbhelper.EventsDbhelper;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.ResourceBundle;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by Ashish on 12/8/2016.
 */
public class ViewEvent extends AppCompatActivity {

    private TextView eventid;
    private TextView eventnameview;
    private TextView eventdate;
    private TextView eventadmin;
    public HashMap<String,String> myMap;
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    private TextView eventime;
    private TextView eventmembers;
    private TextView location;
    private Event event;
    private EventsDbhelper mdbhelper;
    private Eid eid;
    private ButtonRectangle finalizeEvent;
    public ButtonRectangle saveButton;
    public ButtonRectangle membersButton;
    private ButtonRectangle cancelEvent;
    private ButtonRectangle requestPlaces;
    private String members;
    private String [] newMemberList;
    public ListView contact_list = null;
    public ArrayList<String> contactsarray = new ArrayList<String>();
    public ArrayList<String> contactsarray2 = new ArrayList<String>();
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_event_layout);
        eventnameview = (TextView)findViewById(R.id.eventname);
        eventid = (TextView)findViewById(R.id.eventid);
        eventdate = (TextView)findViewById(R.id.eventdate);
        eventime = (TextView)findViewById(R.id.eventtime);
        eventmembers = (TextView)findViewById(R.id.eventmembers);
        eventadmin = (TextView)findViewById(R.id.eventadmin);
        location = (TextView)findViewById(R.id.location);


        Bundle b = getIntent().getExtras();
        if(b != null){
            eventId = b.getString("id");
            String eventName = b.getString("eventname");
            String eventAdmin=b.getString("admin");
            String date = b.getString("date");
            String time = b.getString("time");
            String latlongs = b.getString("latlongs");
            String members = b.getString("members");
            System.out.println("IDID:" + eventId);
            System.out.println("Name:" + eventName);
            System.out.println("admin:" + eventAdmin);
            System.out.println("date:" + date);
            System.out.println("time:" + time);
            System.out.println("members:" + members);

//            eventid.setText(eventId);
//            eventnameview.setText(eventName);
//            eventdate.setText(date);
//            eventime.setText(time);
//            eventmembers.setText(members);
//            eventadmin.setText(eventAdmin);
//            location.setText(latlongs);

        }

            mdbhelper = new EventsDbhelper(this);
            Cursor rs = mdbhelper.getData(eventId);
        if(rs!=null) {

            rs.moveToFirst();
            String nam = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_EVENTNAME));
            String dat = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_DATE));
            String tim = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_TIME));
            String mem = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_MEMBERS));
            String pla = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_LOCATION));
            String adm = rs.getString(rs.getColumnIndex(EventsContract.EventsEntry.COLUMN_NAME_ADMIN));


            eventid.setText(eventId);
            eventnameview.setText(nam);
            eventdate.setText(dat);
            eventime.setText(tim);
            eventmembers.setText(mem);
            eventadmin.setText(adm);
            location.setText(pla);


        }

        finalizeEvent = (ButtonRectangle) findViewById(R.id.edit_viewevent_finalizeevent);
        saveButton = (ButtonRectangle) findViewById(R.id.edit_viewevent_save);
        membersButton = (ButtonRectangle) findViewById(R.id.edit_viewevent_addMembers);
        cancelEvent = (ButtonRectangle)findViewById(R.id.edit_viewevent_cancelevent);
        requestPlaces = (ButtonRectangle)findViewById(R.id.edit_viewevent_places);

        newMemberList = eventmembers.getText().toString().split(",");
        checkContactPermission();
        populateContactList();


//        finalizeEvent.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d("Inside:","Onclick");
//                Integer temp = Integer.parseInt(eventid.getText().toString());
//                eid = new Eid();
//                eid.setId(temp);
//                new HttpAsyncTask().execute(temp.toString(),"https://whispering-everglades-62915.herokuapp.com/api/calcMedian");
////                new EditAdmin.HttpAsyncTask().execute(temp.toString(),"https://whispering-everglades-62915.herokuapp.com/api/calcMedian");
//
//            }
//        });
        requestPlaces.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Integer temp = Integer.parseInt(eventid.getText().toString());
                eid = new Eid();
                eid.setId(temp);
                new HttpAsyncTask().execute(temp.toString(),"https://whispering-everglades-62915.herokuapp.com/api/calcMedian");

            }
        });

        membersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==membersButton)
                {
                    showDialogListView(v);
                }
            }
        });
//        saveButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                System.out.println(eventnameview.getText().toString());
//                System.out.println(eventdate.getText().toString()+"vvv"+eventime.getText().toString());
//
//
//                ArrayList<String> mem = new ArrayList<String>();
//                for(int i=0; i<contactsarray.size(); i++) {
//                    mem.add(i, contactsarray.get(i));
//                }
////                mem.add(0,"123");
////                mem.add(0,"8129551395");
//                members = TextUtils.join(",", mem);
//                //To get it back to ArrayList,
//                //List<String> myList = new ArrayList<String>(Arrays.asList(members.split(",")));
////                new EditAdmin.HttpAsyncTask().execute(eventname.getText().toString(),home.getDate(),home.getTime(),members,"http://149.161.150.243:8080/api/notifyMembers");
//                new HttpAsyncTask().execute(eventnameview.getText().toString(),eventdate.getText().toString(),eventime.getText().toString(),members,"https://whispering-everglades-62915.herokuapp.com/api/notifyMembers");
//
//            }
//        });

    }

    public void checkContactPermission()
    {
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {


            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);
        }
    }

    public void populateContactList()
    {
        myMap = new HashMap<String,String>();


        ContentResolver resolver = this.getContentResolver();
        Cursor cursor =resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        while(cursor.moveToNext())
        {
            String id= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?",new String[] {id},null);
            while(phoneCursor.moveToNext())
            {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

                phoneNumber = phoneNumber.replaceAll("\\s+","");
                phoneNumber= phoneNumber.replaceAll("[^a-zA-Z0-9]","");
                int count=0;
                for(int i=0;i<newMemberList.length;i++)
                {
                    if(newMemberList[i].equals(phoneNumber))
                    {
                        count++;
                        System.out.println("insi");
                        break;
                    }
                }
                if(count==0)
                {
                    myMap.put(name,phoneNumber);
                    contactsarray2.add(name+"/"+phoneNumber);
                }

            }
        }
    }

    public static String POST(String stringURL, Eid eid){
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

//    public static String POST(String stringURL, Event event) {
//        String result = "";
//        try {
//
//            // 1. create URL
//            URL url = new URL(stringURL);
//
//            // 2. create connection to given URL
//            URLConnection connection = url.openConnection();
//            connection.setDoInput(true);
//            connection.setDoOutput(true);
//            connection.setUseCaches(false);
//            connection.setRequestProperty("Content-Type", "application/json");
//            connection.setConnectTimeout(5000);
//            connection.setReadTimeout(5000);
//            connection.connect();
//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//
//            // 3. build jsonObject
//            JSONObject eventJson = new JSONObject();
//            eventJson.accumulate("eventName", event.getEventName());
//            eventJson.accumulate("eventDate", event.getEventDate());
//            eventJson.accumulate("eventTime", event.getEventTime());
//            eventJson.accumulate("memberList", event.getMemberList());
//
//            // 4. convert JSONObject to JSON to String and send json content
//            out.write(eventJson.toString());
//            out.flush();
//            out.close();
//
//            BufferedReader in = new BufferedReader(new InputStreamReader(
//                    connection.getInputStream()));
//            String inputLine;
//            while ((inputLine = in.readLine()) != null)
//                result = inputLine;
//            System.out.println(result);
//            in.close();
////            }
//            System.out.println("\nMedion notify REST Service Invoked Successfully..");
////            in.close();
//        } catch (Exception e) {
//            Log.d("InputStream", e.getLocalizedMessage());
//        }
//
//        return result;
//    }

    public static String GET(String stringURL) {
        InputStream inputStream = null;
        String result = "";
        try {

            Log.d("InputStream", "Before Connecting");
            // 1. create URL
            URL url = new URL(stringURL);

            // 2. create connection to given URL
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setDoInput(true);
//            connection.setDoOutput(true);
            connection.setRequestMethod("GET");
            connection.setUseCaches(false);
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);
            connection.connect();
            System.out.println(connection.getResponseMessage());
//            OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
//
//            // 3. build jsonObject
//           JSONObject json = new JSONObject();
//            json.put("username",myuser);
//            json.put("password",mypass);
//
//            // 4. convert JSONObject to JSON to String and send json content
//            out.write(json.toString());
//            out.flush();
//            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result = inputLine;
            in.close();
//            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//
//            while (in.readLine() != null) {
//                System.out.println(in);
//            }
            System.out.println("\nsuccessfully sent login details.");
//            in.close();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public void showDialogListView(View view)
    {
        contact_list = new ListView(ViewEvent.this);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(ViewEvent.this,R.layout.contact_list,R.id.contacts,contactsarray2);
        contact_list.setAdapter(adapter);
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup contactvg = (ViewGroup)view;
                TextView contactstxt = (TextView)contactvg.findViewById(R.id.contacts);
                String s = contactstxt.getText().toString();
                String[] phone =s.split("/");
                phone[1]=phone[1].replaceAll("\\s+","");
                phone[1]=phone[1].replaceAll("[^a-zA-Z0-9]","");
                System.out.println("contact is:"+phone[1]);
                contactsarray.add(phone[1]);
                Toast.makeText(ViewEvent.this, contactstxt.getText().toString(),Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(ViewEvent.this);
        builder.setCancelable(true);
        builder.setPositiveButton("OK",null);
        builder.setView(contact_list);
        AlertDialog dialog = builder.create();
        dialog.show();

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        private String res;
        @Override
        protected String doInBackground(String... args) {
            int count = args.length;
            if(count < 3){
                res= POST(args[1], eid);
                return res;
            }
//            else {
//                event = new Event();
//                event.setEventId(eid);
//                event.setEventName(args[0]);
//                event.setEventDate(args[1]);
//                event.setEventTime(args[2]);
//                event.setMemberList(args[3]);
//
//                eventId= POST(args[4], event);
//                return eventId;
//            }
            return null;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            System.out.println("inside postexecture"+res);
            String [] parts = res.split(",");
            EventsDbhelper eventsDbhelper = new EventsDbhelper(getApplicationContext());
            location.setText(res);
            if(eventsDbhelper.updateContact(eventid.getText().toString(),eventnameview.getText().toString(),eventdate.getText().toString(),eventime.getText().toString(),eventmembers.getText().toString(),res,eventadmin.getText().toString()))
            {
                Toast.makeText(ViewEvent.this,"Location Updated!",Toast.LENGTH_LONG).show();
            }
            Intent mesintent=new Intent(ViewEvent.this,PlacesMap.class);
            mesintent.putExtra("latlong",parts[0]+"/"+parts[1]);
            startActivity(mesintent);

//            InsertTask insertTask = new InsertTask(getContext());
//            insertTask.execute("",eventId,eventname.getText().toString(),home.getDate(),home.getTime(),members,"ADMIN",null);
//            Toast.makeText(getActivity().getApplicationContext(), "Event Created!", Toast.LENGTH_LONG).show();
        }
    }

}
