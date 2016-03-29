package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class visited_events extends AppCompatActivity {

    ProgressDialog progressDialog;
    RecyclerView mRecyclerView;
    MyVisitedEventsAdapter adapter;
    private List<VisitedEventsInfo> feedsList = Collections.emptyList();
    private List<Data> dataList = Collections.emptyList();
    private List<String> eventIdList = Collections.emptyList();
    private List<Boolean> likeList = Collections.emptyList();
    Boolean like= false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visited_events);

        Toolbar toolbar = (Toolbar) findViewById(R.id.visited_events_toolbar);
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
        getSupportActionBar().setTitle("Favourites");

        mRecyclerView = (RecyclerView) findViewById(R.id.visited_events_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        setContent();
    }

    private void setContent() {
        progressDialog = ProgressDialog.show( this, "Please Wait!", "Fetching Events", true);
        ParseQuery query = new ParseQuery("Event_Management");
        query.whereEqualTo("user", ParseUser.getCurrentUser());
        feedsList = new ArrayList<VisitedEventsInfo>();
        dataList = new ArrayList<Data>();
        eventIdList = new ArrayList<String>();
        likeList = new ArrayList<Boolean>();
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
            if(e==null){
                for (ParseObject post : list) {
                    if (post.getBoolean("liked") || post.getBoolean("attended") || post.getInt("flag") == 1 || post.getInt("flag") == 2
                            || post.getString("feedback") != null) {
                        dataList.add(new Data(post.getString("event"), post.getBoolean("liked")));
                        likeList.add(post.getBoolean("liked"));
                        eventIdList.add(post.getString("event"));
                    }
                }
            }

                ParseQuery query2 = new ParseQuery("events");
                query2.whereContainedIn("objectId", eventIdList);

                query2.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> objects, ParseException e) {

                        if(e==null){

                            for(ParseObject object : objects){
                                LocalDateTime d = new LocalDateTime(object.getDate("startDateTime"));
                                String[] parts = object.getDate("startDateTime").toString().split(" ");
                                String a = parts[2]+" "+parts[1];
                                like = likeList.get( eventIdList.indexOf(object.getObjectId()) );
                                feedsList.add(new VisitedEventsInfo((ParseFile)object.getParseFile("image") , object.getObjectId() ,object.getString("eventName"),object.getString("city"),a , like));//feedsList.add(post.getString(search));
                            }

                            adapter = new MyVisitedEventsAdapter(visited_events.this, feedsList);
                            mRecyclerView.setAdapter(adapter);
                            progressDialog.dismiss();
                            handleClicks();
                        }

                    }
                });



            }
        });

    }

    public void handleClicks(){
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {

                View child = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
                if(child!=null && mGestureDetector.onTouchEvent(motionEvent)){
                    String id = ((TextView) child.findViewById(R.id.myEventObjectId)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), detail.class);
                    intent.putExtra("myEventObjectId", ((TextView) child.findViewById(R.id.myEventObjectId)).getText().toString());
                    intent.putExtra("FlagFromEvents","visited_events");
                    startActivity(intent);
                    return true;
                }
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    public class Data{
        String eventId;
        Boolean like;

        public String getEventId() {
            return eventId;
        }

        public Boolean getLike() {
            return like;
        }

        Data(String eventId, Boolean like){
            this.eventId  = eventId;
            this.like = like;

        }
    }

}
