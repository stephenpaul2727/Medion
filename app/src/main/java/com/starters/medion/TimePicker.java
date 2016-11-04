package com.starters.medion;

/**
 * Created by stephenpaul on 04/11/16.
 */

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;

import java.util.Calendar;

public class TimePicker extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        TimeSettings timeSettings=new TimeSettings(getActivity());
        Calendar cal = Calendar.getInstance();
        int hours=cal.get(Calendar.HOUR_OF_DAY);
        int mins = cal.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog;
        timePickerDialog=new TimePickerDialog(getActivity(),timeSettings,hours,mins,true);
        return timePickerDialog;

    }
}
