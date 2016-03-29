package com.transenigma.iskconapp;

/**
 * Created by Rahul on 3/29/2016.
 */

import android.app.Activity;
import android.os.Bundle;
import android.widget.GridView;

public class bookActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookgrid_layout);

        GridView gridView = (GridView) findViewById(R.id.grid_view);

        // Instance of ImageAdapter Class
        gridView.setAdapter(new bookAdapter(this));
    }
}
