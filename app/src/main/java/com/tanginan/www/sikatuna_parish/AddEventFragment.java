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
import android.widget.TextView;
import android.widget.TimePicker;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddEventFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddEventFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddEventFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
    Button createEvent;
    ProgressBar createEventDialog;
    LinearLayout layoutContainer;
    EventViewModel model;


    private OnFragmentInteractionListener mListener;

    public AddEventFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddEventFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddEventFragment newInstance(String param1, String param2) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_add_event, container, false);

        model = ViewModelProviders.of(getActivity()).get(EventViewModel.class);
        priests = model.getPriestData();

        etName = view.findViewById(R.id.name);
        etTimeStart = view.findViewById(R.id.start_datetime);
        etTimeStart.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDateTimePicker(etTimeStart);
                }
            }
        });


        etTimeEnd = view.findViewById(R.id.end_datetime);
        etTimeEnd.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    showDateTimePicker(etTimeEnd);
                }
            }
        });

        etAlarm = view.findViewById(R.id.alarm);
        etDetails = view.findViewById(R.id.details);
        apiUtils = new ApiUtils(getContext());
        priestSelect = view.findViewById(R.id.priestSpinner);
        priestList = new PriestList(priests);

        ArrayList<String> priestNames = priestList.getPriestNames();
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_spinner_dropdown_item, priestNames);
        priestSelect.setAdapter(adapter);

        createEvent = view.findViewById(R.id.create_event_btn);
        createEvent.setOnClickListener(new View.OnClickListener() {
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
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
            etTimeStart.setError("This field is required.");
        }
        String timeEnd = etTimeEnd.getText().toString();
        if(timeEnd.length()<=0){
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

        String details = etAlarm.getText().toString();



        Priest priest = priestList.getPriestByName(priestSelect.getSelectedItem().toString());

        if(valid){
            showProgress(true);
            JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    System.out.println(response);
                    showProgress(false);
                    model.loadData(getActivity());

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
            params.add("status", "Pending");

            apiUtils.createEvent(params, jhrh);

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
