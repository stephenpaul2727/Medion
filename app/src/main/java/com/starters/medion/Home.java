package com.starters.medion;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.dbtasks.ReadTask;

/**
 * Created by KeerthiTejaNuthi on 11/1/16.
 */

public class Home extends Fragment {

    View homeView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_layout, container, false);
        String message = "Fun Event,02/12/2016,10am,8123456544|5432126879";
        String[] parts= message.split(",");
        InsertTask insert = new InsertTask(getContext());
        ReadTask read = new ReadTask(getContext());
        insert.execute(parts);
        read.execute();
        return homeView;
    }
}
