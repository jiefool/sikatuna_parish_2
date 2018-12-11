package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanginan.www.sikatuna_parish.EventListFragment.OnListFragmentInteractionListener;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent.DummyItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.Header;


/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyEventListRecyclerViewAdapter extends RecyclerView.Adapter<MyEventListRecyclerViewAdapter.ViewHolder> implements Filterable {

    private List<Event> mValues;
    private List<Event> mValuesFiltered;
    private final OnListFragmentInteractionListener mListener;
    private Context context;
    ApiUtils apiUtils;
    DialogFragment dialogFragment;
    CurrentUser currentUser;
    Boolean isFiltered = false;

    public MyEventListRecyclerViewAdapter(List<Event> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mValuesFiltered = items;
        mListener = listener;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_eventlist, parent, false);
        apiUtils = new ApiUtils(context);
        currentUser = new CurrentUser(context);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.dateMonth.setText(mValuesFiltered.get(position).getDateMonth());
        holder.dateDay.setText(mValuesFiltered.get(position).getDateDay());
        holder.eventName.setText(mValuesFiltered.get(position).getName());
        holder.timeStart.setText(mValuesFiltered.get(position).getTimeStart());
        holder.timeEnd.setText(mValuesFiltered.get(position).getTimeEnd());
        holder.details.setText(mValuesFiltered.get(position).getDetails());
        holder.status.setText(mValuesFiltered.get(position).getStatus());

        if (currentUser.getType().equals("secretary")){
            holder.rejectBtn.setVisibility(View.GONE);
            holder.confirmBtn.setVisibility(View.GONE);
            holder.deleteBtn.setVisibility(View.GONE);
        }else{
            holder.editBtn.setVisibility(View.GONE);
        }

        if(mValuesFiltered.get(position).getStatus().toLowerCase().equals("confirmed")){
            holder.status.setTextColor(Color.GREEN);
            holder.confirmBtn.setVisibility(View.GONE);
            holder.rejectBtn.setVisibility(View.GONE);
            holder.editBtn.setVisibility(View.GONE);
        }else if(mValuesFiltered.get(position).getStatus().toLowerCase().equals("rejected")) {
            holder.status.setTextColor(Color.RED);
            holder.confirmBtn.setVisibility(View.GONE);
            holder.rejectBtn.setVisibility(View.GONE);
            holder.editBtn.setVisibility(View.GONE);
        }else if(mValuesFiltered.get(position).getStatus().toLowerCase().equals("pending")){
            holder.editBtn.setVisibility(View.VISIBLE);
        }else{
            holder.status.setTextColor(Color.RED);
        }

        if(mValuesFiltered.get(position).getStatus().toLowerCase().equals("pending") && currentUser.getType().equals("priest")) {
            holder.confirmBtn.setVisibility(View.VISIBLE);
            holder.rejectBtn.setVisibility(View.VISIBLE);
            holder.editBtn.setVisibility(View.GONE);
        }

        holder.confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showProgress(true);
                JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println(response);
                        holder.showProgress(false);
                        try {
                            mValuesFiltered.set(position, new Event(response.getJSONObject("event")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                    }
                };
                apiUtils.eventStatusUpdate(mValuesFiltered.get(position).getId(), "Confirmed", jhrh);
            }
        });

        holder.rejectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.FragmentTransaction ft = ((Activity)context).getFragmentManager().beginTransaction();
                android.app.Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                dialogFragment = RejectEventFragment.newInstance(mValuesFiltered.get(position));
                dialogFragment.show(ft, "dialog");

                holder.showProgress(true);
                JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println(response);
                        holder.showProgress(false);
                        try {
                            mValuesFiltered.set(position, new Event(response.getJSONObject("event")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                    }
                };


                apiUtils.eventStatusUpdate(mValuesFiltered.get(position).getId(), "Rejected", jhrh);
            }


        });

        holder.deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.showProgress(true);
                JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        ArrayList<Event> eList = new ArrayList<>();
                        for(int i=0;i<mValuesFiltered.size();i++){
                            if(i==position){
                                continue;
                            }else{
                                eList.add(mValuesFiltered.get(i));
                            }
                        }
                        mValuesFiltered = eList;
                        notifyDataSetChanged();
                        holder.showProgress(false);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);

                    }
                };
                apiUtils.deleteEvent(mValuesFiltered.get(position).getId(), jhrh);
            }
        });


        holder.editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)context).editEvent(mValuesFiltered.get(position));
            }
        });

        holder.saveEventToDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                android.app.FragmentTransaction ft = ((Activity)context).getFragmentManager().beginTransaction();
                android.app.Fragment prev = ((Activity) context).getFragmentManager().findFragmentByTag("dialog");
                if (prev != null) {
                    ft.remove(prev);
                }
                ft.addToBackStack(null);

                    dialogFragment = SaveEventFragment.newInstance(mValuesFiltered.get(position));

                dialogFragment.show(ft, "dialog");
            }
        });



    }

    @Override
    public int getItemCount() {
        return mValuesFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence status) {
                if (status.toString().isEmpty()) {
                    mValuesFiltered = mValues;
                } else {
                    List<Event> filteredList = new ArrayList<>();
                    for (Event row : mValues) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getStatus().toLowerCase().contains(status.toString().toLowerCase())) {
                            filteredList.add(row);
                        }
                    }

                    mValuesFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mValuesFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                mValuesFiltered = (ArrayList<Event>)filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView dateMonth;
        public final TextView dateDay;
        public final TextView eventName;
        public final TextView timeStart;
        public final TextView timeEnd;
        public final TextView details;
        public final TextView status;
        public final View actionLayout;
        public Button confirmBtn;
        public Button rejectBtn;
        public ProgressBar updateProgress;
        public LinearLayout cardLayout;
        public Button deleteBtn;
        public Button saveEventToDoc;
        public Button editBtn;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            dateMonth = (TextView) view.findViewById(R.id.date_month);
            dateDay = (TextView) view.findViewById(R.id.date_day);
            eventName = (TextView) view.findViewById(R.id.event_name);
            timeStart = (TextView) view.findViewById(R.id.start_time);
            timeEnd = (TextView) view.findViewById(R.id.end_time);
            details = (TextView) view.findViewById(R.id.details);
            status = (TextView) view.findViewById(R.id.status);
            actionLayout = view.findViewById(R.id.action_layout);
            confirmBtn = view.findViewById(R.id.confirm_btn);
            rejectBtn = view.findViewById(R.id.reject_btn);
            updateProgress = view.findViewById(R.id.update_progress);
            cardLayout = view.findViewById(R.id.card_layout);
            deleteBtn = view.findViewById(R.id.delete_btn);
            saveEventToDoc = view.findViewById(R.id.save_btn);
            editBtn = view.findViewById(R.id.edit_btn);
        }

        public void showProgress(final boolean show) {
            // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
            // for very easy animations. If available, use these APIs to fade-in
            // the progress spinner.
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
                int shortAnimTime = context.getResources().getInteger(android.R.integer.config_shortAnimTime);

                cardLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                cardLayout.animate().setDuration(shortAnimTime).alpha(
                        show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        cardLayout.setVisibility(show ? View.GONE : View.VISIBLE);
                    }
                });

                updateProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                updateProgress.animate().setDuration(shortAnimTime).alpha(
                        show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        updateProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                    }
                });
            } else {
                // The ViewPropertyAnimator APIs are not available, so simply show
                // and hide the relevant UI components.
                updateProgress.setVisibility(show ? View.VISIBLE : View.GONE);
                cardLayout.setVisibility(show ? View.GONE : View.VISIBLE);
            }
        }


    }


}
