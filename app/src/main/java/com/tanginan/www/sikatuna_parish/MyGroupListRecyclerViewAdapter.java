package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.os.Build;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.tanginan.www.sikatuna_parish.GroupListFragment.OnListFragmentInteractionListener;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent.DummyItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGroupListRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupListRecyclerViewAdapter.ViewHolder> {

    private final List<Group> mValues;
    private final OnListFragmentInteractionListener mListener;
    ApiUtils apiUtils;
    Context context;
    LinearLayout cardLayout;
    ProgressBar progressBar;

    public MyGroupListRecyclerViewAdapter(List<Group> items, OnListFragmentInteractionListener listener, Context context) {
        mValues = items;
        mListener = listener;
        apiUtils = new ApiUtils(context);
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_grouplist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.groupNameView.setText(mValues.get(position).getGroupName());
        holder.groupLeaderView.setText(mValues.get(position).getGroupLeader());
        holder.contactNumber.setText(mValues.get(position).getGroupContactNumber());
        holder.deleteGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteGroup(position);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView groupLeaderView;
        public final TextView groupNameView;
        public final TextView contactNumber;
        public final Button deleteGroup;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            groupNameView = (TextView) view.findViewById(R.id.group_name_tv);
            groupLeaderView = (TextView) view.findViewById(R.id.group_leader_tv);
            contactNumber = (TextView) view.findViewById(R.id.contact_number_tv);
            deleteGroup = (Button) view.findViewById(R.id.delete_group);
            cardLayout = view.findViewById(R.id.card_layout);
            progressBar = view.findViewById(R.id.loading_progress);

        }
    }

    public void deleteGroup(Integer position){
        showProgress(true);
        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                mValues.remove(position);
                notifyDataSetChanged();
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);

            }
        };
        apiUtils.deleteGroup(mValues.get(position).getGroupId(), jhrh);

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

            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
            cardLayout.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
