package com.transenigma.iskconapp;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Picasso;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class centres_detail extends AppCompatActivity {

    String title="", address="", description="", feedback="", contactEmail = "", contactName = "", contactNo = "", centreObjectId="";
    ParseFile imageFile;
    double latitude=0, longitude=0;
    Boolean like = false;
    int attended = 0;
    int flag = 0, position;

    @Bind(R.id.myCentresName)TextView myCentresName;
    @Bind(R.id.myCentresAddress)TextView myCentresAddress;
    @Bind(R.id.myGoing)Button myGoing;
    @Bind(R.id.myMayBe)Button myMayBe;
    @Bind(R.id.myNotGoing)Button myNotGoing;
    @Bind(R.id.myCentresDescription)TextView myCentresDescription;
    @Bind(R.id.myContactName)TextView myContactName;
    @Bind(R.id.myContactEmail)TextView myContactEmail;
    @Bind(R.id.myContactNo)TextView myContactNo;
    @Bind(R.id.myGiveFeedback)Button myGiveFeedback;
    @Bind(R.id.myVisited)Button myVisited;
    @Bind(R.id.myCentresMapImg)ImageView myCentresMapImg;
    @Bind(R.id.myCentresImg)ImageView myCentresImg;
    @Bind(R.id.myLikeBtn)ImageView myLikeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_centres_detail);
        ButterKnife.bind(this);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            title = bundle.getString("title");
        }
        Log.i("Radhe","title is "+title);
        getCentresFromParse(title);

    }

    private void getCentresFromParse(String title) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("centres");
        query.whereEqualTo("title", title);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                address = list.get(0).getString("Address");
                imageFile = list.get(0).getParseFile("image");
                description = list.get(0).getString("Description");
                contactEmail = list.get(0).getString("ContactEmail");
                contactName = list.get(0).getString("ContactName");
                contactNo = list.get(0).getString("ContactNo");
                description = list.get(0).getString("Description");
                latitude = list.get(0).getDouble("latitude");
                longitude = list.get(0).getDouble("longitude");
                centreObjectId = list.get(0).getObjectId();
                getCentreManagement(list.get(0).getObjectId());
                setContent();
            }
        });
    }

    private void getCentreManagement(String objectId) {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Centre_Management");
        query.whereEqualTo("user",currentUser.getObjectId().toString()).whereEqualTo("centre",objectId);
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

                    attended = object.getInt("visited");

                    if(object.getString("feedback")!= null && !object.getString("feedback").isEmpty()){
                        feedback = object.getString("feedback");
                    }
                }
                else{
                    Log.i("Radhe", "This centre is not yet visited by the user");
                }

            }
        });
    }

    private void setContent() {
        myCentresName.setText(title);
        myCentresAddress.setText(address);
        loadImages( imageFile , myCentresImg);
        myCentresDescription.setText(description);
        myContactEmail.setText(contactEmail);
        myContactName.setText(contactName);
        myContactNo.setText(contactNo);
        String src = "http://maps.google.com/maps/api/staticmap?center=" +latitude + "," + longitude + "&zoom=13&size=600x600&sensor=false";
        Picasso.with(this).load(src).into(myCentresMapImg);
    }
    public void clickLikeBtn(View view){
        like = !like;
        if(like)
            myLikeBtn.setImageResource(R.drawable.fill_heart);
        else
            myLikeBtn.setImageResource(R.drawable.empty_heart);
    }

    public void clickAttended(View v){
        attended++;
        Toast.makeText(this, "You have visited "+title+" "+attended+" number of times",Toast.LENGTH_SHORT).show();
    }

    public void clickGiveFeedback(View view){
        final View dialogView = View.inflate(centres_detail.this, R.layout.feedback, null);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ParseUser currentUser = ParseUser.getCurrentUser();
        eventManagement();
    }
    private void eventManagement() {
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Centre_Management");
        query.whereEqualTo("user",currentUser.getObjectId().toString()).whereEqualTo("centre",centreObjectId);
        Log.i("Radhe", "We are out of query in event management and like is ="+ like+" and eventFlag is = "+flag);

        query.getFirstInBackground(new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                ParseUser currentUser = ParseUser.getCurrentUser();
                if (e == null) {//the row exists
                    object.put("flag", flag);
                    object.put("liked",like);
                    object.put("visited",attended);
                    if(!feedback.isEmpty()) object.put("feedback", feedback);
                    Log.i("Radhe", "We are inside of query in centre management and flag is "+ flag);
                    object.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                Log.i("Radhe", "Row Already Exisits , Just edited flag in Centres Management");
                            else
                                Log.i("Radhe", "Unable to save the data to Centre management due to error " + e);
                        }
                    });

                } else {// Create a new row
                    Log.i("Radhe", "The getFirst request failed. Create a new row in Centre_Management");
                    ParseObject testObject = new ParseObject("Centre_Management");
                    testObject.put("user", currentUser.getObjectId().toString());
                    testObject.put("centre", centreObjectId);
                    testObject.put("flag", flag);
                    testObject.put("liked", like);
                    testObject.put("visited",attended);
                    if(!feedback.isEmpty()) testObject.put("feedback", feedback);
                    testObject.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null)
                                Log.i("Radhe", "Saved the new data to Centre Management ");
                            else
                                Log.i("Radhe", "Unable to save the data to Centre management due to error " + e);
                        }
                    });
                }
            }
        });

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

}
