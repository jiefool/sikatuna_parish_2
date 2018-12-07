package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
public class MyMinimalEventListRecyclerViewAdapter extends RecyclerView.Adapter<MyMinimalEventListRecyclerViewAdapter.ViewHolder> {

    private List<Event> mValues;
    private Context context;

    public MyMinimalEventListRecyclerViewAdapter(List<Event> items, Context context) {
        mValues = items;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.minimal_event_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.eventName.setText(mValues.get(position).getName());
        holder.timeStart.setText(mValues.get(position).getTimeStart());
        holder.status.setText(mValues.get(position).getStatus());
        holder.month.setText(mValues.get(position).getDateMonth());
        holder.day.setText(mValues.get(position).getDateDay());

        System.out.println("statux:"+mValues.get(position).getStatus());

        if(mValues.get(position).getStatus().toLowerCase().equals("confirmed")){
            holder.status.setTextColor(Color.GREEN);
        }else{
            holder.status.setTextColor(Color.RED);
        }


    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final TextView eventName;
        public final TextView timeStart;
        public final TextView status;
        public final TextView month;
        public final TextView day;

        public ViewHolder(View view) {
            super(view);
            status = (TextView) view.findViewById(R.id.status);
            eventName = (TextView) view.findViewById(R.id.event_name);
            timeStart = (TextView) view.findViewById(R.id.start_time);
            month = (TextView) view.findViewById(R.id.month_tv);
            day = (TextView) view.findViewById(R.id.day_tv);
        }

    }


}
