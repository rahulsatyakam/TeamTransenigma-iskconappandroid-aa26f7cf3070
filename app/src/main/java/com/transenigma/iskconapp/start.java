package com.transenigma.iskconapp;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.DateTimeKeyListener;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Toast;

import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


public class start extends AppCompatActivity {

    double latitude, longitude;
    ParseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentUser = ParseUser.getCurrentUser();
        setContentView(R.layout.activity_start);

        if (currentUser != null) {
            new Timer().schedule(new TimerTask(){
            public void run() {
                start.this.runOnUiThread(new Runnable() {
                    public void run() {
                        startActivity(new Intent(start.this, home.class));
                        String Datetime;
                        Calendar c = Calendar.getInstance();
                        SimpleDateFormat dateformat = new SimpleDateFormat(" hh");
                        Datetime = dateformat.format(c.getTime());
                        //System.out.println(Datetime);
                        //Toast.makeText(getApplicationContext(), "Welcome" + currentUser.getString("legalname"),Toast.LENGTH_LONG).show();
                        finish();
                        final View coordinatorLayoutView = findViewById(R.id.snackbarPosition);

                      //  Snackbar
                              //  .make(coordinatorLayoutView, "Welcome " + currentUser.getString("legalname"), Snackbar.LENGTH_LONG)

                              //  .show();
                    }
                });
                }
        }, 1000);
        }
        else{
            Log.i("Radhe", "No current user");
        }
    }

    public void clickHomeLoginBtn(View view) {
        startActivity(new Intent(this, login.class));
        finish();
    }

    public void clickHomeSignupBtn(View v) {
        startActivity(new Intent(this, signup.class));
        finish();
    }

    public void clickTerms(View view)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Terms and Conditions").setMessage(this.getString(R.string.TermsMessage)).setPositiveButton(R.string.OK, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

            }
        });
        builder.show();
    }

    private class Mylocationlistener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                // ---Get current location latitude, longitude, altitude & speed ---
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                float speed = location.getSpeed();
                double altitude = location.getAltitude();
                Toast.makeText(getApplicationContext(),"Latitude = "+ location.getLatitude() + "" +"Longitude = "+ location.getLongitude()+"Altitude = "+altitude+"Speed = "+speed,
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    }



}
