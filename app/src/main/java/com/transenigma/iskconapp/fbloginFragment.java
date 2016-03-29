package com.transenigma.iskconapp;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class fbloginFragment extends Fragment {
    ProgressDialog progressDialog;
    LoginButton loginButton;
    String email, dob;
    Boolean cancelFlag = false, doSignUpFlag = false, homeFlag = false, signUpFirstTimeByFB = false;
    Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        return inflater.inflate(R.layout.fragment_fblogin, container, false);
    }

    private CallbackManager callbackManager;
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "Available");
            return true;
        }

        Log.e("Network Testing", "Not Available");
        return false;
    }

    private FacebookCallback<LoginResult> callback = new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        email = object.optString("email");
                        dob = object.optString("birthday");
                        Log.i("Radhe", response.toString());
                        Log.i("Radhe", object.toString() + "  " + email + "  " + dob);
                        if (Profile.getCurrentProfile() == null) {
                            doSignUpFlag = true;
                            profileTracker = new ProfileTracker() {
                                @Override
                                protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                                    this.stopTracking();
                                    Profile.setCurrentProfile(newProfile);
                                    //createNewUser(email, dob, newProfile);
                                }
                            };
                            profileTracker.startTracking();
                        } else createNewUser(email, dob, Profile.getCurrentProfile());
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email,gender, birthday");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.i("Radhe", "Cancelled");
                cancelFlag = true;
            }

            @Override
            public void onError(FacebookException e) {
                Log.i("Radhe", "Error = " + e);
                startActivity(new Intent(getActivity().getApplicationContext(), start.class));
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                getActivity().finish();
            }
        };

    private void createNewUser(final String email, String dob, Profile profile) {

        progressDialog = ProgressDialog.show( mContext, "Please Wait!", "Authenticating", true);
        if (profile != null) {
            ParseUser user = new ParseUser();
            if(!email.equals("")) {
                user.setUsername(email);
                user.setPassword(email);
                user.put("email",email);
            }
            else{
                user.setUsername(profile.getId().toString());
                user.setPassword(profile.getId().toString());
            }

            user.put("legalname", profile.getFirstName());
            user.put("surname", profile.getLastName());

            SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy");
            Log.i("Radhe", "Hari Bol inside createNewUser");
            try {
                Date date = formatter.parse(dob);
                Log.i("Radhe", date.toString());
                Log.i("Radhe", formatter.format(date));
                user.put("dob",date);
            } catch (java.text.ParseException e) {
                Log.i("Radhe", e.toString());
                e.printStackTrace();
            }

            user.signUpInBackground(new SignUpCallback() {
                public void done(ParseException e) {
                    if (e == null) {
                        Log.i("Radhe", "Radhe! Parse signup by fb is success");
                        startActivity(new Intent(mContext, home.class));
                        Toast.makeText(mContext,"Log In Success. Welcome "+ParseUser.getCurrentUser().getString("legalname"),Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        progressDialog.dismiss();
                    } else {
                        e.printStackTrace();
                        Log.i("Radhe", "Radhe! Parse signup by fb is failure due to " + e);
                        ParseUser.logInInBackground(email,email, new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    Log.i("Radhe", "Radhe! Parse login is success user is "+ParseUser.getCurrentUser().getString("legalname"));
                                    startActivity(new Intent(mContext, home.class));
                                    Toast.makeText(mContext,"Log In Success. Welcome "+ParseUser.getCurrentUser().getString("legalname"),Toast.LENGTH_LONG).show();
                                    getActivity().finish();
                                    progressDialog.dismiss();
                                } else {
                                    Log.i("Radhe", "Radhe! Parse login is failure due to "+e.toString());
                                    LoginManager.getInstance().logOut();
                                    Toast.makeText(mContext,"Log In Failed",Toast.LENGTH_LONG).show();
                                    progressDialog.dismiss();
                                }
                            }
                        });
                    }
                }
            });
        }
        else {
            Log.i("Radhe", "Profile is nill");
        }
    }

    private OnFragmentInteractionListener mListener;

    public fbloginFragment() {
        mContext = getActivity();
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        mContext = getActivity();
        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {
                this.stopTracking();
                Profile.setCurrentProfile(newProfile);
                if(doSignUpFlag) createNewUser(email,dob, newProfile);
                Log.i("Radhe", "So Hari it is coming here and doSignupFlag is "+ doSignUpFlag);
            }
        };
        accessTokenTracker.startTracking();
        profileTracker.startTracking();
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mContext = getActivity();
        loginButton = (LoginButton) view.findViewById(R.id.login_button);
        List<String> permissions = new ArrayList<String>();
        permissions.add("public_profile");
        permissions.add("email");
        permissions.add("user_birthday");
        permissions.add("user_friends");
        loginButton.setReadPermissions(permissions);
        loginButton.setFragment(this);
        loginButton.registerCallback(callbackManager, callback);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mContext = getActivity();
        callbackManager.onActivityResult(requestCode, resultCode, data);

            if (cancelFlag == true) {
                startActivity(new Intent(getActivity().getApplicationContext(), start.class));
                getActivity().finish();
            }
//            if(homeFlag){
//                startActivity(new Intent(getActivity().getApplicationContext(), home.class));
//                getActivity().finish();
//            }
//            if(signUpFirstTimeByFB){
//                Log.i("Radhe","Hare Krishna this is first time signup using FB");
//                startActivity(new Intent(getActivity().getApplicationContext(), home.class));
//                getActivity().finish();
//            }
            Log.i("Radhe","Hare Krishna We are in OnActivityResult()");
            return;
    }

    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }
    @Override
    public void onResume() {
        super.onResume();
    }
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }
    @Override
    public void onAttach(Context context) {
        mContext = context;
        super.onAttach(context);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

//    private void displayMessage(Profile profile) {
//        /****************************Save user to Parse***********************************/
//        if (profile != null) {
//            ParseUser user = new ParseUser();
//            user.setUsername(profile.getId().toString());
//            user.setPassword(profile.getId().toString());
//            user.put("legalname", profile.getFirstName());
//            user.put("surname", profile.getLastName());
//
//            user.signUpInBackground(new SignUpCallback() {
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.i("Radhe", "Radhe! Parse signup is success");
//                        startActivity(new Intent(getActivity().getApplicationContext(), home.class));
//                    } else {
//                        e.printStackTrace();
//                        Log.i("Radhe", "Radhe! Parse signup is failure " + e);
//                    }
//                }
//            });
//
//        }
//    }


}
