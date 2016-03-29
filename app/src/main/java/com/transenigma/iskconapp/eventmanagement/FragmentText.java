package com.transenigma.iskconapp.eventmanagement;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.transenigma.iskconapp.R;

import java.util.ArrayList;

public class FragmentText extends Fragment{

    public static Fragment newInstance() {
        return new FragmentText2();
    }

    private PieChart mChart;
    private String name;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_piechart, container, false);

        mChart = (PieChart) v.findViewById(R.id.chart1);
        mChart.setUsePercentValues(false);
        mChart.setDescription("");
        mChart.setExtraOffsets(5, 10, 5, 5);
        mChart.setDragDecelerationFrictionCoef(0.95f);
        mChart.setDrawHoleEnabled(true);
        mChart.setHoleColorTransparent(true);
        mChart.setTransparentCircleColor(Color.WHITE);
        mChart.setTransparentCircleAlpha(110);
        mChart.setDrawCenterText(true);
        mChart.setRotationAngle(0);
        mChart.setRotationEnabled(true);
        mChart.setHighlightPerTapEnabled(true);
        mChart.setCenterTextSize(8f);
        mChart.setHoleRadius(35f);
        mChart.setTransparentCircleRadius(40f);
        mChart.animateY(500, Easing.EasingOption.EaseInOutQuad);
        Legend l =  mChart.getLegend();
        l.setPosition(LegendPosition.BELOW_CHART_CENTER);
        Bundle bundle = getArguments();
        name = bundle.getString("chartType");
        mChart.setCenterText(generateCenterText());
        int[] genderArray = (int[]) bundle.getIntArray("genderArray");
        mChart.setData(generatePieData(genderArray));
        return v;
    }
    protected PieData generatePieData(int[] genderArray) {

        int count = 2;
        ArrayList<Entry> entries1 = new ArrayList<Entry>();
        ArrayList<String> xVals = new ArrayList<String>();
        String[] lab = {"Male","Female"};
         for(int i = 0; i < count; i++) {
            if(genderArray[i]!=0) {
                xVals.add(lab[i]);
                entries1.add(new Entry(genderArray[i], i));
            }
        }
        PieDataSet ds1 = new PieDataSet(entries1, "Gender Wise "+name+" Distribution");
        ds1.setColors(ColorTemplate.VORDIPLOM_COLORS);
        ds1.setSliceSpace(2f);
        ds1.setValueTextSize(12f);

        PieData d = new PieData(xVals, ds1);
        d.setValueFormatter(new MyValueFormatter());
        return d;
    }
    private SpannableString generateCenterText() {
        SpannableString s = new SpannableString("Gender Wise \n"+name+" Distribution");
        s.setSpan(new RelativeSizeSpan(2f), 0, 12, 0);
        s.setSpan(new ForegroundColorSpan(Color.GRAY), 12, s.length(), 0);
        return s;
    }
}
