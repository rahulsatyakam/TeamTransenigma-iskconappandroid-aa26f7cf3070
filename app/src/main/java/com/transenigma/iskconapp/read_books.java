package com.transenigma.iskconapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class read_books extends AppCompatActivity {

    RecyclerView mRecyclerView;
    Context mContext;
    private BooksRecyclerAdapter adapter;
    private ArrayList<BooksInfo> feedsList = new ArrayList<BooksInfo>();
    Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_books);

        Toolbar toolbar = (Toolbar) findViewById(R.id.book_toolbar);
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
        getSupportActionBar().setTitle("Read Books");

        mContext = this;
        mRecyclerView = (RecyclerView) findViewById(R.id.book_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        populateFeedList();

        adapter = new BooksRecyclerAdapter(read_books.this, feedsList);
        mRecyclerView.setAdapter(adapter);
        handleClicks();
    }

    private void populateFeedList() {
        ArrayList<String> booksList = new ArrayList<String>(Arrays.asList( getResources().getStringArray(R.array.book_names) ));
        ArrayList<String> authorsList = new ArrayList<String>(Arrays.asList( getResources().getStringArray(R.array.book_authors) ));

        for(int i=0; i< booksList.size(); i++)
        {
            feedsList.add(new BooksInfo( booksList.get(i),authorsList.get(i) ));
        }

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
                    String myBookTitle = ((TextView) child.findViewById(R.id.myBookTitle)).getText().toString();
                    String link = myBookTitle.replaceAll("\\s+","");
                    Log.i("Radhe", "link is "+link);
                    if(isConnectedToInternet()) {
                        PDFTools.showPDFUrl(context, context.getString(context.getResources().getIdentifier(link, "string", context.getPackageName())));
                    }else{
                        Toast.makeText(mContext,"No internet connection",Toast.LENGTH_LONG).show();
                        startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                    }
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
