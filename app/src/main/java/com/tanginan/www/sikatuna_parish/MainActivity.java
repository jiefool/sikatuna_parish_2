package com.tanginan.www.sikatuna_parish;

import android.arch.lifecycle.ViewModelProviders;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.tanginan.www.sikatuna_parish.dummy.DummyContent;


public class MainActivity extends AppCompatActivity implements EventListFragment.OnListFragmentInteractionListener, GroupListFragment.OnListFragmentInteractionListener,  AddEventFragment.OnFragmentInteractionListener {

    public FragmentManager fragmentManager;
    Integer fragContainer;
    BottomNavigationView navigation;

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

                    AddEventFragment addEventFragment = new AddEventFragment();
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
//                    GroupListFragment groupListFragment = new GroupListFragment();
//                    fragmentTransaction.replace(fragContainer, groupListFragment);
//                    fragmentTransaction.addToBackStack(null);
//                    fragmentTransaction.commit();

                    fireAddEventFragment();
                    return true;
            }


            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EventViewModel model = ViewModelProviders.of(this).get(EventViewModel.class);
        model.loadData(this);

        fragmentManager = getSupportFragmentManager();
        fragContainer =  findViewById(R.id.fragContainer).getId();

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

    public void fireAddEventFragment(){
        navigation.setSelectedItemId(R.id.add_event);
    }

}
