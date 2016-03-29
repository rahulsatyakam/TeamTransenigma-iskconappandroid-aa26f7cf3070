package com.transenigma.iskconapp.eventmanagement;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.transenigma.iskconapp.R;
import com.transenigma.iskconapp.edit_event;
import com.transenigma.iskconapp.feedback.Feedbacks;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EventManagement extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    int ageArray [] = {0,0,0,0};
    int genderArray [] = {0,0};
    String myEventObjectId = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            myEventObjectId = bundle.getString("myEventObjectId");
        }
        setContentView(R.layout.activity_event_management);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
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
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
         return super.onOptionsItemSelected(item);
    }

    private String[] mPlanets ;
    private String[] mPlanets1 ;
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Arrays.fill(ageArray, 0);
        Arrays.fill(genderArray, 0);
        if (id == R.id.nav_camera) {
            final Intent  intent;
            intent = new Intent(this,Charts.class);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.whereEqualTo("liked", true);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user");
                            getDataForCharts(user);
                        }
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Likes");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        } else if (id == R.id.nav_gallery) {
            final Intent  intent;
            intent = new Intent(this,Feedbacks.class);
            intent.putExtra("myEventObjectId", myEventObjectId);
            startActivity(intent);
        } else if (id == R.id.nav_slideshow) {
            final Intent intent;
            intent = new Intent(this,Charts.class);
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching All Views", true);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user"); ;
                            getDataForCharts(user);
                        }
                        finalProgressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Views");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        } else if (id == R.id.nav_manage) {
            final Intent intent;
            intent = new Intent(this,Charts.class);
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Data", true);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.whereEqualTo("attended", true);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user"); ;
                            getDataForCharts(user);
                        }
                        finalProgressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Attended");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        } else if (id == R.id.nav_manage1) {
            final Intent intent;
            intent = new Intent(this,Charts.class);
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Data", true);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.whereEqualTo("flag", 1);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user"); ;
                            getDataForCharts(user);
                        }
                        finalProgressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Interested");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        } else if (id == R.id.nav_manage2) {
            final Intent intent;
            intent = new Intent(this,Charts.class);
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Data", true);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.whereEqualTo("flag", 2);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user"); ;
                            getDataForCharts(user);
                        }
                        finalProgressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Maybe");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        }  else if (id == R.id.nav_manage3) {
            final Intent intent;
            intent = new Intent(this,Charts.class);
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Data", true);
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId);
                myQuery1.whereEqualTo("flag", 3);
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            for(String key :post.keySet() ){
                                Log.i("Radhe", key);
                            }
                            ParseObject user = post.getParseObject("user"); ;
                            getDataForCharts(user);
                        }
                        finalProgressDialog.dismiss();
                        Bundle bundle = new Bundle();
                        bundle.putIntArray("ageArray", ageArray);
                        bundle.putIntArray("genderArray", genderArray);
                        bundle.putString("myEventObjectId", myEventObjectId);
                        bundle.putString("chartType", "Not Interested");
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Log.i("Radhe", "Exception" + e);
            }
        }  else if (id == R.id.nav_manage5) {
            Intent intent = new Intent(this, edit_event.class);
            intent.putExtra("myEventObjectId",myEventObjectId);
            startActivity(intent);
        } else if (id == R.id.nav_manage4) {
            final ProgressDialog finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Exporting All", true);
            final List<String> stockList = new ArrayList<String>();
            final List<String> stockList1 = new ArrayList<String>();
            try {
                ParseQuery myQuery1 = new ParseQuery("Event_Management");
                myQuery1.include("user");
                myQuery1.whereEqualTo("event", myEventObjectId); ;
                myQuery1.findInBackground(new FindCallback<ParseObject>() {
                    @Override
                    public void done(List<ParseObject> list, ParseException e) {
                        for (ParseObject post : list) {
                            ParseObject user = post.getParseObject("user");
                            stockList1.add(user.getString("legalname"));
                            Log.i("Feedback", user.getString("legalname"));
                        }
                        finalProgressDialog.dismiss();
                        mPlanets1 = new String[stockList1.size()];
                        mPlanets1 = stockList1.toArray(mPlanets1);
                        try {
                            checkExternalMedia();
                            if(writeToSDFile(mPlanets1))
                                Toast.makeText(EventManagement.this, "Done writing SD 'Details.xlsx'", Toast.LENGTH_SHORT).show();
                        } catch (Exception ex) {
                            Toast.makeText(EventManagement.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }catch (Exception e) {
                e.printStackTrace();
                Log.i("Feedback", "Exception" + e);
            }
        }
        return true;
    }

    public void getDataForCharts(ParseObject post) {
        int age = -1;
        if(post.getString("age")!=null){
            age = Integer.parseInt(post.getString("age"));
            if(age!= -1) {
                if (age < 20) ageArray[0]++;
                else if (20 <= age & age < 35) ageArray[1]++;
                else if (35 <= age & age < 50) ageArray[2]++;
                else if (age >= 50) ageArray[3]++;
            }
        }
        String gender = post.getString("gender");
        if(gender!=null) {
            if (gender.equals("Male")) genderArray[0]++;
            else if (gender.equals("Female")) genderArray[1]++;
        }
    }

    private void checkExternalMedia(){
        boolean mExternalStorageAvailable = false;
        boolean mExternalStorageWriteable = false;
        String state = Environment.getExternalStorageState();

        if (Environment.MEDIA_MOUNTED.equals(state)) {
            // Can read and write the media
            mExternalStorageAvailable = mExternalStorageWriteable = true;
        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            // Can only read the media
            mExternalStorageAvailable = true;
            mExternalStorageWriteable = false;
        } else {
            // Can't read or write
            mExternalStorageAvailable = mExternalStorageWriteable = false;
        }
        Log.i("Feedback", "External Media: readable="+ mExternalStorageAvailable + " writable=" + mExternalStorageWriteable);
    }

    private static boolean writeToSDFile(String[] mPlanets) {

        boolean success = false;
        //New Workbook
        Workbook wb = new HSSFWorkbook();

        Cell c = null;

        //Cell style for header row
        CellStyle cs = wb.createCellStyle();
        cs.setFillForegroundColor(HSSFColor.LIME.index);
        cs.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);

        //New Sheet
        Sheet sheet1 = null;
        sheet1 = wb.createSheet("Feedbacks");

        // Generate column headings
        Row row = sheet1.createRow(0);

        c = row.createCell(0);
        c.setCellValue("Feedback");
        c.setCellStyle(cs);

        c = row.createCell(1);
        c.setCellValue("Name");
        c.setCellStyle(cs);

        c = row.createCell(2);
        c.setCellValue("Gender");
        c.setCellStyle(cs);

        c = row.createCell(3);
        c.setCellValue("Age");
        c.setCellStyle(cs);

        sheet1.setColumnWidth(0, (15 * 500));
        sheet1.setColumnWidth(1, (15 * 500));
        sheet1.setColumnWidth(2, (15 * 500));
        sheet1.setColumnWidth(3, (15 * 500));

        int rownum = sheet1.getLastRowNum() + 1;

        for (String key : mPlanets) {
            row = sheet1.createRow(rownum++);
            int cellnum = 0;
            Cell cell = row.createCell(cellnum++);
            cell.setCellValue(key);
        }
        String root = Environment.getExternalStorageDirectory().toString();
        File myDir = new File(root + "/Details");
        myDir.mkdirs();
        Log.i("Feedback", "External file system root: " + root);
        File file = new File(myDir, "Details.xlsx");
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            wb.write(os);
            success = true;
        } catch (IOException e) {
        } catch (Exception e) {
        } finally {
            try {
                if (null != os)
                    os.close();
            } catch (Exception ex) {
            }
        }
        Log.i("Feedback", "File written to " + file);
        return success;
    }

}
