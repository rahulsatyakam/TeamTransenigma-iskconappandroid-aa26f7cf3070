package com.transenigma.iskconapp.eventmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.transenigma.iskconapp.EventsInfo;
import com.transenigma.iskconapp.MyRecyclerAdapter;
import com.transenigma.iskconapp.R;

import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MyEvents extends AppCompatActivity  {

    ProgressDialog  finalProgressDialog;
    ActionBarDrawerToggle toggle;
    RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private List<EventsInfo> feedsList = Collections.emptyList();
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        getMyEvents();
    }

    private void getMyEvents() {
        finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Events", true);
        try {
            ParseQuery myQuery1 = new ParseQuery("events");
            ParseUser currentUser = ParseUser.getCurrentUser();
            myQuery1.whereEqualTo("user", currentUser).whereLessThanOrEqualTo("target", 3).whereEqualTo("verified", true);
            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            queries.add(myQuery1);
            ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
            mainQuery.orderByAscending("startDateTime");
            mainQuery.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    feedsList = new ArrayList<EventsInfo>();
                    for (ParseObject post : list) {
                        LocalDateTime d = new LocalDateTime(post.getDate("startDateTime"));
                        String[] parts = post.getDate("startDateTime").toString().split(" ");
                        String a = parts[2] + " " + parts[1];
                        feedsList.add(new EventsInfo((ParseFile) post.getParseFile("image"), post.getObjectId(), post.getString("eventName"), post.getString("city"), a));//feedsList.add(post.getString(search));
                    }
                    adapter = new MyRecyclerAdapter(MyEvents.this, feedsList);
                    mRecyclerView.setAdapter(adapter);
                    finalProgressDialog.dismiss();
                    handleClicks();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Radhe", "Exception" + e);
        }
    }
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        toggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    public void handleClicks() {
        final GestureDetector mGestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView recyclerView, MotionEvent motionEvent) {
                View child = recyclerView.findChildViewUnder(motionEvent.getX(), motionEvent.getY());
                if (child != null && mGestureDetector.onTouchEvent(motionEvent)) {
                    String id = ((TextView) child.findViewById(R.id.myEventObjectId)).getText().toString();
                    Intent intent = new Intent(getApplicationContext(), EventManagement.class);
                    intent.putExtra("myEventObjectId", ((TextView) child.findViewById(R.id.myEventObjectId)).getText().toString());
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
}