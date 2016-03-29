package com.transenigma.iskconapp.eventmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

public abstract class SimpleFragment extends Fragment {


    public SimpleFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         return super.onCreateView(inflater, container, savedInstanceState);
    }

    protected LineData generateLineData() {

        ArrayList<ILineDataSet> sets = new ArrayList<ILineDataSet>();
        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < 30; i++) {
            xVals.add(i+"");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < 30; i++) {
            int val = i;
            yVals.add(new Entry(val, i));
        }

        LineDataSet ds1 = new LineDataSet(yVals, "Daily Views");

        ds1.setLineWidth(2f);

        ds1.setDrawCircles(false);

        ds1.setColor(Color.BLACK);

        // load DataSets from textfiles in assets folders
        sets.add(ds1);
        int max = Math.max(sets.get(0).getEntryCount(), sets.get(0).getEntryCount());
        LineData d = new LineData(ChartData.generateXVals(0, max),  sets);
        return d;
    }
}
