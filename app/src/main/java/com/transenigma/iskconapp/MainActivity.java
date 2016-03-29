package com.transenigma.iskconapp;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.utils.FileUtils;
import com.google.android.gms.maps.model.LatLng;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Integer;

public class MainActivity extends AppCompatActivity {
    ParseUser currentUser;
    private CoordinatorLayout coordinatorLayout;
    MyMsgBox m;
    int k=0;
    String stateOfDay="";
    ArrayList<String[]> dataArr = new ArrayList<String[]>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
      // calc();
        View arg0;

        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.FadeOUT;
        setContentView(R.layout.activity_main);
        LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        final View layout = li.inflate(R.layout.newmb__messagebar,
                (ViewGroup) findViewById(R.id.mbContainer));
        //final TextView text = li.inflate(R.layout.newmb__messagebar,
               // (TextView) findViewById(R.id.mbMessage));
        currentUser = ParseUser.getCurrentUser();

       final int dayState;
        Calendar c = Calendar.getInstance();
       final int timeOfDay = c.get(Calendar.HOUR_OF_DAY);

        new Timer().schedule(new TimerTask() {
            public void run() {
                MainActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        if (currentUser != null && timeOfDay>=17) {

                            startActivity(new Intent(MainActivity.this, home.class));
                            finish();
                            String s = "Good Evening "+ currentUser.getString("legalname");
                            TextView text = (TextView)layout.findViewById(R.id.mbMessage);
                            text.setText(s);
                            ImageView image = (ImageView)layout.findViewById(R.id.custom_toast_image);
                            image.setImageResource(R.drawable.ic_evening);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(layout);//setting the view of custom toast layout



                            //toast.makeText(getApplicationContext(), "Good Evening "+ currentUser.getString("legalname"), Toast.LENGTH_LONG);
                            toast.show();
                        } else if (currentUser != null &&(timeOfDay>=12 && timeOfDay<17) ) {
                            startActivity(new Intent(MainActivity.this, home.class));
                            finish();
                            // time in ms
                            String s = "Good Afternoon "+ currentUser.getString("legalname");
                            TextView text = (TextView)layout.findViewById(R.id.mbMessage);
                            text.setText(s);
                            ImageView image = (ImageView)layout.findViewById(R.id.custom_toast_image);
                            image.setImageResource(R.drawable.ic_afternoon);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(layout);//setting the view of custom toast layout



                            //toast.makeText(getApplicationContext(), "Good Evening "+ currentUser.getString("legalname"), Toast.LENGTH_LONG);
                            toast.show();

                           // Toast.makeText(getApplicationContext(), "Good Afternoon "+ currentUser.getString("legalname"), Toast.LENGTH_LONG).show();
                        }
                        else if (currentUser != null) {
                            startActivity(new Intent(MainActivity.this, home.class));
                            finish();
                            String s = "Good Morning "+ currentUser.getString("legalname");
                            TextView text = (TextView)layout.findViewById(R.id.mbMessage);
                            text.setText(s);
                            ImageView image = (ImageView)layout.findViewById(R.id.custom_toast_image);
                            image.setImageResource(R.drawable.ic_sunrise);
                            Toast toast = new Toast(getApplicationContext());
                            toast.setDuration(Toast.LENGTH_LONG);
                            toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                            toast.setView(layout);//setting the view of custom toast layout



                            //toast.makeText(getApplicationContext(), "Good Evening "+ currentUser.getString("legalname"), Toast.LENGTH_LONG);
                            toast.show();

                            //snackbar.show();
                           // Toast.makeText(getApplicationContext(), "Good Morning "+ currentUser.getString("legalname"), Toast.LENGTH_LONG).show();
                        }
                        else {
                            startActivity(new Intent(MainActivity.this, start.class));
                            finish();
                            Log.i("Radhe", "No current user");
                        }
                    }
                });
            }
        }, 1000);

        //putCentresToParse();

    }


    private void putCentresToParse() {

        try {
            readData();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("Radhe", "IOException is " + e);
        }

        for(int j =0; j< 47 ; j++){
            new Timer().schedule(new TimerTask() {
                public void run() {
                    MainActivity.this.runOnUiThread(new Runnable() {
                        public void run() {

                            for (int i = 10+ 15*k ; i < 10+15*(k+1); i++) {
                                LatLng latLng = showAddressOnMap(dataArr.get(i)[2]);
                                ParseObject post = new ParseObject("centres");
                                int label = Integer.parseInt(dataArr.get(i)[0].replaceAll("\\s+", ""));
                                post.put("label", label);
                                post.put("title", dataArr.get(i)[1]);
                                post.put("Address", dataArr.get(i)[2]);
                                post.put("ContactEmail", dataArr.get(i)[3]);
                                post.put("ContactNo", dataArr.get(i)[4]);
                                post.put("Description", "Description about the centre is unavailable");
                                post.put("latitude", latLng.latitude);
                                post.put("longitude", latLng.longitude);
                                if (latLng.latitude == 0 & latLng.longitude == 0) {
                                    Log.i("Radhe", "Centre Address " + dataArr.get(i)[2] + " not found");
                                } else post.saveInBackground();

                            }k++;
                            Log.i("Radhe", k + "th loop done");
                        }
                    });
                }
            }, 3000);
        }



//        for (int i = 10; i < 15; i++) {
//            LatLng latLng = showAddressOnMap(dataArr.get(i)[2]);
//            ParseObject post = new ParseObject("centres");
//            int label = Integer.parseInt(dataArr.get(i)[0].replaceAll("\\s+", ""));
//            post.put("label", label);
//            post.put("title", dataArr.get(i)[1]);
//            post.put("Address", dataArr.get(i)[2]);
//            post.put("ContactEmail", dataArr.get(i)[3]);
//            post.put("ContactNo", dataArr.get(i)[4]);
//            post.put("Description", "Description about the centre is unavailable");
//            post.put("latitude", latLng.latitude);
//            post.put("longitude", latLng.longitude);
//            if (latLng.latitude == 0 & latLng.longitude == 0) {
//                Log.i("Radhe", "Centre Address " + dataArr.get(i)[2] + " not found");
//            } else post.saveInBackground();
//
//        }
    }

    public void readData() throws IOException {

        InputStream is = this.getResources().openRawResource(R.raw.finalpipes2);
        //BufferedReader dataBR = new BufferedReader(new InputStreamReader(is));

          String line ="";
        try {
            line = convertStreamToString(is);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] value = line.toString().split("\\|", 3575);

        for(int i=0; i<715; i++){
            String[] club = new String[5];
            for(int j=0; j<5; j++){
                club[j] = value[5*i+j];
            }
            dataArr.add(club);
        }

//        Log.i("Radhe","line is -: "+ line);
//        Log.i("Radhe","data size is -: "+ dataArr.size());
//
//        for (int i = 0; i < dataArr.size(); i++) {
//            for (int x = 0; x < dataArr.get(i).length; x++) {
//                Log.i("Radhe", " i = "+i+" x = "+ x);
//                Log.i("Radhe","dataArr.get(i)[x] = "+dataArr.get(i)[x]);
//            }
//        }
    }

    private LatLng showAddressOnMap(String searchedLocation) {
        Toast.makeText(this,"Drag Marker to Event Place or Touch & Hold Marker at event Place",Toast.LENGTH_SHORT).show();

        LatLng latLng = new LatLng(0,0);
        List<Address> addressList = null;
        if(searchedLocation!=null || !searchedLocation.equals(""))
        {
            Log.i("Radhe", "Radhe! Address is " + searchedLocation);
            Geocoder geocoder = new Geocoder(this);
            try {
                addressList = geocoder.getFromLocationName(searchedLocation, 1);
                Log.i("Radhe", "Radhe! No IOException. AddressList is "+ addressList);
            } catch (IOException e) {
                Log.i("Radhe", "Radhe! IOException is "+e);
                e.printStackTrace();
                return latLng;
            }

            if(addressList.size()>0 & addressList!=null) {
                Log.i("Radhe", "Radhe! AddressList.size is "+addressList.size());
                Address address = addressList.get(0);
                latLng = new LatLng(address.getLatitude(), address.getLongitude());
                Log.i("Radhe", "Radhe! addressList "+addressList.size()+"  /******/  "+addressList.get(0));
            }
            else
            {
                Log.i("Radhe", "Radhe Place not found");
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),"Please Enter a Place",Toast.LENGTH_SHORT).show();
        }
        return latLng;
    }

    public static String convertStreamToString(InputStream is) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        String line = null;
        while ((line = reader.readLine()) != null) {
            sb.append(line).append("\n");
        }
        reader.close();
        return sb.toString();
    }

}
