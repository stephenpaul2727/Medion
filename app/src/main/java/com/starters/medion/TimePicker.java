package com.starters.medion;

/**
 * Created by stephenpaul on 04/11/16.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.Toast;

import java.util.Calendar;

public class TimePicker extends DialogFragment {

    String s;
    Context context;

    private String hh;
    private String mm;
    private String time;

    private Activity mActivity;
    private TimePickerDialog.OnTimeSetListener mListener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity= activity;

        try {
            mListener = (TimePickerDialog.OnTimeSetListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnTimeSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int hours=cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog=new TimePickerDialog(mActivity,mListener,hours,mins, DateFormat.is24HourFormat(mActivity));
        return timePickerDialog;

    }




}
