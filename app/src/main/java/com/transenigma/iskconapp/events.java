package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ListPopupWindow;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.maps.model.LatLng;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.transenigma.iskconapp.eventmanagement.MyEvents;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class events extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, PopupMenu.OnMenuItemClickListener {

    GPSTracker gps;
    LatLng latLng;
    Geocoder geocoder;
    List<Address> addresses;
    double latitude=0, longitude=0;

    ProgressDialog progressDialog, finalProgressDialog;
    PopupMenu popupMenu;
    int count, target;
    ActionBarDrawerToggle toggle;
    String navigationDrawerItem = "";
    RecyclerView mRecyclerView;
    private MyRecyclerAdapter adapter;
    private List<EventsInfo> feedsList = Collections.emptyList();
    List<String> postTexts;
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        home.homeProgressDialog.dismiss();

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
//        mRecyclerView.addOnItemTouchListener(new RecyclerTouchListener(this, mRecyclerView, new ClickListener() {
//            @Override
//            public void onClick(View view, int position) {
//                if(view!=null) {
//                    String id = ((TextView) view.findViewById(R.id.myEventObjectId)).getText().toString();
//                    Intent intent = new Intent(getApplicationContext(), detail.class);
//                    intent.putExtra("myEventObjectId", ((TextView) view.findViewById(R.id.myEventObjectId)).getText().toString());
//                    //Log.i("Radhe", "Radhe child Clicked " + ((TextView) view.findViewById(R.id.myEventTitle)).getText() +" And object Id is "+ id);
//                    startActivity(intent);
//                }
//            }
//
////            @Override
////            public void onLongClick(View view, int position) {
////                Log.i("TAG", "Radhe child Long Clicked ");
////                Toast.makeText(getActivity(),"Long CLick",Toast.LENGTH_SHORT).show();
////            }
//        }));
//        progressBar = (ProgressBar) findViewById(R.id.progress_bar);
//        progressBar.setVisibility(View.INVISIBLE);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), event_detail.class));
            }
        });

        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        /**************************Set up the initial events as per the Location of the User**************/
        gps = new GPSTracker(events.this);
        if(gps.canGetLocation()){
            getLatLngAndShowEvents();
        }
        else{
            gps.showSettingsAlert();
        }
    }

    private void getLatLngAndShowEvents() {
        latitude = gps.getLatitude();
        longitude = gps.getLongitude();
        latLng = new LatLng(latitude, longitude);
        geocoder = new Geocoder(this, Locale.getDefault());

        if(isConnectedToInternet())
        {
            finalProgressDialog = ProgressDialog.show( this, "Please Wait!", "Fetching Events", true);
            try {
//                addresses = geocoder.getFromLocation(latitude, longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                addresses = geocoder.getFromLocation(22.572646, 88.363895, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();
                String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
                Log.i("Radhe", "city = "+city+ " state = "+state+" country = "+country +" address "+ addresses.get(0));
                showRelevantEvents(city, state, country);
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Got the IO exception in getting the location "+e);
                showRelevantEvents("Kolkata", "West Bengal", "India");
            }
        }
        else
        {
            Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
        }
    }

    private void showRelevantEvents(String city, String state, String country) {

        ParseQuery myQuery1 = new ParseQuery("events");
        myQuery1.whereEqualTo("city",city).whereLessThanOrEqualTo("target",3).whereEqualTo("verified",true);
        myQuery1.whereGreaterThanOrEqualTo("endDateTime", new Date());
        ParseQuery myQuery2 = new ParseQuery("events");
        myQuery2.whereEqualTo("state",state).whereLessThanOrEqualTo("target",2).whereNotEqualTo("city",city).whereEqualTo("verified",true);
        myQuery2.whereGreaterThanOrEqualTo("endDateTime", new Date());
        ParseQuery myQuery3 = new ParseQuery("events");
        myQuery3.whereEqualTo("country",country).whereLessThanOrEqualTo("target",1).whereNotEqualTo("state",state).whereNotEqualTo("city",city).whereEqualTo("verified",true);;
        myQuery3.whereGreaterThanOrEqualTo("endDateTime", new Date());
        ParseQuery myQuery4 = new ParseQuery("events");
        myQuery4.whereEqualTo("target",0).whereNotEqualTo("country",country).whereNotEqualTo("state",state).whereNotEqualTo("city",city).whereEqualTo("verified",true);;
        myQuery4.whereGreaterThanOrEqualTo("endDateTime", new Date());
        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();
        queries.add(myQuery1);
        queries.add(myQuery2);
        queries.add(myQuery3);
        queries.add(myQuery4);

        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByAscending("startDateTime");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                feedsList = new ArrayList<EventsInfo>();
                for(ParseObject post : list){
                    LocalDateTime d = new LocalDateTime(post.getDate("startDateTime"));
                    String[] parts = post.getDate("startDateTime").toString().split(" ");
                    String a = parts[2]+" "+parts[1];
                    //feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),d.toString() ));//feedsList.add(post.getString(search));
                    feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                }
                adapter = new MyRecyclerAdapter(events.this, feedsList);
                mRecyclerView.setAdapter(adapter);
                finalProgressDialog.dismiss();
                handleClicks();
            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        RelativeLayout myview = (RelativeLayout) findViewById(R.id.myHomePage);

        switch(item.getItemId()){
//            case R.id.myHomeLogout:
//                if(item.isChecked())
//                    item.setChecked(false);
//                else
//                    item.setChecked(true);
//                ParseUser.logOut();
//                LoginManager.getInstance().logOut();
//                clearCookies();
//                startActivity(new Intent(this, start.class));
//                finish();
//                return true;
//
//            case R.id.myHomeProfile:
//                if(item.isChecked())
//                    item.setChecked(false);
//                else
//                    item.setChecked(true);
//                validatePassword();
//                return true;

            case R.id.myVisitedEvents:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    startActivity(new Intent(this, visited_events.class));
                return true;

            case R.id.myAddEvents:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    startActivity(new Intent(getApplicationContext(), event_detail.class));
                return true;
//            case R.id.myVisitedCentres:
//                if(item.isChecked())
//                    item.setChecked(false);
//                else
//                    startActivity(new Intent(this, visited_centres.class));
//                return true;
            case R.id.myEventManagement:
                if(item.isChecked())
                    item.setChecked(false);
                else
                    startActivity(new Intent(this, MyEvents.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void validatePassword() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Password needed");
        alert.setMessage("Before you edit your profile, give your password. In case you logged in using FB or Google, enter your email-id");
// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                progressDialog = ProgressDialog.show( events.this, "Please Wait!", "Authenticating", true);
                String password = input.getText().toString();
                ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            startActivity(new Intent(events.this, profile.class));
                            progressDialog.dismiss();
                        } else {
                            Toast.makeText(events.this,"The password given is not correct, Please try again", Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    }
                });
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    public void clearCookies(){
        CookieSyncManager.createInstance(events.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        popupMenu = new PopupMenu(this,fab, Gravity.CENTER);
        popupMenu.setOnMenuItemClickListener(this);

        if (id == R.id.myClickLocation) {
            final View dialogView = View.inflate(this, R.layout.location_menu, null);
            final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
            LinearLayout myEventAll = (LinearLayout) dialogView.findViewById(R.id.myEventAll);
            LinearLayout myEventCity = (LinearLayout) dialogView.findViewById(R.id.myEventCity);
            LinearLayout myEventState = (LinearLayout) dialogView.findViewById(R.id.myEventState);
            LinearLayout myEventCountry = (LinearLayout) dialogView.findViewById(R.id.myEventCountry);
            LinearLayout myEventWorld = (LinearLayout) dialogView.findViewById(R.id.myEventWorld);

            myEventAll.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationDrawerItem= "nearme";
                    getLatLngAndShowEvents();
                    alertDialog.dismiss();
                }
            });
            myEventCity.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationDrawerItem= "city";
                    target = 3;
                    showProcessDialogBox();
                    fetchFromParse("city");
                    alertDialog.dismiss();
                }
            });
            myEventState.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationDrawerItem= "state";
                    target = 2;
                    showProcessDialogBox();
                    fetchFromParse(navigationDrawerItem);
                    alertDialog.dismiss();
                }
            });
            myEventCountry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationDrawerItem= "country";
                    target = 1;
                    showProcessDialogBox();
                    fetchFromParse(navigationDrawerItem);
                    alertDialog.dismiss();
                }
            });
            myEventWorld.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    navigationDrawerItem= "world";
                    target = 0;
                    fetchAllEventsFromParse();
                    alertDialog.dismiss();
                }
            });

            alertDialog.setView(dialogView);
            alertDialog.show();
        }
        else if (id == R.id.myClickTag) {
            final CharSequence[] items = {" Kirtan "," Lecture "," Harinaam-Sankirtan "," Prashadam "," Presentation "};
            // arraylist to keep the selected items
            final ArrayList seletedItems=new ArrayList();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select The Tags")
                    .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                seletedItems.add(indexSelected);
                            } else if (seletedItems.contains(indexSelected)) {
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String tags="";
                            int i=0;
                            for(; i<seletedItems.size()-1; i++)
                                tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                            tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                            fetchTaggedEventsFromParse(tags);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();
            dialog.show();
        }else if (id == R.id.myClickAge) {
            final CharSequence[] items = {" Below 20 years ", " Between 20 to 35 years ", " Between 35 to 50 years ", " Above 50 "};
            // arraylist to keep the selected items
            final ArrayList seletedItems=new ArrayList();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select the age group")
                    .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                seletedItems.add(indexSelected);
                            } else if (seletedItems.contains(indexSelected)) {
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String tags="";
                            int i=0;
                            for(; i<seletedItems.size()-1; i++)
                                tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                            tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                            fetchAgedEventsFromParse(tags);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();
            dialog.show();
        }else if (id == R.id.myClickAshram) {
            final CharSequence[] items = {" Brahmachari ", " Grihastha ", " Vanaprashtha ", " Sannyasa "};
            // arraylist to keep the selected items
            final ArrayList seletedItems=new ArrayList();

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Select the Ashram(s)")
                    .setMultiChoiceItems(items, null, new DialogInterface.OnMultiChoiceClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int indexSelected, boolean isChecked) {
                            if (isChecked) {
                                seletedItems.add(indexSelected);
                            } else if (seletedItems.contains(indexSelected)) {
                                seletedItems.remove(Integer.valueOf(indexSelected));
                            }
                        }
                    }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            String tags="";
                            int i=0;
                            for(; i<seletedItems.size()-1; i++)
                                tags = tags+ items[Integer.valueOf(seletedItems.get(i).toString())] + ",";
                            tags = tags+items[Integer.valueOf(seletedItems.get(i).toString())];
                            fetchAshramEventsFromParse(tags);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    }).create();
            dialog.show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fetchAshramEventsFromParse(String tags) {
        String[] parts = (tags.replaceAll("\\s","")).split(",");

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        for(int i=0; i<parts.length; i++){
            ParseQuery myQuery = new ParseQuery("events").whereEqualTo("verified",true);
            myQuery.whereGreaterThanOrEqualTo("endDateTime", new Date());
            ArrayList<String> tagsList = new ArrayList<String>();
            tagsList.add(parts[i]);
            myQuery.whereContainsAll("TargetAshram", tagsList);
            queries.add(myQuery);
        }
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByAscending("startDateTime");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null){
                    feedsList = new ArrayList<EventsInfo>();
                    for(ParseObject post : list){
                        LocalDate d = new LocalDate(post.getDate("startDateTime"));
                        String[] parts = post.getDate("startDateTime").toString().split(" ");
                        String a = parts[2]+" "+parts[1];
                        feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                    }
                    adapter = new MyRecyclerAdapter(events.this, feedsList);
                    mRecyclerView.setAdapter(adapter);
                    Log.i("Radhe", "Arraylist of tags is "+ list.toString());
                    handleClicks();
                }
            }
        });
    }

    private void fetchAgedEventsFromParse(String tags) {

        String[] parts = (tags.replaceAll("\\s","")).split(",");

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        for(int i=0; i<parts.length; i++){
            ParseQuery myQuery = new ParseQuery("events").whereEqualTo("verified",true);
            myQuery.whereGreaterThanOrEqualTo("endDateTime", new Date());
            ArrayList<String> tagsList = new ArrayList<String>();
            tagsList.add(parts[i]);
            myQuery.whereContainsAll("TargetAge", tagsList);
            queries.add(myQuery);
        }
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByAscending("startDateTime");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null){
                    feedsList = new ArrayList<EventsInfo>();
                    for(ParseObject post : list){
                        LocalDate d = new LocalDate(post.getDate("startDateTime"));
                        String[] parts = post.getDate("startDateTime").toString().split(" ");
                        String a = parts[2]+" "+parts[1];
                        feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                    }
                    adapter = new MyRecyclerAdapter(events.this, feedsList);
                    mRecyclerView.setAdapter(adapter);
                    Log.i("Radhe", "Arraylist of tags is "+ list.toString());
                    handleClicks();
                }
            }
        });
    }

    private void fetchTaggedEventsFromParse(String tags) {

        String[] parts = (tags.replaceAll("\\s","")).split(",");

        List<ParseQuery<ParseObject>> queries = new ArrayList<ParseQuery<ParseObject>>();

        for(int i=0; i<parts.length; i++){
            ParseQuery myQuery = new ParseQuery("events").whereEqualTo("verified",true);
            myQuery.whereGreaterThanOrEqualTo("endDateTime", new Date());
            ArrayList<String> tagsList = new ArrayList<String>();
            tagsList.add(parts[i]);
            myQuery.whereContainsAll("tags", tagsList);
            queries.add(myQuery);
        }
        ParseQuery<ParseObject> mainQuery = ParseQuery.or(queries);
        mainQuery.orderByAscending("startDateTime");
        mainQuery.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                if(e==null){
                    feedsList = new ArrayList<EventsInfo>();
                    for(ParseObject post : list){
                        LocalDate d = new LocalDate(post.getDate("startDateTime"));
                        String[] parts = post.getDate("startDateTime").toString().split(" ");
                        String a = parts[2]+" "+parts[1];
                        feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                    }
                    adapter = new MyRecyclerAdapter(events.this, feedsList);
                    mRecyclerView.setAdapter(adapter);
                    Log.i("Radhe", "Arraylist of tags is "+ list.toString());
                    handleClicks();
                }
            }
        });
    }

    private void fetchAllEventsFromParse() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events").whereEqualTo("verified",true);
        query.whereLessThanOrEqualTo("target",target);
        query.whereGreaterThanOrEqualTo("endDateTime", new Date());
        query.orderByAscending("startDateTime");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> list, ParseException e) {
                feedsList = new ArrayList<EventsInfo>();
                for(ParseObject post : list){
                    LocalDate d = new LocalDate(post.getDate("startDateTime"));
                    String[] parts = post.getDate("startDateTime").toString().split(" ");
                    String a = parts[2]+" "+parts[1];
                    feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                }
                adapter = new MyRecyclerAdapter(events.this, feedsList);
                mRecyclerView.setAdapter(adapter);
                //progressBar.setVisibility(View.INVISIBLE);
                handleClicks();
            }
        });
    }

    private void showProcessDialogBox() {
        progressDialog = ProgressDialog.show( this, "Please Wait!", "Fetching Events", true);
        Log.i("Radhe","Inside Process Dialog Box");
    }

    private void fetchFromParse(String s) {
        final String search = s.toLowerCase();
        Log.i("Radhe","Hare Krishna "+search);
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.whereEqualTo("verified",true);
        query.whereGreaterThanOrEqualTo("endDateTime", new Date());
        query.selectKeys(Arrays.asList(search));
        query.orderByAscending("startDateTime");
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {

                if (e == null) {
                    postTexts = new ArrayList<String>();
                    for(ParseObject post : posts){
                        postTexts.add(post.getString(search));
                    }
                    removeDuplicates(postTexts);
                    populatePopupMenu(postTexts);
                    progressDialog.dismiss();
                    popupMenu.show();
                    if (popupMenu.getDragToOpenListener() instanceof ListPopupWindow.ForwardingListener)
                    {
                        ListPopupWindow.ForwardingListener listener = (ListPopupWindow.ForwardingListener) popupMenu.getDragToOpenListener();
                        listener.getPopup().setVerticalOffset(200);
                        listener.getPopup().setHorizontalOffset(-150);
                        listener.getPopup().show();
                    }
                    Log.i("Radhe","Hi we got sth from server "+search);
                }
                else {
                    Log.i("Radhe","nothing came from server "+e);
                    Toast.makeText(getApplicationContext(), "query error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void populatePopupMenu(List<String> postTexts) {
        for(int i=0 ; i<postTexts.size(); i++)
        {
            popupMenu.getMenu().add(1, i+1 , i+1, postTexts.get(i));// popupMenu.getMenu().add(groupId, itemId, order, title)
        }
        count = postTexts.size();
    }

    private void removeDuplicates(List<String> list) {
        int count = list.size();
        for (int i = 0; i < count; i++)
        {
            for (int j = i + 1; j < count; j++)
            {
                if (list.get(i).equals(list.get(j)))
                {
                    list.remove(j--);
                    count--;
                }
            }
        }
    }

    private void chooseRadius() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Near Me");
        alert.setMessage("Give the radius in miles");
// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);
        alert.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String inputRadius = input.getText().toString();
                //selectEventsFromParseAsPerRadius(Double.valueOf(inputRadius));
                Log.i("Radhe","Input Radius "+ inputRadius);
            }
        });
        alert.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });
        alert.show();
    }

    private void selectEventsFromParseAsPerRadius(double inputRadius) {
        LatLng myLatLng = getMyLocation();
        double myLat = myLatLng.latitude;
        double myLng = myLatLng.longitude;
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.whereEqualTo("verified",true);
        query.whereGreaterThanOrEqualTo("endDateTime", new Date());
        query.orderByAscending("startDateTime");
        //query.whereLessThanOrEqualTo(distFrom("latitude","longitude", myLatLng.latitude, myLatLng.longitude), inputRadius);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> posts, ParseException e) {

                if (e == null) {

                }
                else {
                    Log.i("Radhe","nothing came from server "+e);
                    Toast.makeText(getApplicationContext(), "query error: " + e, Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    public static double distFrom(double lat1, double lng1, double lat2, double lng2) {
        double earthRadius = 3958.75; // miles (or 6371.0 kilometers)
        double dLat = Math.toRadians(lat2-lat1);
        double dLng = Math.toRadians(lng2-lng1);
        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2));
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;
        return dist;
    }

    public LatLng getMyLocation() {
        LatLng myLatLng = new LatLng(0,0);
        return myLatLng;
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        Log.i("Radhe","Inside Menu Item Click");
        for(int i=0; i<count; i++)
            {
                if(item.getItemId() == i+1) {
                    Toast.makeText(this, "You Selected " + item, Toast.LENGTH_SHORT).show();
                    //progressBar.setVisibility(View.VISIBLE);
                    fetchEventsFromParse(item.toString());
                    return true;
                }
            }
        return false;
    }

    private void fetchEventsFromParse(String s) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("events");
        query.whereEqualTo(navigationDrawerItem,s);
        query.whereLessThanOrEqualTo("target",target).whereEqualTo("verified",true);
        query.whereGreaterThanOrEqualTo("endDateTime", new Date());
        query.orderByAscending("startDateTime");
        query.findInBackground(new FindCallback<ParseObject>() {
            public void done(List<ParseObject> posts, ParseException e) {
                if (posts == null) {
                    Log.i("Radhe", "The getFirst request failed. "+ e);
                } else {
                    feedsList = new ArrayList<EventsInfo>();
                    for(ParseObject post : posts){
                        LocalDate d = new LocalDate(post.getDate("startDateTime"));
                        //Log.i("Radhe", "The object id in fetching is "+post.getObjectId() );
                        String[] parts = post.getDate("startDateTime").toString().split(" ");
                        String a = parts[2]+" "+parts[1];
                        feedsList.add(new EventsInfo((ParseFile)post.getParseFile("image") , post.getObjectId() ,post.getString("eventName"),post.getString("city"),a ));//feedsList.add(post.getString(search));
                    }
                    //progressBar.setVisibility(View.GONE);
                    adapter = new MyRecyclerAdapter(getApplicationContext(), feedsList);
                    mRecyclerView.setAdapter(adapter);
                    handleClicks();
//                    Toast.makeText(getApplicationContext(), "Retrieved the object", Toast.LENGTH_SHORT).show();
//                    Log.i("Radhe", "Size of feedList " + feedsList.size());
                }
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
                    intent.putExtra("FlagFromEvents","events");
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

    class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private ClickListener clickListener;
        private GestureDetector gestureDetector;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final ClickListener clickListener){

            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener(){
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

//                @Override
//                public void onLongPress(MotionEvent e) {
//                   View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
//                    if(child!=null && clickListener!=null)
//                    {
//                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
//                        Log.i("TAG", "Radhe handling LongPress ");
//                    }
//                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
            boolean a = false;
            if(e.getAction()==MotionEvent.ACTION_UP && e.getAction()!=MotionEvent.ACTION_SCROLL) {
                View child = rv.findChildViewUnder(e.getX(), e.getY());
                if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)&& e.getAction()!=MotionEvent.ACTION_SCROLL);
                {
                    Log.i("Radhe", "Gopal inside onInterceptTouchEvent");
                    clickListener.onClick(child, rv.getChildPosition(child));
                    a = true;
                }
            }
            return a;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {

        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }

    }

    public static interface ClickListener{
        public void onClick(View view, int position);
        //public void onLongClick(View view, int position);

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
