package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener, GroupListFragment.OnListFragmentInteractionListener,  AddEventFragment.OnFragmentInteractionListener {

    public FragmentManager fragmentManager;
    Integer fragContainer;
    View fragContainerView;
    BottomNavigationView navigation;
    ArrayList<Priest> ulist =  new ArrayList<Priest>();
    ArrayList<Event> elist =  new ArrayList<Event>();
    EventViewModel model;
    ApiUtils apiUtils;
    Date clickedDate = Calendar.getInstance().getTime();

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.home:
//                    mTextMessage.setText(R.string.title_home);

                    CalendarFragment calendarFragment = new CalendarFragment();
                    fragmentTransaction.replace(fragContainer, calendarFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;

                case R.id.add_event:
//                    mTextMessage.setText(R.string.title_home);

                    AddEventFragment addEventFragment = AddEventFragment.newInstance(clickedDate);
                    fragmentTransaction.replace(fragContainer, addEventFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;

                case R.id.events:
//                  mTextMessage.setText(R.string.title_dashboard);
                    EventListFragment eventListFragment = new EventListFragment();
                    fragmentTransaction.replace(fragContainer, eventListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;
                case R.id.groups:
//                  mTextMessage.setText(R.string.title_notifications);
                    GroupListFragment groupListFragment = new GroupListFragment();
                    fragmentTransaction.replace(fragContainer, groupListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;
            }


            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        model = ViewModelProviders.of(this).get(EventViewModel.class);
        apiUtils = new ApiUtils(this);


        fragmentManager = getSupportFragmentManager();
        fragContainerView = findViewById(R.id.fragContainer);
        fragContainer =  findViewById(R.id.fragContainer).getId();


        loadPriests();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.home);



    }

    @Override
    public void onListFragmentInteraction(DummyContent.DummyItem item) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onListFragmentInteraction(Event item) {

    }

    public void fireAddEventFragment(Date clickedDate){
        this.clickedDate = clickedDate;
        navigation.setSelectedItemId(R.id.add_event);
    }

    public void loadPriests(){

        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                try {
                    ulist = new ArrayList<Priest>();
                    for(int i=0;i<response.length();i++){
                        JSONObject priest = response.getJSONObject(i);
                        Priest nPriest = new Priest();
                        nPriest.setPriest(priest);
                        System.out.println("Priest:"+nPriest.getName());
                        ulist.add(nPriest);
                        model.setUlist(ulist);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
            }
        };
        apiUtils.getPriestUsers(jhtrh);
    }

    public void setEventAlarms(){
        List<Event> elist = model.getEventData();
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();

        for(int i=0;i<elist.size();i++){
            if (System.currentTimeMillis() < elist.get(i).getAlarm().getTime()) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(elist.get(i).getAlarm());

                System.out.println("ALARM IN:");
                System.out.println( elist.get(i).getAlarm());
                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("event", elist.get(i).toString());
                PendingIntent pi = PendingIntent.getBroadcast(this, elist.get(i).getId(), intent, elist.get(i).getId());
                am.cancel(pi);
                am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pi);
                intentArray.add(pi);
            }
        }


    }

    public void addNewGroup(){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        AddNewGroupFragment addNewGroupFragment = new AddNewGroupFragment();
        fragmentTransaction.replace(fragContainer, addNewGroupFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void fireListGroupFragment(){
        navigation.setSelectedItemId(R.id.groups);
    }

}
