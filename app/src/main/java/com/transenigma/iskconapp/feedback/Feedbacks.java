package com.transenigma.iskconapp.feedback;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.transenigma.iskconapp.R;

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
import java.util.List;


public class Feedbacks extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private String[] mPlanets ;
    private String[] mPlanets1 ;
    private FloatingActionButton fab;
    private class PlanetViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected TextView myTitle,feedback;
        public PlanetViewHolder(View v) {
            super(v);
            this.myTitle = (TextView) v.findViewById(R.id.myTitle);
            this.feedback = (TextView) v.findViewById(R.id.text1);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Toast.makeText(getApplicationContext(),
                    ((TextView) v.findViewById(R.id.text1)).getText(),
                    Toast.LENGTH_LONG).show();
        }
    }
    private String myEventObjectId = "";

    private ProgressDialog  finalProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedbacks);

        Toolbar toolbar = (Toolbar) findViewById(R.id.feedbacks_toolbar);
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
        getSupportActionBar().setTitle("Feedback");

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            myEventObjectId = bundle.getString("myEventObjectId");
        }
        finalProgressDialog = ProgressDialog.show(this, "Please Wait!", "Fetching Feedbacks", true);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
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
                        for(String key :post.keySet() ){
                            Log.i("Feedback",key);
                        }
                        ParseObject user = post.getParseObject("user");
                        stockList.add(post.getString("feedback"));
                        stockList1.add(user.getString("legalname"));
                        Log.i("Feedback", user.getString("legalname"));
                        Log.i("Feedback", post.getString("feedback"));
                    }
                    finalProgressDialog.dismiss();
                    mPlanets = new String[stockList.size()];
                    mPlanets = stockList.toArray(mPlanets);
                    mPlanets1 = new String[stockList1.size()];
                    mPlanets1 = stockList1.toArray(mPlanets1);
                    mRecyclerView.setAdapter(new RecyclerView.Adapter<PlanetViewHolder>() {
                        @Override
                        public PlanetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.feedbacks_row, parent, false);
                            PlanetViewHolder vh = new PlanetViewHolder(v);
                            return vh;
                        }
                        @Override
                        public void onBindViewHolder(PlanetViewHolder vh, int position) {
                            TextView tv = (TextView) vh.feedback;
                            tv.setText(mPlanets[position].trim());
                            TextView tv1 = (TextView) vh.myTitle;
                            tv1.setText(mPlanets1[position].trim());
                        }
                        @Override
                        public int getItemCount() {
                            return mPlanets.length;
                        }
                    });
                    fab = (FloatingActionButton) findViewById(R.id.savefeedbacks);
                    fab.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View v) {
                            try {
                                checkExternalMedia();
                                if(writeToSDFile(mPlanets))
                                    Toast.makeText(v.getContext(), "Done writing SD 'feedbacks.xlsx'", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(v.getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            Log.i("Feedback", "Exception" + e);
        }
        // new String[] { "The prasadam was very good","","The venue could have been bigger","The kirtans was very nice"};
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
        File myDir = new File(root + "/Feedbacks");
        myDir.mkdirs();
        Log.i("Feedback", "External file system root: " + root);
        File file = new File(myDir, "feedback.xlsx");
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
