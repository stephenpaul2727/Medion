package com.starters.medion;


import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;

import java.util.Calendar;

public class Picker extends DialogFragment{


    String s;
    Context context;

    private String day;
    private String month;
    private String year;

    private Activity mActivity;
    private DatePickerDialog.OnDateSetListener mListener;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mActivity = activity;
        try
        {
            mListener = (DatePickerDialog.OnDateSetListener) activity;
        }
        catch(Exception e)
        {
            throw new ClassCastException(activity.toString() + " must implement OnDateSetListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar cal = Calendar.getInstance();
        int day = cal.get(Calendar.DAY_OF_MONTH);
        int month = cal.get(Calendar.MONTH);
        int year = cal.get(Calendar.YEAR);
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(mActivity, mListener,year,month,day);
        return datePickerDialog;
    }



}
