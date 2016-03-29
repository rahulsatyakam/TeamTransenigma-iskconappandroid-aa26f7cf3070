package com.transenigma.iskconapp;

import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;

import org.joda.time.LocalDate;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class profile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, GenderDialogRadio.GenderAlertPositiveListener{

    ParseUser currentUser = ParseUser.getCurrentUser();
    Date dob = new Date(DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime()));
    ProgressDialog progressDialog;
    int position;
    static String[] code = new String[]{"Male", "Female"};

    @Bind(R.id.mySignupName) EditText mySignupName;
    @Bind(R.id.mySignupEmail) EditText mySignupEmail;
    //@Bind(R.id.mySignupPass) EditText mySignupPass;
    @Bind(R.id.mydob) EditText mydob;
    @Bind(R.id.mySignupSurname) EditText mySignupSurname;
    @Bind(R.id.mySignupSpName) EditText mySignupSpName;
    @Bind(R.id.mySignupPhone) EditText mySignupPhone;
    @Bind(R.id.mySpiritualMaster) EditText mySpiritualMaster;
    @Bind(R.id.gender) EditText gender;
    @Bind(R.id.myEditProfileBtn)Button myEditProfileBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        getSupportActionBar().setTitle("Update Profile");

        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                /** Instantiating the DialogFragment class */
                GenderDialogRadio alert = new GenderDialogRadio();
                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();
                /** Storing the selected item's index in the bundle object */
                b.putInt("position", position);
                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);
                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                alert.show(manager, "alert_dialog_radio");
            }
        };
        gender.setOnClickListener(listener);

        mySignupName.setText(currentUser.getString("legalname"));
        mySignupEmail.setText(currentUser.getEmail());
        mySignupSurname.setText(currentUser.getString("surname"));
        mySignupSpName.setText(currentUser.getString("spname"));
        mySpiritualMaster.setText(currentUser.getString("guru"));
        mySignupPhone.setText(currentUser.getString("phoneno"));
        if(currentUser.getDate("dob")!= null) {
            String[] parts = currentUser.getDate("dob").toString().split(" ");
            String showDate = parts[1] + " " + parts[2] + " " + parts[5];
            mydob.setText(showDate);
        }
        gender.setText(currentUser.getString("gender"));
        if(mySignupSpName.getText().toString().equals("")) mySpiritualMaster.setVisibility(View.GONE);
        /************************************Set up teh Spiritual Master Field***************************/
        mySignupSpName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    if (!TextUtils.isEmpty(mySignupSpName.getText().toString())) {
//                        LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                        et.setLayoutParams(p);
//                        et.setHint("Spiritual Master");
//                        et.setTextColor(Color.rgb(0, 0, 0));
//                        mySignupLin.addView(et);
                        mySpiritualMaster.setVisibility(View.VISIBLE);
                    } else {
//                        if (mySignupLin != null) {
//                            mySignupLin.removeAllViews();
//                          }
                        mySpiritualMaster.setVisibility(View.GONE);

                    }
                }
            }
        });
        /************************************Over setting up of Spiritual Master Field***************************/

    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        month = month +1;
        ((EditText) findViewById(R.id.mydob)).setText(day + "/" + month + "/" + year);
        LocalDate localDate = new LocalDate(year, month, day);
        dob = localDate.toDate();
    }

    public void clickEditProfileBtn(View v){
        if (!validate()) {
            onEdittingFailed();
            return;
        }
        myEditProfileBtn.setEnabled(false);

        progressDialog = ProgressDialog.show( this, "Please Wait!", "We are editting your profile", true);
        putUserToParse();

    }

    private void onEdittingFailed() {
        Toast.makeText(getBaseContext(), "Editting Profile failed", Toast.LENGTH_LONG).show();
        myEditProfileBtn.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
//        if(mySignupPass.getText().toString().equals("")){
//            mySignupPass.setError("Password between 4 and 25 alphanumeric characters");
//            valid = false;
//        }
        return valid;
    }

    private void putUserToParse()
    {
        currentUser.setUsername(mySignupEmail.getText().toString());
        //currentUser.setPassword(mySignupPass.getText().toString());
        currentUser.setEmail(mySignupEmail.getText().toString());
        currentUser.put("legalname",mySignupName.getText().toString() );
        currentUser.put("surname",mySignupSurname.getText().toString() );
        currentUser.put("spname",mySignupSpName.getText().toString() );
        currentUser.put("phoneno",mySignupPhone.getText().toString() );
        currentUser.put("guru", mySpiritualMaster.getText().toString());
        currentUser.put("dob", dob);
        currentUser.put("gender", gender.getText().toString());
        currentUser.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null){
                    progressDialog.dismiss();
                    Toast.makeText(profile.this, "Profile Successfully Updated", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext() ,home.class));
                    finish();
                }
                else {
                    Log.i("Radhe","Unable to edit the profile due to the exception "+e);
                    Toast.makeText(profile.this, "Unable to edit the profile due to the exception: "+e, Toast.LENGTH_LONG).show();
                    progressDialog.dismiss();
                    myEditProfileBtn.setEnabled(true);
                }
            }
        });

    }

    @Override
    public void onGenderPositiveClick(int position) {
        this.position = position;
        gender.setText(code[position]);
    }
}
