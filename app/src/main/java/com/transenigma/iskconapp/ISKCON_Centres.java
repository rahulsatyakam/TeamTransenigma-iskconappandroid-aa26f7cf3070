package com.transenigma.iskconapp;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.readystatesoftware.systembartint.SystemBarTintManager;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ISKCON_Centres extends AppCompatActivity {

    double myLat, myLng;
    GPSTracker gps;
//  @Bind(R.id.toolbar) Toolbar toolbar;
    @Bind(R.id.Centrestabs) PagerSlidingTabStrip Centrestabs;
    @Bind(R.id.Centrespager) com.transenigma.iskconapp.NonSwipeableViewPager Centrespager;

    private CentresPagerAdapter centresPagerAdapter ;
    private Drawable oldBackground = null;
    private int currentColor;
    private SystemBarTintManager mTintManager;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iskcon__centres);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.iskconCentres_toolbar);
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
        getSupportActionBar().setTitle("ISKCON Centres");

        mTintManager = new SystemBarTintManager(this);
        mTintManager.setStatusBarTintEnabled(true);
        centresPagerAdapter = new CentresPagerAdapter(getSupportFragmentManager());
        Centrespager.setAdapter(centresPagerAdapter);
        Centrestabs.setViewPager(Centrespager);
        final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
        Centrespager.setPageMargin(pageMargin);
        Centrespager.setCurrentItem(1);
        changeColor(getResources().getColor(R.color.IskconTab));

        Centrestabs.setOnTabReselectedListener(new PagerSlidingTabStrip.OnTabReselectedListener() {
            @Override
            public void onTabReselected(int position) {
                Toast.makeText(getApplicationContext(), "Tab reselected: " + position, Toast.LENGTH_SHORT).show();
            }
        });

        gps = new GPSTracker(ISKCON_Centres.this);
        if(gps.canGetLocation()){
            myLat = gps.getLatitude();
            myLng = gps.getLongitude();
        }
        else{
            gps.showSettingsAlert();
        }

//        Bundle bundle = new Bundle();
//        bundle.putDouble("myLat", myLat);
//        bundle.putDouble("myLng", myLng);
//        MyMapFragment fragobj = new MyMapFragment();
//        fragobj.setArguments(bundle);
    }

    public LatLng getMyLatLng()
    {
        return new LatLng(myLat,myLng);
    }

    private void changeColor(int newColor) {
        Centrestabs.setBackgroundColor(newColor);
        mTintManager.setTintColor(newColor);
        // change ActionBar color just if an ActionBar is available
        Drawable colorDrawable = new ColorDrawable(newColor);
        Drawable bottomDrawable = new ColorDrawable(getResources().getColor(android.R.color.transparent));
        LayerDrawable ld = new LayerDrawable(new Drawable[]{colorDrawable, bottomDrawable});
        if (oldBackground == null) {
            getSupportActionBar().setBackgroundDrawable(ld);
        } else {
            TransitionDrawable td = new TransitionDrawable(new Drawable[]{oldBackground, ld});
            getSupportActionBar().setBackgroundDrawable(td);
            td.startTransition(200);
        }

        oldBackground = ld;
        currentColor = newColor;
    }
    public class CentresPagerAdapter extends FragmentPagerAdapter {

        private final String[] TITLES = {"Near Me", "Restraunts", "Farms", "Temples", "Bhakti Centres", "Preaching Centres"};

        public CentresPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return TITLES[position];
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }
        @Override
        public android.support.v4.app.Fragment getItem(int position) {
            //Log.i("Radhe", "In Iskcon_centres Latitude is "+latitude+" and longitude is "+longitude);
            return MyMapFragment.newInstance(position);
        }
    }

}
