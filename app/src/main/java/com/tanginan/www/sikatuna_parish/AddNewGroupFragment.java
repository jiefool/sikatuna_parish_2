package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddNewGroupFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddNewGroupFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddNewGroupFragment extends Fragment {
    EditText groupName;
    EditText groupLeaderName;
    EditText groupContactNumber;
    ApiUtils apiUtils;
    ProgressBar progressBar;
    LinearLayout fieldsContainer;

    private OnFragmentInteractionListener mListener;

    public AddNewGroupFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddNewGroupFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddNewGroupFragment newInstance(String param1, String param2) {
        AddNewGroupFragment fragment = new AddNewGroupFragment();
        Bundle args = new Bundle();
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

        View v = inflater.inflate(R.layout.fragment_add_new_group, container, false);
        groupName = v.findViewById(R.id.group_name);
        groupLeaderName = v.findViewById(R.id.group_leader_name);
        groupContactNumber = v.findViewById(R.id.group_contact_number);
        progressBar = v.findViewById(R.id.progress_bar);
        fieldsContainer = v.findViewById(R.id.field_container);
        apiUtils = new ApiUtils(getContext());
        Button creatGroupBtn = v.findViewById(R.id.create_group);
        creatGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createGroup();
            }
        });

        Button clearBtn = v.findViewById(R.id.clear_fields);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFields();
            }
        });

        return v;
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

    public void createGroup() {
        Boolean valid = true;
        String groupNameStr = "";
        if (groupName.getText().length()>0){
           groupNameStr = groupName.getText().toString();
        }else{
            valid = false;
            groupName.requestFocus();
            groupName.setError("This is field is required.");
        }

        String groupLeaderNameStr = "";
        groupLeaderNameStr = groupLeaderName.getText().toString();
        String groupContactNumberStr = "";
        if (groupContactNumber.getText().length()>0){
            groupContactNumberStr = groupContactNumber.getText().toString();
        }else{
            valid = false;
            groupContactNumber.requestFocus();
            groupContactNumber.setError("This is field is required.");
        }

        if(valid) {
            showProgress(true);
            RequestParams params = new RequestParams();
            params.add("name", groupNameStr);
            params.add("leader", groupLeaderNameStr);
            params.add("contact_number", groupContactNumberStr);

            JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    showProgress(false);
                    ((MainActivity) getActivity()).fireListGroupFragment();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

                }
            };

            apiUtils.createGroup(params, jhrh);
        }
    }

    public void clearFields() {
        groupName.setText("");
        groupLeaderName.setText("");
        groupContactNumber.setText("");
    }

    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            fieldsContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            fieldsContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    fieldsContainer.setVisibility(show ? View.GONE : View.VISIBLE);
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
            fieldsContainer.setVisibility(show ? View.VISIBLE : View.GONE);
            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
