package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.transenigma.iskconapp.eventmanagement.EventManagement;
import java.lang.String;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import butterknife.Bind;
import butterknife.ButterKnife;
import android.support.v7.app.ActionBarDrawerToggle;
import butterknife.ButterKnife;

public class RssFeeds extends AppCompatActivity{

    ProgressDialog progressDialog;
    RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private List<EventsInfo> feedsList = Collections.emptyList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_events);
        ButterKnife.bind(this);
       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

       // getSupportActionBar().setDisplayHomeAsUpEnabled(true);

      //  getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationIcon(R.drawable.back_arrow);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("Radhe", "Back About");
                onBackPressed();
            }
        });
      //  getSupportActionBar().setTitle("About ISKCON");*/
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.news_toolbar);
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
        getSupportActionBar().setTitle("Read Books");*/
      // Toolbar myToolbar = (Toolbar) this.findViewById(R.id.toolbar);
//        setSupportActionBar(myToolbar);
       // LayoutInflater li = getLayoutInflater();
      //  LinearLayout item = (LinearLayout) findViewById(R.id.toolbarLayout);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
       // View child = getLayoutInflater().inflate(R.layout.child, null);
    mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        getMyEvents();

        //getSupportActionBar(toolbar);*/

    }

    private void getMyEvents() {
        progressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching News", true);
        try {
            ParseQuery query = new ParseQuery("rssfeed");
            query.orderByDescending("createdAt");
            List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
            query.findInBackground(new FindCallback<ParseObject>() {
                @Override
                public void done(List<ParseObject> list, ParseException e) {
                    feedsList = new ArrayList<EventsInfo>();
                    for (ParseObject post : list) {
                        String newTitle;
                        String[] parts = post.getString("pubdate").split(" ");
                        String a = parts[2] + " " + parts[1];
                        String title= post.getString("title");
                        int len= title.length();
                        if(len>54) {
                             newTitle = title.substring(0, 52);
                            newTitle=newTitle+"...";
                           // Log.v("message",newTitle);
                        }
                         else {
                            newTitle=title;
                        }//   String nnewTitle= newTitle+"...";

                        String anewTitle = newTitle;

                        feedsList.add(new EventsInfo((ParseFile) post.getParseFile("image"), post.getString("link"),newTitle, post.getString("pubdate"), a));//feedsList.add(post.getString(search));
                    }
                    adapter = new MyRecyclerAdapter(RssFeeds.this, feedsList, "News");
                    mRecyclerView.setAdapter(adapter);
                    progressDialog.dismiss();
                    handleClicks();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Radhe", "Exception" + e);
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
                    String url = ((TextView) child.findViewById(R.id.myEventObjectId)).getText().toString();
                   // URL urlnew = new URL(url);
                    Log.i("Radhe", "The url is -: "+ url);

                   // Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                  //  startActivity(browserIntent);
                    Intent intent = new Intent(getApplicationContext(), webView.class);
                    intent.putExtra("URL",url);
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
