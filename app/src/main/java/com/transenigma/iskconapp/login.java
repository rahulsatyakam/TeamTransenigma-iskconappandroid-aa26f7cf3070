package com.transenigma.iskconapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class login extends AppCompatActivity {

    Context context = this;
    @Bind(R.id.myLoginEmail) EditText myLoginEmail;
    @Bind(R.id.myLoginPassword) EditText myLoginPassword;
    @Bind(R.id.myLoginBtn) Button myLoginBtn;
    @Bind(R.id.myLoginSignup) TextView myLoginSignup;
    AccountManager mAccountManager;

    //    @Bind(R.id.toolbarLogin)Toolbar toolbarLogin;
    ProgressDialog progressDialog;
    static ProgressDialog progressDialogLogin;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null){
            if(bundle.getBoolean("SignUpFlag")) ParseUser.logOut();
        }
        //progressDialog1.dismiss();

        getSupportActionBar().setTitle("Login");
        // If current user is NOT anonymous user , Get current user data from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();
        if (currentUser != null)
        {
            Log.i("Radhe","Hari user is already logged in!!!!!!!");
            startActivity(new Intent(this, home.class));
            Toast.makeText(getApplicationContext(),"Welcome " + currentUser.getString("legalname"),Toast.LENGTH_LONG).show();
            finish();
        }else {
            Log.i("Radhe", "No current user!!!!!!!!!!");
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fbloginFragment fblogin = new fbloginFragment();
        fragmentTransaction.add(R.id.fragment_container, fblogin ,"fblogin");
        fragmentTransaction.commit();
    }

    public void clickForgotPass(View view){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password Recovery");
        alert.setPositiveButton("Recover Password via Email", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int whichButton) {

                AlertDialog.Builder alertEmail = new AlertDialog.Builder(context);
                alertEmail.setTitle("Enter your Email");
                final EditText input = new EditText(context);
                input.setHint("Email Address");
                alertEmail.setView(input);
                alertEmail.setPositiveButton("OK", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParseUser.requestPasswordResetInBackground(input.getText().toString(), new RequestPasswordResetCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Toast.makeText(getApplicationContext(), "Check Inbox for the password", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Email not correct"+e, Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
                alertEmail.show();

            }

        });
//        alert.setNegativeButton("Reset Password", new DialogInterface.OnClickListener() {
//
//            public void onClick(DialogInterface dialog, int whichButton) {
//                AlertDialog.Builder alertReset = new AlertDialog.Builder(context);
//                alertReset.setTitle("Type In New Password");
//                final EditText inputNewPass = new EditText(context);
//                alertReset.setView(inputNewPass);
//                alertReset.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//
//                        // Do something with value!
//                    }
//                });
//                alertReset.show();
//            }
//        });

        alert.show();
    }

    public void clickLoginBtn(View view) {
        if (!validate()) {
            onLoginFailed();
            return;
        }
        if(isConnectedToInternet()) {
            myLoginBtn.setEnabled(false);
            progressDialog = ProgressDialog.show(this, "Please Wait!", "Authenticating", true);
            ParseUser.logInInBackground(myLoginEmail.getText().toString(), myLoginPassword.getText().toString(), new LogInCallback() {
                public void done(ParseUser user, ParseException e) {
                    if (user != null) {
                        if (user.getBoolean("emailVerified")) {
                            Log.i("TAG", "Radhe! Parse login is success user is ");
                            progressDialog.dismiss();
                            startActivity(new Intent(getApplicationContext(), home.class));
                            Toast.makeText(getApplicationContext(), "Successfully Logged in. Welcome " + user.getString("legalname"), Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            ParseUser.logOut();
                            startActivity(new Intent(getApplicationContext(), login.class));
                            Toast.makeText(getApplicationContext(), "Please Verify your email", Toast.LENGTH_LONG).show();
                            finish();
                        }

                    } else {
                        Log.i("TAG", "Radhe! Parse login is failure");
                        progressDialog.dismiss();
                        myLoginBtn.setEnabled(true);
                        Toast.makeText(getApplicationContext(), "Log In Failed", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else{
            Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    public void onLoginSuccess() {
        myLoginBtn.setEnabled(true);
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        myLoginBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = myLoginEmail.getText().toString();
        String password = myLoginPassword.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            myLoginEmail.setError("enter a valid email address");
            valid = false;
        } else {
            myLoginEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 25) {
            myLoginPassword.setError("between 4 and 25 alphanumeric characters");
            valid = false;
        } else {
            myLoginPassword.setError(null);
        }
        return valid;
    }

    public void clickLoginSignup(View v) {
        startActivity(new Intent(this, signup.class));
    }

    public void clickGoogleSignInBtn(View view){
        progressDialogLogin = ProgressDialog.show( this, "Please Wait!", "Authenticating", true);
        syncGoogleAccount();
    }

    private String[] getAccountNames() {
        mAccountManager = AccountManager.get(this);
        Account[] accounts = mAccountManager.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE);
        String[] names = new String[accounts.length];
        for(int i = 0; i < names.length; i++) {
            names[i] = accounts[i].name;
        }
        return names;
    }

    private AbstractGetNameTask getTask(login activity, String email, String scope) {
        return new GetNameInForeground(activity, email, scope);
    }

    public void syncGoogleAccount() {
        if(isNetworkAvailable() == true) {
            String[] accountarrs = getAccountNames();
            if(accountarrs.length > 0) {
                getTask(this, accountarrs[0], SCOPE).execute();
            } else {
                Toast.makeText(login.this, "No Google Account Sync!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(login.this, "No Network Service!", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if(networkInfo != null && networkInfo.isConnected()) {
            Log.e("Network Testing", "Available");
            return true;
        }

        Log.e("Network Testing", "Not Available");
        return false;
    }
    public boolean isConnectedToInternet(){
        ConnectivityManager connectivity = (ConnectivityManager)getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }
}

