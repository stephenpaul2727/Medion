package com.starters.medion;

/**
 * Created by stephenpaul on 04/11/16.
 */
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.*;
import android.widget.TimePicker;

public class TimeSettings implements TimePickerDialog.OnTimeSetListener{

    Context context;
    public TimeSettings(Context context)
    {
        this.context=context;
    }
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Toast.makeText(context,"selected time: "+hourOfDay+":"+minute,Toast.LENGTH_LONG).show();

    }
}
