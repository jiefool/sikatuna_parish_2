package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.arch.lifecycle.ViewModelProviders;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;


import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.os.Environment.getExternalStoragePublicDirectory;
import static android.support.v4.media.session.MediaButtonReceiver.handleIntent;
import static com.loopj.android.http.AsyncHttpClient.LOG_TAG;


public class MainActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener, GroupListFragment.OnListFragmentInteractionListener,  AddEventFragment.OnFragmentInteractionListener {

    public FragmentManager fragmentManager;
    Integer fragContainer;
    View fragContainerView;
    BottomNavigationView navigation;
    ArrayList<Priest> ulist =  new ArrayList<Priest>();
    EventViewModel model;
    ApiUtils apiUtils;
    Date clickedDate = Calendar.getInstance().getTime();
    CurrentUser currentUser;
    MenuItem searchItem;
    Boolean showSearch = false;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.home:
//                    mTextMessage.setText(R.string.title_home);
                    showSearch = false;
                    CalendarFragment calendarFragment = new CalendarFragment();
                    fragmentTransaction.replace(fragContainer, calendarFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;

                case R.id.add_event:
//                    mTextMessage.setText(R.string.title_home);
                    showSearch = false;
                    AddEventFragment addEventFragment = AddEventFragment.newInstance(clickedDate);
                    fragmentTransaction.replace(fragContainer, addEventFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;

                case R.id.events:
//                  mTextMessage.setText(R.string.title_dashboard);
                    showSearch = true;
                    EventListFragment eventListFragment = new EventListFragment();
                    fragmentTransaction.replace(fragContainer, eventListFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                    return true;
                case R.id.groups:
                    showSearch = false;
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
        currentUser = new CurrentUser(this);


        fragmentManager = getSupportFragmentManager();
        fragContainerView = findViewById(R.id.fragContainer);
        fragContainer =  findViewById(R.id.fragContainer).getId();

        getUserDetails();
        loadPriests();

        navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.home);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(getIntent().getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            doMySearch(query);
        }

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

    public void setEventAlarms(List<Event> elist){
        AlarmManager am = (AlarmManager) getSystemService(ALARM_SERVICE);
        ArrayList<PendingIntent> intentArray = new ArrayList<PendingIntent>();


        for(int i=0;i<elist.size();i++){
            if (System.currentTimeMillis() < elist.get(i).getAlarm().getTime()) {

                Calendar cal = Calendar.getInstance();
                cal.setTime(elist.get(i).getAlarm());

                Intent intent = new Intent(this, AlarmReceiver.class);
                intent.putExtra("event", elist.get(i).getJSONObject().toString());
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

    public void editEvent(Event event){
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        EditEventFragment editFragmentFragment = EditEventFragment.newInstance(event);
        fragmentTransaction.replace(fragContainer, editFragmentFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void fireListGroupFragment(){
        navigation.setSelectedItemId(R.id.groups);
    }
    public void fireEventListFragment(){
        navigation.setSelectedItemId(R.id.events);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);

      


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.settings:
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                SettingsFragment settingsFragment = new SettingsFragment();
                fragmentTransaction.replace(fragContainer, settingsFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
            case R.id.logout:
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public  void getUserDetails(){
        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    currentUser.setDataToSharedPreferences("user_id", response.getString("id"));
                    currentUser.setDataToSharedPreferences("username", response.getString("username"));
                    currentUser.setDataToSharedPreferences("name", response.getString("name"));
                    currentUser.setDataToSharedPreferences("photo", response.getString("photo"));
                    currentUser.setDataToSharedPreferences("type", response.getString("type"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        };

        apiUtils.getUserDetails(currentUser.getEmail(), jhrh);
    }


    public void doMySearch(String query){
        System.out.println("search query: "+query);
    }

    public Boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    public File getPublicDocumentStorageDir(String dirName) {
        // Get the directory for the user's public pictures directory.
        File file = new File(getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), dirName);
        if (!file.mkdirs()) {
            Log.e(LOG_TAG, "Directory not created");
        }
        return file;
    }

    public void scanMedia(String path, Context context) {
        File file = new File(path);
        Uri uri = Uri.fromFile(file);
        Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
        context.sendBroadcast(scanFileIntent);
    }
}
