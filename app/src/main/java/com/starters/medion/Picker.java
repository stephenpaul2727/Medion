package com.starters.medion;

/**
 * Created by stephenpaul on 03/11/16.
 */
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

import java.util.Calendar;

public class Picker extends DialogFragment{

    String s;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DateSettings dateSettings = new DateSettings(getActivity());
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getActivity(), dateSettings,year,month,day);
        s =dateSettings.ret_Date_String();
        System.out.println(s);
        return datePickerDialog;
    }

    public String dateCalc()
    {
        return s;
    }

}
