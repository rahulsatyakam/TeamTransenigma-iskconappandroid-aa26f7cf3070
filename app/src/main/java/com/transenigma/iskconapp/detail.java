package com.transenigma.iskconapp;

import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;


public class detail extends AppCompatActivity implements SpamDialogRadio.AlertPositiveListener {

    String myEventObjectId = "" , blockAddress ="", street = "", city = "", state="",  country="", eventName="", myDescription="",
            FlagFromEvents="" , feedback="", contactEmail = "", contactName = "", contactNo = "";
    Date endDateTime, startDateTime;
    ParseFile imageFile;
    double latitude=0, longitude=0;
    FloatingActionButton edit_event_fab;
//    ArrayList<String> likedBy;
    Boolean like = false, attended = false;
    int flag = 0, position;
    static String[] code = new String[]{
            "Spam",
            "Vulgar"
    };

    @Bind(R.id.myEventTitle)TextView myEventTitle;
    @Bind(R.id.myEventAddress)TextView myEventAddress;
    @Bind(R.id.myEventDescription)TextView myEventDescription;
    @Bind(R.id.myEventStartDate)TextView myEventStartDate;
    //@Bind(R.id.myGivenFB)TextView myGivenFB;
    @Bind(R.id.myEventEndDate)TextView myEventEndDate;
    @Bind(R.id.myEventDetailImg)ImageView myEventDetailImg;
    @Bind(R.id.myEventMapImg)ImageView myEventMapImg;
    @Bind(R.id.myLikeBtn)ImageView myLikeBtn;
    @Bind(R.id.myGoing)Button myGoing;
    @Bind(R.id.myMayBe)Button myMayBe;
    @Bind(R.id.myNotGoing)Button myNotGoing;
    @Bind(R.id.myGiveFeedback)Button myGiveFeedback;
    @Bind(R.id.myAttended)Button myAttended;
    @Bind(R.id.mySpam)ImageView mySpam;
    @Bind(R.id.myContactEmail)TextView myContactEmail;
    @Bind(R.id.myContactName)TextView myContactName;
    @Bind(R.id.myContactNo)TextView myContactNo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);
        Log.i("Radhe", "We are in detail");
        Bundle bundle = getIntent().getExtras();

        //myGivenFB.setVisibility(View.GONE);


//        myAttended.setOnTouchListener(new View.OnTouchListener() {
//
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN: {
//                        v.getBackground().setColorFilter(0xe0f47521, PorterDuff.Mode.SRC_ATOP);
//                        v.invalidate();
//                        break;
//                    }
//                    case MotionEvent.ACTION_UP: {
//                        v.getBackground().clearColorFilter();
//                        v.invalidate();
//                        break;
//                    }
//                }
//                return false;
//            }
//        });
        //        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
        if(bundle!=null) {
            myEventObjectId = bundle.getString("myEventObjectId");
            FlagFromEvents = bundle.getString("FlagFromEvents");
        }

        getEventDataFromParse(myEventObjectId);
        getEventManagement();
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
        //myEventDescription.setMovementMethod(new ScrollingMovementMethod());
        edit_event_fab = (FloatingActionButton) findViewById(R.id.edit_event_fab);
        edit_event_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(detail.this, edit_event.class);
                intent.putExtra("myEventObjectId",myEventObjectId);
                startActivity(intent);
                finish();
            }
        });
        edit_event_fab.setVisibility(View.INVISIBLE);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseUser currentUser = ParseUser.getCurrentUser();
        Log.i("Radhe","Hari back button");
        eventManagement();
        if(!FlagFromEvents.equals("events")) startActivity(new Intent(this, events.class));
        finish();
    }

//    private void removeCurrentObjectIdFromParse(final String userId) {
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
//        query.getInBackground(myEventObjectId , new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    likedBy.remove(userId);
//                    object.put("likedby", likedBy);
//                    object.saveInBackground();
//                } else {
//                    Log.i("Radhe", "Could not fetch data from Parse of Id "+ myEventObjectId +" due to the exception "+e);
//                }
//            }
//        });
//    }
//
//    private void addCurrentObjIdToParse(final String userId) {
//
//        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
//        query.getInBackground(myEventObjectId , new GetCallback<ParseObject>() {
//            public void done(ParseObject object, ParseException e) {
//                if (e == null) {
//                    likedBy.add(userId);
//                    object.put("likedby", likedBy);
//                    object.saveInBackground();
//                } else {
//                    Log.i("Radhe", "Could not fetch data from Parse of Id "+ myEventObjectId +" due to the exception "+e);
//                }
//            }
//        });
//    }

    public void clickGiveFeedback(View view){
        final View dialogView = View.inflate(detail.this, R.layout.feedback, null);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        TextView myFeedback = (TextView) dialogView.findViewById(R.id.myFeedback);
        final EditText myAddFB = (EditText) dialogView.findViewById(R.id.myAddFB);
        Button myAddFeedbackBtn = (Button) dialogView.findViewById(R.id.myAddFeedbackBtn);

        if(feedback.isEmpty()) myFeedback.setVisibility(View.GONE);
        else myFeedback.setText(feedback);

        myAddFeedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                feedback = feedback + "\n"+ myAddFB.getText().toString();
                //if(!feedback.isEmpty()) myGivenFB.setText(feedback);
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(dialogView);
        alertDialog.show();
    }

    public void clickLikeBtn(View view){
        like = !like;
        if(like)
            myLikeBtn.setImageResource(R.drawable.fill_heart);
        else
            myLikeBtn.setImageResource(R.drawable.empty_heart);
    }

    private void setContent() {
        //myEventDetailImg.setImageResource(R.drawable.img);
        myEventTitle.setText(eventName);
        myEventAddress.setText(blockAddress+","+street+","+city+","+state+","+country);
        Log.i("Radhe","Event address is "+myEventAddress.getText().toString());
        myEventDescription.setText(myDescription);

        String[] parts = startDateTime.toString().split(" ");
        String showStartDate = parts[0]+" "+parts[1]+" "+parts[2]+",";
        String delegate = "hh:mm aaa";
        showStartDate = showStartDate +(String) android.text.format.DateFormat.format(delegate,startDateTime);
        showStartDate = showStartDate.replace(",", System.getProperty("line.separator"));
        myEventStartDate.setText(showStartDate);

        String[] endParts = endDateTime.toString().split(" ");
        String showEndDate = endParts[0]+" "+endParts[1]+" "+endParts[2]+",";
        String delegate2 = "hh:mm aaa";
        showEndDate = showEndDate +(String) android.text.format.DateFormat.format(delegate2,endDateTime);
        showEndDate = showEndDate.replace(",", System.getProperty("line.separator"));
        myEventEndDate.setText(showEndDate);

//        if(checkIfCurrentUserLiked())
//            myLikeBtn.setImageResource(R.drawable.fill_heart);
//        else myLikeBtn.setImageResource(R.drawable.empty_heart);

        myContactEmail.setText(contactEmail);
        myContactName.setText(contactName);
        myContactNo.setText(contactNo);

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
                    contactEmail =object.getString("ContactEmail");
                    contactName = object.getString("ContactName");
                    contactNo = object.getString("ContactNo");
//                    likedBy = (ArrayList<String>)object.get("likedby");
//                    if(likedBy == null) likedBy = new ArrayList<String>();
//                    Log.i("Radhe", "likedBy is = "+ likedBy);
                    setContent();
                } else {
                    Log.i("Radhe", "Could not fetch data from Parse of Id "+ myEventObjectId1 +" due to the exception "+e);
                }
            }
        });
    }

    public void clickGoingStatus(View v){
        myGoing.setBackgroundColor(getResources().getColor(R.color.white));
        myMayBe.setBackgroundColor(getResources().getColor(R.color.white));
        myNotGoing.setBackgroundColor(getResources().getColor(R.color.white));
        switch(v.getId()) {
            case R.id.myGoing:
                myGoing.setBackgroundColor(getResources().getColor(R.color.primary));
                flag = 1;
                break;
            case R.id.myMayBe:
                myMayBe.setBackgroundColor(getResources().getColor(R.color.primary));
                flag = 2;
                break;
            case R.id.myNotGoing:
                myNotGoing.setBackgroundColor(getResources().getColor(R.color.primary));
                flag = 3;
                break;
        }
    }

    private void eventManagement() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event_Management");
        query.whereEqualTo("user",currentUser).whereEqualTo("event",myEventObjectId);
        Log.i("Radhe", "We are out of query in event management and like is ="+ like+" and eventFlag is = "+flag);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (e == null) {//the row exists
                        object.put("flag", flag);
                        object.put("liked",like);
                        object.put("attended",attended);
                    if(!feedback.isEmpty()) object.put("feedback", feedback);
                        Log.i("Radhe", "We are inside of query in event management and flag is "+ flag);
                        object.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null)
                                    Log.i("Radhe", "Row Already Exisits , Just edited flag in Event Management");
                                else
                                    Log.i("Radhe", "Unable to save the data to Event management due to error " + e);
                            }
                        });

                } else {// Create a new row
                    Log.i("Radhe", "The getFirst request failed. Create a new row in Event_Maqnagement");
                    ParseObject testObject = new ParseObject("Event_Management");
                    testObject.put("user", currentUser);
                    testObject.put("event", myEventObjectId);
                    testObject.put("flag", flag);
                    testObject.put("liked", like);
                    testObject.put("attended",attended);
                    if(!feedback.isEmpty()) testObject.put("feedback", feedback);
                    testObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                Log.i("Radhe", "Saved the new data to Event Management ");
                            else
                                Log.i("Radhe", "Unable to save the data to Event management due to error " + e);
                        }
                    });
                }
            }
        });

    }

    public void getEventManagement() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Event_Management");
        query.whereEqualTo("user",currentUser).whereEqualTo("event",myEventObjectId);
        Log.i("Radhe", "We are in getEventManagement");
        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if(e==null) {
                    switch(object.getInt("flag")) {
                        case 0:
                            flag =0;
                            break;
                        case 1:
                            myGoing.setBackgroundColor(getResources().getColor(R.color.primary));
                            flag = 1;
                            break;
                        case 2:
                            myMayBe.setBackgroundColor(getResources().getColor(R.color.primary));
                            flag = 2;
                            break;
                        case 3:
                            myNotGoing.setBackgroundColor(getResources().getColor(R.color.primary));
                            flag = 3;
                            break;
                    }
                    if(object.getBoolean("liked")) {
                        like = true;
                        myLikeBtn.setImageResource(R.drawable.fill_heart);
                    }
                    else myLikeBtn.setImageResource(R.drawable.empty_heart);

                    if(object.getBoolean("attended")) {
                        attended = object.getBoolean("attended");
                        (myAttended).setPressed(true);
                        if(attended) {
                            myAttended.setBackgroundColor(Color.GREEN);
                            myAttended.setText("Attended");
                        }
                        else if(!attended) {
                            myAttended.setBackgroundColor(Color.RED);
                            myAttended.setText("Not Attended");
                        }
                    }

                    if(object.getString("feedback")!= null && !object.getString("feedback").isEmpty()){
                        feedback = object.getString("feedback");
                        //myGivenFB.setVisibility(View.VISIBLE);
                        //myGivenFB.setText(feedback);
                    }
                }
                else{
                    Log.i("Radhe", "This event is not yet visited by the user");
                }

            }
        });
    }

//    public void clickSpam(View v){
//
//        View.OnClickListener listener = new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                FragmentManager manager = getFragmentManager();
//                /** Instantiating the DialogFragment class */
//                SpamDialogRadio alert = new SpamDialogRadio();
//                /** Creating a bundle object to store the selected item's index */
//                Bundle b  = new Bundle();
//                /** Storing the selected item's index in the bundle object */
//                b.putInt("position", position);
//                /** Setting the bundle object to the dialog fragment object */
//                alert.setArguments(b);
//                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
//                alert.show(manager, "alert_dialog_radio");
//            }
//        };
//        mySpam.setOnClickListener(listener);
//
//        saveReportToParse();
//    }

    public void clickSpam(View v){

                FragmentManager manager = getFragmentManager();
                /** Instantiating the DialogFragment class */
                SpamDialogRadio alert = new SpamDialogRadio();
                /** Creating a bundle object to store the selected item's index */
                Bundle b  = new Bundle();
                /** Storing the selected item's index in the bundle object */
                b.putInt("position", position);
                /** Setting the bundle object to the dialog fragment object */
                alert.setArguments(b);
                /** Creating the dialog fragment object, which will in turn open the alert dialog window */
                alert.show(manager, "alert_dialog_radio");

    }


    public void clickAttended(View v){

        if(attended){
            attended = false;
            myAttended.setBackgroundColor(Color.RED);
            myAttended.setText("Not Attended");
            Log.i("Radhe", "Hari Bol attended -> not Attended and attended = "+attended);
        }
        else{
            attended = true;
            myAttended.setBackgroundColor(Color.GREEN);
            myAttended.setText("Attended");
            Log.i("Radhe", "Hari Bol Not attended -> Attended and attended = "+attended);
        }

//        final View dialogView = View.inflate(this, R.layout.toggle, null);
//        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
//        Switch switch1 = (Switch) dialogView.findViewById(R.id.switch1);
//        if(attended!=null) switch1.setChecked(attended);
//        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                Log.i("Radhe", "Switch state = "+isChecked);
//                attended = isChecked;
//                if(isChecked) {
//                    myAttended.setBackgroundColor(Color.GREEN);
//                    myAttended.setText("Attended");
//                }
//                if(!isChecked) {
//                    myAttended.setBackgroundColor(Color.RED);
//                    myAttended.setText("Not Attended");
//                }
//                alertDialog.dismiss();
//            }
//        });
//        alertDialog.setView(dialogView);
//        alertDialog.show();
//        new SlideDateTimePicker.Builder(getSupportFragmentManager()).setListener(startDateListener).setInitialDate(new Date()).build().show();
    }

    private void saveReportToParse() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseObject testObject = new ParseObject("Report");
        testObject.put("userId", currentUser.getObjectId());
        testObject.put("eventId", myEventObjectId);
        DateFormat df = new SimpleDateFormat("yyyy.MM.dd G 'at' HH:mm:ss z");
        String date = df.format(Calendar.getInstance().getTime());
        testObject.put("dateOfReport", date);
        testObject.put("Issue",code[position]);
        testObject.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.i("Radhe", "Saved the report to Event Management ");
                    Toast.makeText(detail.this, "Thank you for your concern. The issue has been reported", Toast.LENGTH_SHORT).show();
                }
                else {
                    Log.i("Radhe", "Unable to save the report to Event management due to error " + e);
                    Toast.makeText(detail.this, "Unfortunately the issue has not been reported. Please try later", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onPositiveClick(int position) {
        this.position = position;
        saveReportToParse();
    }

}
