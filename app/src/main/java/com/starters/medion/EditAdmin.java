package com.starters.medion;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.util.List;

import com.gc.materialdesign.views.ButtonRectangle;
import com.starters.medion.constants.config;
import com.starters.medion.dbhelper.EventsDbhelper;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.model.Eid;
import com.starters.medion.model.Event;
import com.starters.medion.model.User;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;


import static android.app.Activity.RESULT_OK;

/**
 * Created by stephenpaul on 03/11/16.
 */

public class EditAdmin extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 0;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_CONTACTS = 2;
    ArrayAdapter<String> adapter;
    public ListView contact_list = null;
    public HashMap<String,String> myMap;
    public ArrayList<String> contactsarray = new ArrayList<String>();
    public ArrayList<String> contactsarray2 = new ArrayList<String>();
    public ImageButton imageButton;
    private int RESULT_LOAD_IMG =1;
    private String decodableImage;
    private String tempDate;
    private String tempTime;
    private EditText eventname;
    private ImageButton membersButton;
    private ButtonRectangle saveButton;
    private Event event;
    private Home home;
    private ButtonRectangle finalizeEvent;
    private Eid eid;
    private String eventId;
    private String members;
    private ImageButton datepicker;
    View view;
    private ImageButton timepicker;

    public interface HomeListener
    {
        public String getDate();
        public String getTime();
    }
    public interface MainActivityListener
    {

    }
    MainActivityListener mainActivityListener;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if(getView()!=null) {
            ViewGroup parent = (ViewGroup) getView().getParent();
            if (parent != null) {
                parent.removeView(getView());
            }
        }
        try {
            view = inflater.inflate(R.layout.edit_admin, container, false);
        } catch (InflateException e) {

        }

        datepicker = (ImageButton) view.findViewById(R.id.edit_admin_select_date);
        timepicker= (ImageButton) view.findViewById(R.id.edit_admin_select_time);
        eventname = (EditText)view.findViewById(R.id.edit_admin_event_name);
        imageButton = (ImageButton) view.findViewById(R.id.edit_imagebutton);
        saveButton = (ButtonRectangle) view.findViewById(R.id.edit_admin_save);
        membersButton = (ImageButton) view.findViewById(R.id.edit_admin_addMembers);
        checkContactPermission();populateContactList();


        membersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==membersButton)
                {
                    showDialogListView(v);
                }
            }
        });




        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==imageButton)
                {
                    Intent gallery_opener = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivity(gallery_opener);
                }
            }
        });

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==datepicker){
                    Picker pickerDialogs= new Picker();
                    pickerDialogs.show(getFragmentManager(),"date_picker");

                }
            }
        });
        timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==timepicker){
                    TimePicker timepickerdialog = new TimePicker();
                    timepickerdialog.show(getFragmentManager(),"time_picker");
                }

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(eventname.getText());
                System.out.println(home.getDate()+"vvv"+home.getTime());


                ArrayList<String> mem = new ArrayList<String>();
                for(int i=0; i<contactsarray.size(); i++) {
                    mem.add(i, contactsarray.get(i));
                }

//                mem.add(0,"123");
//                mem.add(0,"8129551395");
                members = TextUtils.join(",", mem);
                members=members+","+ config.ownerPhoneNumber;
                //To get it back to ArrayList,
                //List<String> myList = new ArrayList<String>(Arrays.asList(members.split(",")));
//                new EditAdmin.HttpAsyncTask().execute(eventname.getText().toString(),home.getDate(),home.getTime(),members,"http://149.161.150.243:8080/api/notifyMembers");
                new EditAdmin.HttpAsyncTask().execute(eventname.getText().toString(),home.getDate(),home.getTime(),members,"https://whispering-everglades-62915.herokuapp.com/api/notifyMembers");

            }
        });

        return view;
    }

    public void setTempDate(String x)
    {

        tempDate = x;

    }


    public void setTempTime(String y)
    {
        tempTime =y;
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        home = (Home) getActivity();
        try {
            mainActivityListener = (MainActivityListener) getActivity();
        }
        catch(ClassCastException e)
        {
            Log.d("Error","MainActivity must implement listener");
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if(requestCode==RESULT_LOAD_IMG && resultCode== RESULT_OK && null!=data)
            {
                Uri selectedImage = data.getData();
                String[] path = { MediaStore.Images.Media.DATA};
                Cursor imageTraverse = getActivity().getContentResolver().query(selectedImage,path,null,null,null);
                imageTraverse.moveToFirst();
                int column = imageTraverse.getColumnIndex(path[0]);
                decodableImage= imageTraverse.getString(column);
                imageButton.setImageBitmap(BitmapFactory.decodeFile(decodableImage));
                imageButton.invalidate();

            }
            else
            {
                Toast.makeText(getActivity(),"You have picked the wrong image",Toast.LENGTH_LONG).show();
            }

        }
        catch(Exception e)
        {
            Toast.makeText(getActivity(),"unknown Error retreiving image",Toast.LENGTH_LONG).show();
        }
    }

    public void checkContactPermission()
    {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {


            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_CONTACTS},
                    MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.WRITE_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.WRITE_CONTACTS},
                    MY_PERMISSIONS_REQUEST_WRITE_CONTACTS);  
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_CONTACTS) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                populateContactList();
            }
            else
            {
                Toast.makeText(this.getActivity(),"until you give permissions, this app cannot function properly",Toast.LENGTH_LONG);
            }
        }


    }

    public void showDialogListView(View view)
    {

        checkContactPermission();
        contact_list = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.contact_list,R.id.contacts,contactsarray2);
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
                Toast.makeText(getActivity(), contactstxt.getText().toString(),Toast.LENGTH_LONG).show();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
        builder.setPositiveButton("OK",null);
        builder.setView(contact_list);
        AlertDialog dialog = builder.create();
        dialog.show();

    }


    public void populateContactList()
    {
        myMap = new HashMap<String,String>();

    checkContactPermission();
        ContentResolver resolver = getActivity().getContentResolver();
        Cursor cursor =resolver.query(ContactsContract.Contacts.CONTENT_URI,null,null,null,null);

        while(cursor.moveToNext())
        {
            String id= cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,ContactsContract.CommonDataKinds.Phone.CONTACT_ID+" =?",new String[] {id},null);
            while(phoneCursor.moveToNext())
            {
                String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                myMap.put(name,phoneNumber);
                contactsarray2.add(name+"/"+phoneNumber);
            }
        }
    }

    public static String POST(String stringURL, Eid eid){
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

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (in.readLine() != null) {
                System.out.println(in);
            }
            System.out.println("\nMedion notify REST Service Invoked Successfully..");
            in.close();

        }catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return null;
    }

    public static String POST(String stringURL, Event event) {
        String result = "";
        try {

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
            JSONObject eventJson = new JSONObject();
            eventJson.accumulate("eventName", event.getEventName());
            eventJson.accumulate("eventDate", event.getEventDate());
            eventJson.accumulate("eventTime", event.getEventTime());
            eventJson.accumulate("memberList", event.getMemberList());

            // 4. convert JSONObject to JSON to String and send json content
            out.write(eventJson.toString());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null)
                result = inputLine;
                System.out.println(result);
            in.close();
//            }
            System.out.println("\nMedion notify REST Service Invoked Successfully..");
//            in.close();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }


    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            int count = args.length;
            if(count < 3){
                return POST(args[1], eid);
            }else {
                event = new Event();
                event.setEventName(args[0]);
                event.setEventDate(args[1]);
                event.setEventTime(args[2]);
                event.setMemberList(args[3]);

                eventId= POST(args[4], event);
                return eventId;
            }

        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            System.out.println("inside postexecture"+result);
            eventId= result;
            InsertTask insertTask = new InsertTask(getContext());
            insertTask.execute("",eventId,eventname.getText().toString(),home.getDate(),home.getTime(),members,"ADMIN",null);
            Toast.makeText(getActivity().getApplicationContext(), "Event Created!", Toast.LENGTH_LONG).show();
        }
    }


}
