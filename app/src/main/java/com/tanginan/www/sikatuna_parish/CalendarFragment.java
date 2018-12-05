package com.tanginan.www.sikatuna_parish;

import android.arch.lifecycle.ViewModelProviders;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class CalendarFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EventViewModel model;
    List<EventDay> events;



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


        model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        List<Event> parishEvents = model.getEventData();

        events = new ArrayList<>();

        for(int i=0;i<parishEvents.size();i++){
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(parishEvents.get(i).getTimeStartDate());
            EventDay event = new EventDay(calendar, R.drawable.ic_event_note_black_24dp);
            events.add(event);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        View v = inflater.inflate(R.layout.fragment_calendar, container, false);
        CalendarView calendarView = v.findViewById(R.id.calendarView);
        calendarView.setEvents(events);
        Calendar min = Calendar.getInstance();
        min.add(Calendar.DATE, -1);
        calendarView.setMinimumDate(min);


        calendarView.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                ((MainActivity)getActivity()).fireAddEventFragment();
            }
        });

        return v;
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }


}
