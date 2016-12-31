package com.starters.medion;

import android.app.DatePickerDialog;
import android.content.Context;
import android.widget.DatePicker;
import android.widget.Toast;

public class DateSettings implements DatePickerDialog.OnDateSetListener {
    Context context;

    private String dd;
    private String mm;
    private String yy;
    private String date;
    public DateSettings(Context context)
    {
        this.context = context;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        Toast.makeText(context,"selected date: "+dayOfMonth+"/ "+monthOfYear+"/ "+year,Toast.LENGTH_LONG).show();
        dd= Integer.toString(dayOfMonth);
        mm = Integer.toString(monthOfYear);
        yy = Integer.toString(year);
        date = dd+"/"+mm+"/"+yy;
    }

    public String ret_Date_String()
    {
        return date;

    }

}
