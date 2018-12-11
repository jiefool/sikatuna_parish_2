package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import cz.msebera.android.httpclient.Header;

public class RejectEventFragment extends DialogFragment {
    static JSONObject eventJson;
    View view;
    DialogFragment frag;
    EditText reasonEditText;
    LinearLayout formContainer;
    ProgressBar progressBar;
    ApiUtils apiUtils;

    public static RejectEventFragment newInstance(Event event) {
        eventJson = event.getJSONObject();
        RejectEventFragment fragment = new RejectEventFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_reject_event, container, false);
        frag = this;

        reasonEditText = view.findViewById(R.id.reject_reason_et);
        Button button = view.findViewById(R.id.reject_btn);
        progressBar = view.findViewById(R.id.progress_bar);
        formContainer = view.findViewById(R.id.form_container);
        apiUtils = new ApiUtils(getContext());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(reasonEditText.getText().length() <= 0){
                    reasonEditText.requestFocus();
                    reasonEditText.setError("Field is required.");
                }


                showProgress(true);
                JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println(response);
                         showProgress(false);
                         frag.dismiss();

                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                        super.onFailure(statusCode, headers, throwable, errorResponse);
                    }
                };

                String reason = "Reason: "+reasonEditText.getText();
                RequestParams params = new RequestParams();
                try {
                    params.add("name", eventJson.getString("name"));
                    params.add("time_start", eventJson.getString("time_start"));
                    params.add("time_end", eventJson.getString("time_end"));
                    params.add("alarm", eventJson.getString("alarm"));
                    params.add("details", reason);
                    params.add("user_id", eventJson.getString("user_id"));
                    params.add("is_confirmed", "false");
                    apiUtils.updateEvent(eventJson.getInt("id"), params, jhrh);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
        return view;
    }

    public void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getContext().getResources().getInteger(android.R.integer.config_shortAnimTime);

            formContainer.setVisibility(show ? View.GONE : View.VISIBLE);
            formContainer.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formContainer.setVisibility(show ? View.GONE : View.VISIBLE);
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
            progressBar.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }
}
