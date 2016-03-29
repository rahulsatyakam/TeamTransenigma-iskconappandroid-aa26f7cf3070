package com.transenigma.iskconapp.eventmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.transenigma.iskconapp.R;

import java.util.ArrayList;

public class FragmentText1 extends Fragment{


    private LineChart mChart;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_linechart, container, false);
        mChart = (LineChart) v.findViewById(R.id.chart2);
        LineData data = getData(40, 100);
        setupChart(mChart, data, Color.rgb(137,230,81));
        return v;
    }

    private void setupChart(LineChart chart, LineData data, int color) {

        // no description text
        chart.setDescription("");
        chart.setNoDataTextDescription("You need to provide data for the chart.");


        // enable / disable grid background
        chart.setDrawGridBackground(true);
//        chart.getRenderer().getGridPaint().setGridColor(Color.WHITE & 0x70FFFFFF);

        // enable touch gestures
        chart.setTouchEnabled(true);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);

        // if disabled, scaling can be done on x- and y-axis separately
        chart.setPinchZoom(true);

//        chart.setBackgroundColor(color);

        // set custom chart offsets (automatic offset calculation is hereby disabled)
        chart.setViewPortOffsets(10, 0, 10, 0);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.RED);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setEnabled(true);
        // add data
        chart.setData(data);
        // get the legend (only possible after setting data)
        chart.getAxisLeft().setDrawLabels(true);
        chart.getAxisRight().setDrawLabels(true);
        Legend l = chart.getLegend();
        l.setEnabled(true);
        // animate calls invalidate()...
        chart.getXAxis().setDrawLabels(true);
        chart.animateX(2500);
    }

    private LineData getData(int count, int range) {

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i = 0; i < count; i++) {
            xVals.add(i+"");
        }

        ArrayList<Entry> yVals = new ArrayList<Entry>();

        for (int i = 0; i < count; i++) {
            float val = (float) (Math.random() * range) + range / 4 ;
            yVals.add(new Entry(val, i));
        }

        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(yVals, "Daily Views");
        // set1.setFillAlpha(110);
        // set1.setFillColor(Color.RED);

        set1.setLineWidth(1f);
        set1.setCircleRadius(2f);
        set1.setColor(Color.BLACK);
        set1.setCircleColor(Color.BLUE);
        set1.setHighLightColor(Color.GREEN);
        set1.setDrawValues(false);
        set1.setDrawCubic(true);
        ArrayList<ILineDataSet> dataSets = new ArrayList<ILineDataSet>();
        dataSets.add(set1); // add the datasets

        // create a data object with the datasets
        LineData data = new LineData(xVals, dataSets);
        return data;
    }
}
