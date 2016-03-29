package com.transenigma.iskconapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.GoogleAuthUtil;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import butterknife.Bind;
import butterknife.ButterKnife;

public class signup extends AppCompatActivity {//implements DatePickerDialog.OnDateSetListener

    Context context = this;
    //Date dob;
    ProgressDialog progressDialog;
    static ProgressDialog progressDialogSignup;
    //    @Bind(R.id.mydob) EditText mydob;
    @Bind(R.id.mySignupName) EditText mySignupName;
    //    @Bind(R.id.mySignupSurname) EditText mySignupSurname;
//    @Bind(R.id.mySignupSpName) EditText mySignupSpName;
    @Bind(R.id.mySignupEmail) EditText mySignupEmail;
    @Bind(R.id.mySignupPass) EditText mySignupPass;
    @Bind(R.id.confirm_password) EditText confirm_password;
    //    @Bind(R.id.mySignupPhone) EditText mySignupPhone;
//    @Bind(R.id.mySpiritualMaster) EditText mySpiritualMaster;
    @Bind(R.id.mySignupBtn) Button mySignupBtn;
//    @Bind(R.id.link_login) TextView link_login;
//    @Bind(R.id.mySignupLin) LinearLayout mySignupLin;

    AccountManager mAccountManager;
    private static final String SCOPE = "oauth2:https://www.googleapis.com/auth/userinfo.profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        ButterKnife.bind(this);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fbloginFragment fblogin = new fbloginFragment();
        fragmentTransaction.add(R.id.fragment_container, fblogin ,"fblogin");
        fragmentTransaction.commit();
    }

    public void clickGoogleSignInBtn(View view){
        progressDialogSignup = ProgressDialog.show( this, "Please Wait!", "Authenticating", true);
        syncGoogleAccount();
    }

    public void syncGoogleAccount() {
        if(isNetworkAvailable() == true) {
            String[] accountarrs = getAccountNames();
            if(accountarrs.length > 0) {
                getTask(signup.this, accountarrs[0], SCOPE).execute();
            } else {
                Toast.makeText(signup.this, "No Google Account Sync!",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(signup.this, "No Network Service!", Toast.LENGTH_SHORT).show();
        }
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

    private AbstractGetNameTaskForSignup getTask(signup activity, String email, String scope) {
        return new com.transenigma.iskconapp.GetNameInForegroundForSignup(activity, email, scope);
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


    public void clickSignupLogin(View view) {
        startActivity(new Intent(this, login.class));
    }


    public void clickSignupBtn(View view) {
        if (!validate()) {
            onSignupFailed();
            return;
        }

        mySignupBtn.setEnabled(false);

        if(isConnectedToInternet()) {
            progressDialog = ProgressDialog.show(this, "Please Wait!", "Creating New Account", true);
            putUserToParse();
        }
        else{
            Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        mySignupBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        String name = mySignupName.getText().toString();
        String email = mySignupEmail.getText().toString();
        String password = mySignupPass.getText().toString();
        String rePassword = confirm_password.getText().toString();

        if (name.isEmpty() || name.length() < 2) {
            mySignupName.setError("at least 3 characters");
            valid = false;
        } else {
            mySignupName.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mySignupEmail.setError("enter a valid email address");
            valid = false;
        } else {

            mySignupEmail.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 25) {
            mySignupPass.setError("between 4 and 25 alphanumeric characters");
            valid = false;
        } else {
            mySignupPass.setError(null);
        }

        if (rePassword.isEmpty() || rePassword.length() < 4 || rePassword.length() > 25) {
            confirm_password.setError("between 4 and 25 alphanumeric characters");
            valid = false;
        } else {
            confirm_password.setError(null);
        }

        if (!rePassword.equals(password)) {
            confirm_password.setError("The Passwords and Confirm Passwords must match");
            valid = false;
        } else {
            confirm_password.setError(null);
        }

        return valid;
    }
    public void onSignupSuccess() {
        mySignupBtn.setEnabled(true);
        setResult(RESULT_OK, null);
        finish();
    }

    private void putUserToParse() {
        ParseUser user = new ParseUser();
        user.setUsername(mySignupEmail.getText().toString());
        user.setPassword(mySignupPass.getText().toString());
        user.setEmail(mySignupEmail.getText().toString());
        user.put("legalname",mySignupName.getText().toString());

        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Radhe", "Radhe! Parse signup is success");
                    progressDialog.dismiss();
                    Intent intent = new Intent(context, login.class);
                    intent.putExtra("SignUpFlag", true);
                    startActivity(intent);
                    finish();
                } else {
                    e.printStackTrace();
                    Log.i("Radhe", "Radhe! Parse signup is failure " + e);
                    Toast.makeText(getApplicationContext(),e.toString(),Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    mySignupBtn.setEnabled(true);
                }
            }
        });
    }

    public void clickLink_login(View view)
    {
        startActivity(new Intent(this, login.class));
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

