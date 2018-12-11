package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.AlertDialog;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TimePicker;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EditEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EditEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EditEventFragment extends Fragment {

    private static Event thisEvent;
    ApiUtils apiUtils;

    EditText etName;
    EditText etTimeStart;
    EditText etTimeEnd;
    EditText etAlarm;
    EditText etDetails;
    Spinner priestSelect;
    List<Priest> priests;
    ArrayAdapter adapter;
    PriestList priestList;
    Long time;
    Button updateEvent;
    ProgressBar createEventDialog;
    LinearLayout layoutContainer;
    EventViewModel model;
    CurrentUser currentUser;
    List<Event> eventList;


    private OnFragmentInteractionListener mListener;


    public static EditEventFragment newInstance(Event event) {
        EditEventFragment fragment = new EditEventFragment();
        Bundle args = new Bundle();
        thisEvent = event;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_event, container, false);
        model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        eventList = model.getEventData();
        priests = model.getPriestData();

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        etName = view.findViewById(R.id.name);
        etName.setText(thisEvent.getName());
        etName.requestFocus();

        etTimeStart = view.findViewById(R.id.start_datetime);
        etTimeStart.setText(formatter.format(thisEvent.getTimeStartDate()));
        etTimeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDateTimePicker(etTimeStart);
                }
            }
        });


        etTimeEnd = view.findViewById(R.id.end_datetime);
        etTimeEnd.setText(formatter.format(thisEvent.getTimeEndDate()));
        etTimeEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDateTimePicker(etTimeEnd);
                }
            }
        });

        etAlarm = view.findViewById(R.id.alarm);
        etAlarm.setText(thisEvent.getAlarmString());


        etDetails = view.findViewById(R.id.details);
        etDetails.setText(thisEvent.getDetails());

        apiUtils = new ApiUtils(getContext());
        priestList = new PriestList(priests);

        ArrayList<String> priestNames = priestList.getPriestNames();
        priestSelect = view.findViewById(R.id.priestSpinner);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, priestNames);
        priestSelect.setAdapter(adapter);
        int spinnerPosition = adapter.getPosition(priestList.getPriestNameByUserId(thisEvent.getUserId()));
        priestSelect.setSelection(spinnerPosition);

        updateEvent = view.findViewById(R.id.update_event_btn);
        updateEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    validateAndCreateEvent();
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        createEventDialog = view.findViewById(R.id.create_event_progress);
        layoutContainer = view.findViewById(R.id.create_event_container);
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void showDateTimePicker(EditText editText){
        final View dialogView = View.inflate(getActivity(), R.layout.date_time_picker, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();

        Button setBtn = dialogView.findViewById(R.id.date_time_set);
        setBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                datePicker.setMinDate(System.currentTimeMillis() - 1000);
                TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                        datePicker.getMonth(),
                        datePicker.getDayOfMonth(),
                        timePicker.getCurrentHour(),
                        timePicker.getCurrentMinute());

                time = calendar.getTimeInMillis();
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                editText.setText(formatter.format(time));
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();

    }


    public void validateAndCreateEvent() throws JSONException, ParseException {
        String eventName = etName.getText().toString();
        Boolean valid = true;
        if(eventName.length()<=0){
            etName.requestFocus();
            etName.setError("Event name is required.");
            valid = false;

        }

        String timeStart = etTimeStart.getText().toString();
        if(timeStart.length()<=0){
            etTimeStart.requestFocus();
            etTimeStart.setError("This field is required.");
        }


        String timeEnd = etTimeEnd.getText().toString();
        if(timeEnd.length()<=0){
            etTimeEnd.requestFocus();
            etTimeEnd.setError("This field is required.");
        }
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Long timeStartInt = formatter.parse(timeStart).getTime();
        Long timeEndInt = formatter.parse(timeEnd).getTime();

        if (timeEndInt < timeStartInt ){
            etTimeEnd.requestFocus();
            etTimeEnd.setError("End time should be later than start time.");
            valid = false;
        }

        String alarm = etAlarm.getText().toString();

        String details = etDetails.getText().toString();



        Priest priest = priestList.getPriestByName(priestSelect.getSelectedItem().toString());

        if(valid){
            showProgress(true);
            JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(response);
                    ((MainActivity)getActivity()).fireEventListFragment();
                    showProgress(false);

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    super.onFailure(statusCode, headers, throwable, errorResponse);
                }
            };
            RequestParams params = new RequestParams();
            params.add("name", eventName);
            params.add("time_start", timeStart);
            params.add("time_end", timeEnd);
            params.add("alarm", alarm);
            params.add("details", details);
            params.add("user_id", priest.getId().toString());
            params.add("is_confirmed", "false");
            params.add("status", thisEvent.getStatus());

            apiUtils.updateEvent(thisEvent.getId(), params, jhrh);

        }
    }

    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getActivity().getResources().getInteger(android.R.integer.config_shortAnimTime);

            layoutContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            layoutContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    layoutContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            createEventDialog.setVisibility(show ? View.VISIBLE : View.GONE);
            createEventDialog.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    createEventDialog.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            createEventDialog.setVisibility(show ? View.VISIBLE : View.GONE);
            layoutContainer.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
