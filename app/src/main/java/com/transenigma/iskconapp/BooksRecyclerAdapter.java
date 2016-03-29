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

public class BooksRecyclerAdapter extends RecyclerView.Adapter<BooksRecyclerAdapter.CustomViewHolder> {

    private List<BooksInfo> feedItemList;
    private Context mContext;
    private int mPreviousPosition = 0;
    //    OnItemClickListener mItemClickListener;
    public BooksRecyclerAdapter(Context context, List<BooksInfo> feedItemList) {
        this.feedItemList = feedItemList;
        this.mContext = context;
    }
    @Override
    public BooksRecyclerAdapter.CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_books_info, parent, false);
        CustomViewHolder viewHolder = new CustomViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {

        BooksInfo feedItem = feedItemList.get(position);
        //Setting text view title
        holder.myBookTitle.setText(Html.fromHtml(feedItem.getBookName()));
        holder.myBookAuthor.setText(Html.fromHtml(feedItem.getBookAuthor()));
        String imageName = feedItem.getBookName().replaceAll("\\s+","").toLowerCase();
        holder.myBookImg.setImageResource(mContext.getResources().getIdentifier(imageName, "drawable", mContext.getPackageName() ));
        /*******************Animation**************************/
        if (position > mPreviousPosition) {
            AnimationUtils.animateSunblind(holder, true);
//            AnimationUtils.animate1(holder, true);
//            AnimationUtils.animate(holder,true);
        } else {
            AnimationUtils.animateSunblind(holder, false);
//            AnimationUtils.animate1(holder, false);
//            AnimationUtils.animate(holder, false);
        }
        mPreviousPosition = position;
    }

    @Override
    public int getItemCount() {
        return (null != feedItemList ? feedItemList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{
        protected ImageView myBookImg;
        protected TextView myBookTitle,myBookAuthor;

        public CustomViewHolder(View view) {
            super(view);
            this.myBookImg = (ImageView) view.findViewById(R.id.myBookImg);
            this.myBookTitle = (TextView) view.findViewById(R.id.myBookTitle);
            this.myBookAuthor = (TextView) view.findViewById(R.id.myBookAuthor);
        }
    }
}
