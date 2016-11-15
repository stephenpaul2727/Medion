package com.starters.medion;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.net.Uri;

import com.gc.materialdesign.views.ButtonRectangle;

import java.util.ArrayList;
import java.util.HashMap;

import static android.app.Activity.RESULT_OK;


/**
 * Created by stephenpaul on 04/11/16.
 */

public class EditMembers extends Fragment {

    public ListView contact_list = null;
    private static int RESULT_LOAD_IMG=1;
    public HashMap<String,String> myMap;
    String decodableImage;
    public ImageButton imageButton;
    public ArrayList<String> contactsarray = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.edit_members,container,false);

        imageButton = (ImageButton) view.findViewById(R.id.edit_members_imageButton);
        final ButtonRectangle saveButton = (ButtonRectangle) view.findViewById(R.id.edit_members_save);
        final ButtonRectangle addMembersButton = (ButtonRectangle) view.findViewById(R.id.edit_members_addmembers);

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

        saveButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                //add the save Functionality
            }

        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery_opener = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivity(gallery_opener);
            }
        });

        addMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogListView(v);

            }
        });

        return view;

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
                contactsarray.add(name + " "+ phoneNumber);
            }
        }
    }
}
