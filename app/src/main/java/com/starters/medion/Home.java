package com.starters.medion;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.*;
import android.widget.TimePicker;

import com.gc.materialdesign.views.ButtonFloat;
import com.starters.medion.dbtasks.InsertTask;
import com.starters.medion.dbtasks.ReadTask;

public class Home extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener,EditAdmin.HomeListener {

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
    private double latitude;
    private double longitude;
    private ButtonFloat plusButton;
    private ListView listView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
        try{
            //noinspection ConstantConditions
            getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#b7d6e5\">" +"Upcoming Events"+"</font>"));}
        catch(Exception ignored)
        {}

        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimary));

        insert = new InsertTask(this);
        //insert.execute();
        read = new ReadTask(this);
        read.execute();
        plusButton = (ButtonFloat) findViewById(R.id.plusButton);


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                getSupportActionBar().setTitle(Html.fromHtml("<font color=\"#b7d6e5\">" +"Plan an event"+"</font>"));
                fragmentTransaction.replace(R.id.fragment_container,new EditAdmin(), "edit_admin_tag");
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this,Home.class));
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
        @SuppressWarnings("UnusedAssignment") android.app.FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
       if(id == R.id.home)
        {
            Intent intent = new Intent(getApplicationContext(),Home.class);
            startActivity(intent);
        }
        fragmentTransaction.commit();
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
    public String getDate() {
        return pickerDay +"-"+ pickerMonth +"-"+ pickerYear;
    }

    @Override
    public String getTime() {
        return Integer.toString(pickerHour) +":"+ Integer.toString(pickerMin);
    }
}
