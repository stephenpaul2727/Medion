package com.starters.medion;

import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.TimePicker;

import com.gc.materialdesign.views.ButtonFloat;
import com.gc.materialdesign.views.ButtonRectangle;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.dbtasks.ReadTask;

import java.util.zip.Inflater;
//import com.starters.medion.utils.Maps;

/**
 * Created by KeerthiTejaNuthi on 11/1/16.
 */

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,EditAdmin.HomeListener,EditMembers.HomeListener {

    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private InsertTask insert;
    private ReadTask read;
    private ListView list;

    private int pickerHour;
    private int pickerMin;
    private String pickerDay;
    private String pickerMonth;
    private String pickerYear;
    private ButtonFloat plusButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        ActionBar actionBar= getSupportActionBar();
//        actionBar.hide();
//        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
//
////Remove notification bar
//        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//
////set content view AFTER ABOVE sequence (to avoid crash)
//        this.setContentView(R.layout.home_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        insert = new InsertTask(this);
        //insert.execute();
        read = new ReadTask(this);
        read.execute();
        plusButton = (ButtonFloat) findViewById(R.id.plusButton);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                fragmentTransaction.replace(R.id.fragment_container, new EditAdmin(), "edit_admin_tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
        if(list != null)
            list.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        list = read.getListView();
        list.setVisibility(View.GONE)   ;
        FragmentManager fragmentManager = getFragmentManager();
        android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (id == R.id.admin_edit_event) {

            EditAdmin editAdmin = new EditAdmin();
            fragmentTransaction.replace(R.id.fragment_container,new EditAdmin(), "edit_admin_tag");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.edit_members) {

            fragmentTransaction.replace(R.id.fragment_container, new EditMembers(), "edit_members_tag");
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        } else if (id == R.id.getMap) {
            Intent intent = new Intent(getApplicationContext(), PlacesMap.class);
            startActivity(intent);
        }
        else if(id == R.id.home)
        {
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        pickerHour=hourOfDay;
        pickerMin = minute;
        System.out.println("hour is:"+pickerHour+""+"minute is:"+ pickerMin);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        pickerDay = Integer.toString(dayOfMonth);
        pickerMonth = Integer.toString(monthOfYear);
        pickerYear = Integer.toString(year);
        System.out.println("date is:"+" "+pickerMonth+":"+pickerDay+":"+pickerYear);
    }


    @Override
    public String getDateTimeMem() {
        String s = pickerDay +"/"+pickerMonth+"/"+pickerYear+" "+pickerHour+":"+pickerMin;
        return s;
    }

    @Override
    public String getDate() {
        String date = pickerDay +"-"+ pickerMonth +"-"+ pickerYear;
        return date;
    }

    @Override
    public String getTime() {
        String time = Integer.toString(pickerHour) +":"+ Integer.toString(pickerMin);
        return time;
    }
}
