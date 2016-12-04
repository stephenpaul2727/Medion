package com.starters.medion;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
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

import com.gc.materialdesign.views.ButtonRectangle;
import com.starters.medion.model.Event;
import com.starters.medion.model.User;

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

public class EditAdmin extends Fragment{
    ArrayAdapter<String> adapter;
    public ListView contact_list = null;
    public HashMap<String,String> myMap;
    public ArrayList<String> contactsarray = new ArrayList<String>();
    public ImageButton imageButton;
    private int RESULT_LOAD_IMG =1;
    private String decodableImage;
    private String tempDate;
    private String tempTime;
    private EditText eventname;


    private ButtonRectangle saveButton;
    private Event event;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.edit_admin, container, false);
        final ButtonRectangle datepicker = (ButtonRectangle) view.findViewById(R.id.edit_admin_select_date);
        final ButtonRectangle timepicker = (ButtonRectangle) view.findViewById(R.id.edit_admin_select_time);
        eventname = (EditText)view.findViewById(R.id.edit_admin_event_name);
        imageButton = (ImageButton) view.findViewById(R.id.edit_imagebutton);
        saveButton = (ButtonRectangle) view.findViewById(R.id.edit_admin_save);
        final ButtonRectangle membersButton = (ButtonRectangle) view.findViewById(R.id.edit_admin_addMembers);
        populateContactList();
        contact_list = new ListView(getActivity());
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),R.layout.contact_list,R.id.contacts,contactsarray);
        contact_list.setAdapter(adapter);
        contact_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewGroup contactvg = (ViewGroup)view;
                TextView contactstxt = (TextView)contactvg.findViewById(R.id.contacts);
                Toast.makeText(getActivity(), contactstxt.getText().toString(),Toast.LENGTH_LONG).show();
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
                    tempDate=pickerDialogs.dateCalc();

                }
            }
        });
        timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==timepicker){
                    TimePicker timepickerdialog = new TimePicker();
                    timepickerdialog.show(getFragmentManager(),"time_picker");
                    tempTime=timepickerdialog.timeCalc();
                }

            }
        });


        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                System.out.println(eventname.getText());
                System.out.println("Time is: "+tempTime);
                System.out.println("date is :"+tempDate);
                for(int i=0;i<contactsarray.size();i++)
                {
                    System.out.println(contactsarray.get(i));
                }
                ArrayList<String> mem = new ArrayList<String>();
                mem.add(0,"1234567890");
                mem.add(1,"0987654321");
                String members = TextUtils.join(",", mem);
                //To get it back to ArrayList,
                //List<String> myList = new ArrayList<String>(Arrays.asList(members.split(",")));
                new EditAdmin.HttpAsyncTask().execute("TasteOFIndia","12-04-16","18:00",members,"http://149.161.150.185:8080/api/notifyMembers");
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
    public void showDialogListView(View view)
    {
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
                contactsarray.add(phoneNumber);
            }
        }
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
            JSONObject userJson = new JSONObject();
            userJson.accumulate("eventName", event.getEventName());
            userJson.accumulate("eventDate", event.getEventDate());
            userJson.accumulate("eventTime", event.getEventTime());
            userJson.accumulate("memberList", event.getMemberList());

            // 4. convert JSONObject to JSON to String and send json content
            out.write(userJson.toString());
            out.flush();
            out.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            while (in.readLine() != null) {
                System.out.println(in);
            }
            System.out.println("\nMedion notify REST Service Invoked Successfully..");
            in.close();
        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... args) {
            event = new Event();
            event.setEventName(args[0]);
            event.setEventDate(args[1]);
            event.setEventTime(args[2]);
            event.setMemberList(args[3]);

            return POST(args[4],event);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getActivity().getBaseContext(), "Event Created!", Toast.LENGTH_LONG).show();
        }
    }


}
