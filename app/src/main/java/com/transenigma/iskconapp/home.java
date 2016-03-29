package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class home extends AppCompatActivity implements AdapterView.OnItemClickListener {

    static final String EXTRA_MAP = "map";
    ProgressDialog progressDialog;
    static ProgressDialog homeProgressDialog;
//    static final LauncherIcon[] ICONS = {
//            new LauncherIcon(R.drawable.events, "Events", "events.png"),
//            new LauncherIcon(R.drawable.aboutus, "About", "aboutus.png"),
//            new LauncherIcon(R.drawable.iskconhome, "Centres", "iskconhome.png"),
//            new LauncherIcon(R.drawable.books, "Books", "books.png"),
//            new LauncherIcon(R.drawable.setting, "Settings", "setting.png"),
//            new LauncherIcon(R.drawable.news, "News", "news.png")
//    };
static final LauncherIcon[] ICONS = {
        new LauncherIcon(R.drawable.events, "Events", "events.png"),
        new LauncherIcon(R.drawable.aboutus, "About", "aboutus.png"),
        new LauncherIcon(R.drawable.news, "News", "news.png"),
        new LauncherIcon(R.drawable.books, "Books", "books.png"),
        new LauncherIcon(R.drawable.iskconhome, "Centres", "iskconhome.png"),
        new LauncherIcon(R.drawable.setting, "Settings", "setting.png")
};
    GPSTracker gps;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getAttributes().windowAnimations = R.style.Fade;
        //overridePendingTransition(R.anim.fadein, R.anim.fadeout);
        setContentView(R.layout.activity_home);
        getWindow().getDecorView().setBackgroundColor(Color.BLACK);
        FacebookSdk.sdkInitialize(getApplicationContext());

        GridView gridview = (GridView) findViewById(R.id.dashboard_grid);
        gridview.setAdapter(new ImageAdapter(this));
        gridview.setOnItemClickListener(this);

        // Hack to disable GridView scrolling
        gridview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return event.getAction() == MotionEvent.ACTION_MOVE;
            }
        });

        gps = new GPSTracker(home.this);
        if(!gps.canGetLocation()){
            gps.showSettingsAlert();
        }
    }

//    public void clickAddEventBtn(View view) {
//        startActivity(new Intent(this, events.class));
//    }
//
//    public void clickAbout(View v) {
//        startActivity(new Intent(this, about.class));
//    }
//
//    public void clickReadBooks(View view) {
//        PDFTools.showPDFUrl(this, "http://www.radiokrishna.com/rkc_archive_new/Books/ENG/Beyond_Birth_and_Death.pdf");
//    }
//
//    public void clickIskconCentres(View view) {
//        startActivity(new Intent(this, ISKCON_Centres.class));
//    }

//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_home, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        RelativeLayout myview = (RelativeLayout) findViewById(R.id.myHomePage);
//
//        switch(item.getItemId()){
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
//                startActivity(new Intent(this, profile.class));
//                return true;
//
//            case R.id.myVisitedEvents:
//                if(item.isChecked())
//                    item.setChecked(false);
//                else
//                    item.setChecked(true);
//                myview.setBackgroundColor(Color.YELLOW);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }

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

    public void clearCookies(){
        CookieSyncManager.createInstance(home.this);
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.removeAllCookie();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        switch(position){
            case 0:

                if(isConnectedToInternet()) {
                    homeProgressDialog = ProgressDialog.show( home.this, "Please Wait!", "Loading events", true);
                    startActivity(new Intent(this, events.class));
                }
                else{
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            case 1:

                 startActivity(new Intent(this, about.class));
                break;
            case 2:

                if(isConnectedToInternet()) {
                    startActivity(new Intent(this, RssFeeds.class));
                } else{
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            case 3:

                if(isConnectedToInternet()) {
                    startActivity(new Intent(this, bookActivity.class));
                } else{
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            case 4:

                if(isConnectedToInternet()) {
                    startActivity(new Intent(this, ISKCON_Centres.class));
                } else{
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            case 5:

                if(isConnectedToInternet()) {
                    showSettingsMenu();
                } else{
                    Toast.makeText(this,"No internet connection",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                break;
            default:
                break;
        }
    }

    private void showSettingsMenu() {
        final View dialogView = View.inflate(this, R.layout.homesettings, null);
        final android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(this).create();
        LinearLayout myHomeProfile = (LinearLayout) dialogView.findViewById(R.id.myHomeProfile);
        LinearLayout myHomeLogout = (LinearLayout) dialogView.findViewById(R.id.myHomeLogout);

        myHomeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    validatePassword();
                    alertDialog.dismiss();
            }
        });

        myHomeLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                LoginManager.getInstance().logOut();
                clearCookies();
                startActivity(new Intent(home.this, start.class));
                finish();
                alertDialog.dismiss();
            }
        });


        alertDialog.setView(dialogView);
        alertDialog.show();
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
                progressDialog = ProgressDialog.show( home.this, "Please Wait!", "Authenticating", true);
                String password = input.getText().toString();
                ParseUser.logInInBackground(ParseUser.getCurrentUser().getUsername(), password, new LogInCallback() {
                    public void done(ParseUser user, ParseException e) {
                        if (user != null) {
                            startActivity(new Intent(home.this, profile.class));
                            progressDialog.dismiss();
                        } else {
                           Toast.makeText(home.this,"The password given is not correct, Please try again", Toast.LENGTH_LONG).show();
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

    static class LauncherIcon {
        final String text;
        final int imgId;
        final String map;

        public LauncherIcon(int imgId, String text, String map) {
            super();
            this.imgId = imgId;
            this.text = text;
            this.map = map;
        }
    }

    static class ImageAdapter extends BaseAdapter {
        private Context mContext;

        public ImageAdapter(Context c) {
            mContext = c;
        }

        @Override
        public int getCount() {
            return ICONS.length;
        }

        @Override
        public LauncherIcon getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        static class ViewHolder {
            public ImageView icon;
            public TextView text;
        }

        // Create a new ImageView for each item referenced by the Adapter
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            ViewHolder holder;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) mContext.getSystemService(
                        Context.LAYOUT_INFLATER_SERVICE);

                v = vi.inflate(R.layout.dashboard_icon, null);
                holder = new ViewHolder();
                holder.text = (TextView) v.findViewById(R.id.dashboard_icon_text);
                holder.icon = (ImageView) v.findViewById(R.id.dashboard_icon_img);
                v.setTag(holder);
            } else {
                holder = (ViewHolder) v.getTag();
            }

            holder.icon.setImageResource(ICONS[position].imgId);
            holder.text.setText(ICONS[position].text);

            return v;
        }
    }
}
