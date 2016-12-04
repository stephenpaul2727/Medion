package com.starters.medion;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

/**
 * Created by stephenpaul on 03/11/16.
 */

public class DateSettings implements DatePickerDialog.OnDateSetListener {
    Context context;
    public int day;
    public int month;
    public int yearofcreation;
    public DateSettings(Context context)
    {
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(context,"selected date: "+dayOfMonth+"/ "+monthOfYear+"/ "+year,Toast.LENGTH_LONG).show();
        EditAdmin editAdmin = new EditAdmin();
        day = dayOfMonth;
        month = monthOfYear;
        yearofcreation = year;
        editAdmin.setTempDate(day,month,yearofcreation);

    }
}
