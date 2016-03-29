package com.transenigma.iskconapp;

/**
 * Created by Rahul on 3/29/2016.
 */
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class bookAdapter extends BaseAdapter {
    private Context mContext;

    // Keep all Images in array
    public Integer[] mThumbIds = {
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history, R.drawable.history,
            R.drawable.history
    };

    // Constructor
    public bookAdapter(Context c){
        mContext = c;
    }

    @Override
    public int getCount() {
        return mThumbIds.length;
    }

    @Override
    public Object getItem(int position) {
        return mThumbIds[position];
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = new ImageView(mContext);
        imageView.setImageResource(mThumbIds[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(160, 180));
        return imageView;
    }

}