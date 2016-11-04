package com.starters.medion;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.widget.Button;
import android.widget.TextView;
import android.support.v4.app.DialogFragment;


import org.w3c.dom.Text;

import java.util.Calendar;

/**
 * Created by stephenpaul on 03/11/16.
 */

public class EditAdmin extends Fragment{
    Spinner contact_spinner;
    ArrayAdapter<CharSequence> adapter;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.edit_admin, container, false);
        final Button datepicker = (Button)view.findViewById(R.id.edit_admin_select_date);
        final Button timepicker = (Button) view.findViewById(R.id.edit_admin_select_time);
        final ImageButton imageButton = (ImageButton) view.findViewById(R.id.edit_imagebutton);

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v==imageButton)
                {

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

        return view;
    }




}
