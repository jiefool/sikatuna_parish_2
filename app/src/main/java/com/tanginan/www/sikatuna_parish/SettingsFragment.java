package com.tanginan.www.sikatuna_parish;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link SettingsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    TextView responseText;
    String userIdRes = "";
    String responseRes = "";
    CurrentUser currentUser;
    ApiUtils apiUtils;
    EditText name;
    EditText username;
    EditText email;
    EditText currentPassword;
    EditText newPassword;
    EditText confirmPassword;
    ImageView userPhoto;
    LinearLayout fieldsContainer;
    ProgressBar progressBar;
    View view;
    Button updateUserBtn;

    private OnFragmentInteractionListener mListener;

    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SettingsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_settings, container, false);

        currentUser = new CurrentUser(getContext());
        apiUtils = new ApiUtils(getContext());
        responseText = view.findViewById(R.id.response_text);
        name = view.findViewById(R.id.name_input);
        username = view.findViewById(R.id.username_input);
        email = view.findViewById(R.id.email_input);
        currentPassword = view.findViewById(R.id.current_password_input);
        newPassword = view.findViewById(R.id.new_password_input);
        confirmPassword = view.findViewById(R.id.confirm_password_input);
        userPhoto = view.findViewById(R.id.user_photo);
        fieldsContainer = view.findViewById(R.id.field_container);
        progressBar = view.findViewById(R.id.loading_progress);
        responseText = view.findViewById(R.id.response_text);
        updateUserBtn = view.findViewById(R.id.update_user);
        updateUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
            }
        });
        getUserDetails();


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

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {
            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new java.net.URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

    public void updateUser() {
        Boolean valid = true;
        String nameStr = name.getText().toString();
        String usernameStr = username.getText().toString();
        String emailStr = email.getText().toString();
        String currentPasswordStr = currentPassword.getText().toString();

        if (currentPasswordStr.length() == 0){
            valid = false;
            currentPassword.requestFocus();
            currentPassword.setError("This field is required.");
        }
        String newPasswordStr = newPassword.getText().toString();
        if (newPasswordStr.length() == 0){
            valid = false;
            newPassword.requestFocus();
            newPassword.setError("This field is required.");
        }

        String confirmPasswordStr = confirmPassword.getText().toString();
        if (confirmPasswordStr.length() == 0){
            valid = false;
            confirmPassword.requestFocus();
            confirmPassword.setError("This field is required.");
        }

        if (!confirmPasswordStr.equals(newPasswordStr)){
            valid = false;
            confirmPassword.requestFocus();
            confirmPassword.setError("Does not match with new password.");
        }



        if(valid){
            showProgress(true);
            String userId =  currentUser.getUserId();
            RequestParams params = new RequestParams();
            params.add("name", nameStr);
            params.add("username", usernameStr);
            params.add("email", emailStr);
            params.add("current_password", currentPasswordStr);
            params.add("password", newPasswordStr);
            params.add("password_confirmation", confirmPasswordStr);

            JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    responseText.requestFocus();
                    responseText.setText(response.toString());
                    showProgress(false);
                    getUserDetails();

                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    showProgress(false);
                    getUserDetails();
                    responseText.requestFocus();
                    responseText.setText("Error updating details.");
                }
            };

            apiUtils.updateUser(userId, params, jhrh);
        }

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


    public  void getUserDetails(){
        showProgress(true);
        JsonHttpResponseHandler jhrh = new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    currentUser.setDataToSharedPreferences("user_id", response.getString("id"));
                    currentUser.setDataToSharedPreferences("username", response.getString("username"));
                    currentUser.setDataToSharedPreferences("name", response.getString("name"));
                    currentUser.setDataToSharedPreferences("photo", response.getString("photo"));

                    name.setText(response.getString("name"));
                    username.setText( response.getString("username"));
                    email.setText(currentUser.getEmail());

                    String photoUrl = "http://tanginan.com/images/"+currentUser.getUserPhoto();


                    // show The Image in a ImageView
                    new DownloadImageTask((ImageView) view.findViewById(R.id.user_photo))
                            .execute(photoUrl);

                    showProgress(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                showProgress(false);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {

            }
        };

        apiUtils.getUserDetails(currentUser.getEmail(), jhrh);
    }
}
