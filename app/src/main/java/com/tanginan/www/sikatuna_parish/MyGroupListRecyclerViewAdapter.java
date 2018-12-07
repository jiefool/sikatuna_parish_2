package com.tanginan.www.sikatuna_parish;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tanginan.www.sikatuna_parish.GroupListFragment.OnListFragmentInteractionListener;
import com.tanginan.www.sikatuna_parish.dummy.DummyContent.DummyItem;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnListFragmentInteractionListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class MyGroupListRecyclerViewAdapter extends RecyclerView.Adapter<MyGroupListRecyclerViewAdapter.ViewHolder> {

    private final List<Group> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyGroupListRecyclerViewAdapter(List<Group> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
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


        public ViewHolder(View view) {
            super(view);
            mView = view;
            groupNameView = (TextView) view.findViewById(R.id.group_name_tv);
            groupLeaderView = (TextView) view.findViewById(R.id.group_leader_tv);
            contactNumber = (TextView) view.findViewById(R.id.contact_number_tv);
        }
    }
}
