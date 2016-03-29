package com.transenigma.iskconapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SuperAwesomeCardFragment extends Fragment {

    private static final String ARG_POSITION = "position";

    @Bind(R.id.textView) TextView textView;
    @Bind(R.id.myAboutTitle) TextView myAboutTitle;
    @Bind(R.id.myAboutImg) ImageView myAboutImg;

    private int position;

    public static SuperAwesomeCardFragment newInstance(int position) {
        SuperAwesomeCardFragment f = new SuperAwesomeCardFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        position = getArguments().getInt(ARG_POSITION);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_card,container,false);
        ButterKnife.bind(this, rootView);
        ViewCompat.setElevation(rootView,50);

        switch (position) {
            case 0:
                myAboutImg.setImageResource(R.drawable.headquarters);
                textView.setText(getContext().getString(R.string.headquarters));
                myAboutTitle.setText("Headquarters");
                break;
            case 1:
                myAboutImg.setImageResource(R.drawable.mayapur);
                textView.setText(getContext().getString(R.string.radha));
                myAboutTitle.setText("ISKCON");
                break;
            case 2:
                myAboutImg.setImageResource(R.drawable.sp);
                textView.setText(getContext().getString(R.string.sp));
                myAboutTitle.setText("Srila Prabhupada");
                break;
            case 3:
                myAboutImg.setImageResource(R.drawable.history);
                textView.setText(getContext().getString(R.string.history));
                myAboutTitle.setText("History");
                break;
            case 4:
                myAboutImg.setImageResource(R.drawable.foodforlife);
                textView.setText(getContext().getString(R.string.foodforlife));
                myAboutTitle.setText("Food For Life");
                break;
            default:
                break;
        }

//        myAboutImg.setImageResource(R.drawable.img2);
        //textView.setText("Text Page"+position);
        return rootView;
    }
}
