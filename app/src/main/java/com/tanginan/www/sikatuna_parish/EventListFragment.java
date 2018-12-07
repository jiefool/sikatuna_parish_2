package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent.DummyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnListFragmentInteractionListener}
 * interface.
 */
public class EventListFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    ArrayList<Event> elist;
    List<Event> eventList;
    MyEventListRecyclerViewAdapter adapter;
    EventViewModel model;
    RecyclerView recyclerView;
    ProgressBar loadProgress;
    LinearLayout eventListContainer;
    ApiUtils apiUtils;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public EventListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static EventListFragment newInstance(int columnCount) {
        EventListFragment fragment = new EventListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_eventlist_list, container, false);

        model = ViewModelProviders.of((MainActivity)getActivity()).get(EventViewModel.class);
        loadProgress = view.findViewById(R.id.loading_progress);
        eventListContainer = view.findViewById(R.id.event_list_container);
        apiUtils = new ApiUtils(getActivity());
        loadEvents();


        recyclerView = (RecyclerView) view.findViewById(R.id.list);
        Context context = view.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context));



        RadioButton confirmRb = view.findViewById(R.id.confirm_rb);
        RadioButton pendingRb = view.findViewById(R.id.pending_rb);
        RadioButton rejectedRb = view.findViewById(R.id.rejected_rb);
        RadioButton allRb = view.findViewById(R.id.all_rb);

        confirmRb.setOnClickListener(new GenericOnClickListener(confirmRb));
        pendingRb.setOnClickListener(new GenericOnClickListener(pendingRb));
        rejectedRb.setOnClickListener(new GenericOnClickListener(rejectedRb));
        allRb.setOnClickListener(new GenericOnClickListener(allRb));

        // Set the adapter
//        if (view instanceof RecyclerView) {
//            Context context = view.getContext();
//            RecyclerView recyclerView = (RecyclerView) view;
//            if (mColumnCount <= 1) {
//                recyclerView.setLayoutManager(new LinearLayoutManager(context));
//            } else {
//                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
//            }
//            recyclerView.setAdapter(new MyEventListRecyclerViewAdapter(model.getData(), mListener));
//        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnListFragmentInteractionListener {
        // TODO: Update argument type and name
        void onListFragmentInteraction(Event item);

    }

    private class GenericOnClickListener implements View.OnClickListener {
        View view;
        public GenericOnClickListener(View view) {
            this.view = view;
        }

        @Override
        public void onClick(View v) {
            switch(view.getId()){
                case R.id.confirm_rb:
                    adapter.getFilter().filter("confirmed");
                    break;
                case R.id.pending_rb:
                    adapter.getFilter().filter("pending");
                    break;
                case R.id.rejected_rb:
                    adapter.getFilter().filter("rejected");
                    break;
                case R.id.all_rb:
                    adapter.getFilter().filter("");
                    break;
            }

            adapter.notifyDataSetChanged();


        }
    }

    public void loadEvents() {
        showProgress(true);
        JsonHttpResponseHandler jhtrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("EVENTS:"+response);
                try {
                    JSONArray events = response.getJSONArray("events");
                    elist = new ArrayList<Event>();
                    for(int i=0;i<events.length();i++){
                        JSONObject event = events.getJSONObject(i);
                        Event nEvent = new Event(event);
                        System.out.println("Event:"+nEvent.getStatus());
                        elist.add(nEvent);
                        model.setElist(elist);

                        adapter = new MyEventListRecyclerViewAdapter(elist, mListener, getContext());
                        recyclerView.setAdapter(adapter);

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

            eventListContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            eventListContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    eventListContainer.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            loadProgress.setVisibility(show ? View.VISIBLE : View.GONE);
            loadProgress.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loadProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            eventListContainer.setVisibility(show ? View.VISIBLE : View.GONE);
            loadProgress.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
