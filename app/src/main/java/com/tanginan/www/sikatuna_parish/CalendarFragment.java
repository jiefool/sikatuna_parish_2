package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cz.msebera.android.httpclient.Header;

import static android.content.Context.ALARM_SERVICE;


public class CalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EventViewModel model;
    List<EventDay> events;
    MyMinimalEventListRecyclerViewAdapter adapter;
    ArrayList<Event> parishEvents = new ArrayList<>();
    ApiUtils apiUtils;
    LinearLayout calendarContainer;
    ProgressBar loadProgressBar;
    CalendarView calendarView;
    ScrollView listContainer;
    RecyclerView recyclerView;
    LinearLayout noEventTextView;
    Date clickedDate = Calendar.getInstance().getTime();
    Button addNewEvent2Btn;
    Button addNewEventBtn;


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public CalendarFragment() {
        // Required empty public constructor
    }

    public static CalendarFragment newInstance(MainActivity mainActivity) {
        CalendarFragment fragment = new CalendarFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
//            fragContainer = getArguments().getInt("fragContainer");
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }





    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_calendar, container, false);




        model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        apiUtils = new ApiUtils(getActivity());
        calendarContainer = v.findViewById(R.id.calendarContainer);
        loadProgressBar = v.findViewById(R.id.load_progress);
        recyclerView = v.findViewById(R.id.minimal_event_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        calendarView = v.findViewById(R.id.calendarView);
        noEventTextView = v.findViewById(R.id.no_event_tv);
        listContainer = v.findViewById(R.id.list_container);
        addNewEvent2Btn = v.findViewById(R.id.add_new_event2_btn);
        addNewEventBtn = v.findViewById(R.id.add_new_event_btn);
        loadEvents();



        addNewEventBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAddEvent();
            }
        });

        addNewEvent2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireAddEvent();
            }
        });
        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


    public void loadEvents() {
        showProgress(true);
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("EVENTS:"+response);
                try {
                    JSONArray events = response.getJSONArray("events");
                    for(int i=0;i<events.length();i++){
                        JSONObject event = events.getJSONObject(i);
                        Event nEvent = new Event(event);
                        System.out.println("Event:"+nEvent.getStatus());
                        parishEvents.add(nEvent);
                        model.setElist(parishEvents);
                        loadEventsToCalendar();
                        displayEvents(Calendar.getInstance());
                        ((MainActivity)getActivity()).setEventAlarms();
                        showProgress(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.getEvents(jhtrh);
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            calendarContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            calendarContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    calendarContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loadProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            loadProgressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadProgressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            calendarContainer.setVisibility(show ? View.VISIBLE : View.GONE);
            loadProgressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


    public void loadEventsToCalendar(){
        events = new ArrayList<>();
        for(int i=0;i<parishEvents.size();i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parishEvents.get(i).getTimeStartDate());
            EventDay event = new EventDay(calendar, R.drawable.ic_event_note_black_24dp);
            events.add(event);
        }

        calendarView.setEvents(events);
        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Date currentDate = Calendar.getInstance().getTime();
                clickedDate = eventDay.getCalendar().getTime();
                if(clickedDate.getTime() < currentDate.getTime()){
                    clickedDate = currentDate;
                }
                displayEvents(eventDay.getCalendar());
            }

        });

        Calendar min = Calendar.getInstance();
        min.add(Calendar.DATE, -1);
        calendarView.setMinimumDate(min);

    }


    public ArrayList<Event> getEventsForDate(Calendar date){
        ArrayList<Event> eventForDate = new ArrayList<>();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String pickedDate = formatter.format(date.getTime());
        for(int i=0;i<parishEvents.size();i++){
            String eventDate = formatter.format(parishEvents.get(i).getTimeStartDate());
            if (pickedDate.equals(eventDate)){
                eventForDate.add(parishEvents.get(i));
            }
        }
        return eventForDate;
    }

    public void fireAddEvent(){
        ((MainActivity)getActivity()).fireAddEventFragment(clickedDate);
    }


    public void displayEvents(Calendar eventDate){
        ArrayList<Event> eventList = getEventsForDate(eventDate);
        if (eventList.size() > 0){
            adapter = new MyMinimalEventListRecyclerViewAdapter(eventList, getContext());
            recyclerView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            noEventTextView.setVisibility(View.GONE);
            listContainer.setVisibility(View.VISIBLE);
        }else{
            noEventTextView.setVisibility(View.VISIBLE);
            listContainer.setVisibility(View.GONE);
        }
    }







}
