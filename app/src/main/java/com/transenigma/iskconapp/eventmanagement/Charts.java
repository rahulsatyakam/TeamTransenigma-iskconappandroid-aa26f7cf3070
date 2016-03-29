package com.transenigma.iskconapp.eventmanagement;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.transenigma.iskconapp.R;

import it.neokree.materialtabs.MaterialTab;
import it.neokree.materialtabs.MaterialTabHost;
import it.neokree.materialtabs.MaterialTabListener;


public class Charts extends AppCompatActivity implements MaterialTabListener{

    MaterialTabHost tabHost;
    ViewPager pager;
    ViewPagerAdapter adapter;
    private ProgressDialog  finalProgressDialog;
    String name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_charts);
        finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Data", true);
        final Bundle bundle = getIntent().getExtras();
        if (bundle!= null) {
            name = bundle.getString("chartType");
            Log.i("Radhe",name);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.charts_toolbar);
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
        getSupportActionBar().setTitle(name +" Charts");


        tabHost = (MaterialTabHost) this.findViewById(R.id.tabHost);
        pager = (ViewPager) this.findViewById(R.id.pager );
        // init view pager
        adapter = new ViewPagerAdapter(getSupportFragmentManager(),bundle);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                System.out.println(position);
                tabHost.setSelectedNavigationItem(position);
            }
        });
        // insert all tabs from pagerAdapter data
        for (int i = 0; i < adapter.getCount(); i++) {
            tabHost.addTab(
                    tabHost.newTab()
                            .setText(adapter.getPageTitle(i))
                            .setTabListener(Charts.this));
        }
        finalProgressDialog.dismiss();
    }

    @Override
    public void onTabSelected(MaterialTab tab) {
        pager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabReselected(MaterialTab tab) {
    }

    @Override
    public void onTabUnselected(MaterialTab tab) {
    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        private Bundle bundle;
        public ViewPagerAdapter(FragmentManager fm,Bundle bundle) {
            super(fm);
            this.bundle = bundle;
        }

        public Fragment getItem(int num) {
            switch (num) {
                case 0:
                    FragmentText2 f =new FragmentText2();
                    if(bundle!=null){
                        Log.i("Charts","Empty bundle");
                    }
                    f.setArguments(bundle);
                    return f;
//                case 1:
//                    return new FragmentText1();
                case 1:
                    FragmentText f2 = new FragmentText();
                    if(bundle!=null){
                            Log.i("Charts","Empty bundle");
                    }
                    f2.setArguments(bundle);
                    return f2;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if(position==1){
                return "Gender";
            }else{
                if(position==0){
                    return "Age Group";
                }
                else{
                    return "Time";
                }
            }
        }
    }
}
