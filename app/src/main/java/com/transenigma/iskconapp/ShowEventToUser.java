package com.transenigma.iskconapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;

import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.squareup.picasso.Picasso;


import org.joda.time.LocalDate;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import butterknife.Bind;
import butterknife.ButterKnife;


public class ShowEventToUser extends AppCompatActivity {

    String myEventObjectId = "" , blockAddress ="", street = "", city = "", state="",  country="", eventName="", myDescription="";
    Date endDateTime, startDateTime;
    ParseFile imageFile;
    double latitude=0, longitude=0;

    @Bind(R.id.myEventTitle)TextView myEventTitle;
    @Bind(R.id.myEventAddress)TextView myEventAddress;
    @Bind(R.id.myEventDescription)TextView myEventDescription;
    @Bind(R.id.myEventStartDate)TextView myEventStartDate;
    @Bind(R.id.myEventEndDate)TextView myEventEndDate;
    @Bind(R.id.myEventDetailImg)ImageView myEventDetailImg;
    @Bind(R.id.myEventMapImg)ImageView myEventMapImg;
    @Bind(R.id.myContactEmail)TextView myContactEmail;
    @Bind(R.id.myContactName)TextView myContactName;
    @Bind(R.id.myContactNo)TextView myContactNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_event_to_user);

        ButterKnife.bind(this);
        Log.i("Radhe", "We are in detail");

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null) {
            myEventObjectId = bundle.getString("myEventObjectId");
        }
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        getEventDataFromParse(myEventObjectId);
        myEventMapImg.setLongClickable(true);
        myEventMapImg.setOnLongClickListener(new View.OnLongClickListener() {

            @Override
            public boolean onLongClick(View v) {
//                Toast.makeText(getApplicationContext(),"Hari Bol", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), event_map.class);
                intent.putExtra("latitude",latitude);
                intent.putExtra("longitude",longitude);
                intent.putExtra("eventName",eventName);
                startActivity(intent);
                return true;
            }
        });
    }

    protected void onDestroy() {
        super.onDestroy();
        Log.i("Radhe","Hari back button");
        startActivity(new Intent(this, events.class));
    }

    private void setContent() {
        //myEventDetailImg.setImageResource(R.drawable.img);
        myEventTitle.setText(eventName);
        myEventAddress.setText(blockAddress+","+street+","+city+","+state+","+country);
        Log.i("Radhe","Event address is "+myEventAddress.getText().toString());
        myEventDescription.setText(myDescription);

//        SimpleDateFormat startlocalDateFormat = new SimpleDateFormat("HH:mm:ss");
//        String startTime = startlocalDateFormat.format(startDateTime);
//        myEventStartDate.setText((new LocalDate(startDateTime)).toString()+"\n"+startTime);

        String[] parts = startDateTime.toString().split(" ");
        String showStartDate = parts[0]+" "+parts[1]+" "+parts[2]+",";
        String delegate = "hh:mm aaa";
        showStartDate = showStartDate +(String) DateFormat.format(delegate,startDateTime);
        showStartDate = showStartDate.replace(",", System.getProperty("line.separator"));
        myEventStartDate.setText(showStartDate);

//        SimpleDateFormat endlocalDateFormat = new SimpleDateFormat("HH:mm:ss");
//        String endTime = endlocalDateFormat.format(endDateTime);
//        myEventEndDate.setText((new LocalDate(endDateTime)).toString()+"\n"+endTime);


        String[] endParts = endDateTime.toString().split(" ");
        String showEndDate = endParts[0]+" "+endParts[1]+" "+endParts[2]+",";
        String delegate2 = "hh:mm aaa";
        showEndDate = showEndDate +(String) DateFormat.format(delegate2,endDateTime);
        showEndDate = showEndDate.replace(",", System.getProperty("line.separator"));
        myEventEndDate.setText(showEndDate);

        loadImages( imageFile , myEventDetailImg);
        String src = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=13&size=600x600&sensor=false";
        Picasso.with(this).load(src).into(myEventMapImg);
    }
    private void loadImages(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                        //progress.dismiss();
                    } else {
                    }
                }
            });
        } else {
            img.setImageResource(R.drawable.mayapur);
        }
    }
    private void getEventDataFromParse(String myEventObjectId) {
        final String myEventObjectId1 =  myEventObjectId;
        Log.i("Radhe", "inside  getEventDataFromParse "+ myEventObjectId);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.getInBackground(myEventObjectId , new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    blockAddress = object.getString("blockAddress");
                    street = object.getString("street");
                    city = object.getString("city");
                    state = object.getString("state");
                    country = object.getString("country");
                    myDescription = object.getString("description");
                    eventName = object.getString("eventName");
                    startDateTime = object.getDate("startDateTime");
                    endDateTime = object.getDate("endDateTime");
                    latitude = object.getDouble("latitude");
                    longitude = object.getDouble("longitude");
                    imageFile = object.getParseFile("image");
                    myContactEmail.setText(object.getString("ContactEmail"));
                    myContactName.setText(object.getString("ContactName"));
                    myContactNo.setText(object.getString("ContactNo"));
                    setContent();
                } else {
                    Log.i("Radhe", "Could not fetch data from Parse of Id "+ myEventObjectId1 +" due to the exception "+e);
                }
            }
        });
    }
}
