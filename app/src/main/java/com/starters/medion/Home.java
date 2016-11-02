package com.starters.medion;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by KeerthiTejaNuthi on 11/1/16.
 */

public class Home extends Fragment {

    View homeView;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeView = inflater.inflate(R.layout.home_layout, container, false);
        return homeView;
    }
}
