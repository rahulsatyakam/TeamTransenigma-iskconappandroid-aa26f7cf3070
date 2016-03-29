package com.transenigma.iskconapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import java.util.List;

public class MyRecyclerAdapter extends RecyclerView.Adapter<MyRecyclerAdapter.CustomViewHolder> {
    ProgressDialog progress;
    View view;
    String flag="";
    private List<EventsInfo> feedItemList;
    private Context mContext;
    private int mPreviousPosition = 0;
//    OnItemClickListener mItemClickListener;
    public MyRecyclerAdapter(Context context, List<EventsInfo> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    public MyRecyclerAdapter(Context context, List<EventsInfo> feedItemList, String flag) {
        this.feedItemList = feedItemList;
        this.mContext = context;
        this.flag = flag;
    }
    @Override
    public MyRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if(flag=="News"){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_news_row, parent, false);
        }
        else  view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_event_row, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyRecyclerAdapter.CustomViewHolder holder, int position) {
        if(flag=="News"){
            EventsInfo feedItem = feedItemList.get(position);
            holder.myEventTitle.setText(Html.fromHtml(feedItem.getMyEventTitle()));
            holder.myEventCity.setText(Html.fromHtml(feedItem.getMyEventCity()));
            holder.myEventObjectId.setText(feedItem.getEventObjectId());
            //Log.i("Radhe", "Object Id  is " + holder.myEventObjectId.getText());
            loadImages(feedItem.getImageFile(), holder.myEventImg);
            /*******************Animation**************************/
          /*  if (position > mPreviousPosition) {
                AnimationUtils.animateSunblind(holder, true);
            } else {
                AnimationUtils.animateSunblind(holder, false);
            }
            mPreviousPosition = position;*/
        }
        else {
            EventsInfo feedItem = feedItemList.get(position);
            holder.myEventTitle.setText(Html.fromHtml(feedItem.getMyEventTitle()));
            holder.myEventCity.setText(Html.fromHtml(feedItem.getMyEventCity()));
            holder.myEventObjectId.setText(feedItem.getEventObjectId());
            String[] parts = (feedItem.getMyEventDate()).split(" ");
            holder.myAboveDay.setText(parts[0]);
            holder.myAboveMonth.setText(parts[1]);
            Log.i("Radhe", "Object Id  is " + holder.myEventObjectId.getText());
            loadImages(feedItem.getImageFile(), holder.myEventImg);
            /*******************Animation**************************/
          /*  if (position > mPreviousPosition) {
                AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
            } else {
                AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
            }
            mPreviousPosition = position;*/
        }
    }
    private void loadImages(ParseFile thumbnail, final ImageView img) {

        if (thumbnail != null) {
            thumbnail.getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
                        img.setImageBitmap(bmp);
                        //progress.dismiss();
                    } else {
                    }
                }
            });
        } else {
            img.setImageResource(R.drawable.mayapur);
        }

    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }


    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected ImageView myEventImg;
        protected TextView myEventTitle,myEventCity, myEventObjectId, myAboveDay, myAboveMonth;//,myEventDate

        public CustomViewHolder(View view) {
            super(view);
            this.myEventImg = (ImageView) view.findViewById(R.id.myEventImg);
            this.myEventTitle = (TextView) view.findViewById(R.id.myEventTitle);
            this.myEventCity = (TextView) view.findViewById(R.id.myEventCity);
            //this.myEventDate = (TextView) view.findViewById(R.id.myEventDate);
            this.myEventObjectId = (TextView) view.findViewById(R.id.myEventObjectId);
            this.myAboveDay = (TextView) view.findViewById(R.id.myAboveDay);
            this.myAboveMonth = (TextView) view.findViewById(R.id.myAboveMonth);
        }
    }
}
