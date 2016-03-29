package com.transenigma.iskconapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Pranav on 2/3/2016.
 */
public class BlurImageView extends ImageView {

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        final Drawable d = this.getDrawable();

        if (d != null) {
            // ceil not round - avoid thin vertical gaps along the left/right edges
            final int width = MeasureSpec.getSize(widthMeasureSpec);
            final int height = (int) Math.ceil(width * (float) d.getIntrinsicHeight() / d.getIntrinsicWidth());
            this.setMeasuredDimension(width, height);
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    Paint rectPaint;

    private  int blurcolor= Color.parseColor("#aeffffff");

    public BlurImageView(Context context) {
        this(context, null);

    }

    public BlurImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);

    }

    public BlurImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rectPaint=new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Paint.Style.FILL);
        rectPaint.setColor(blurcolor);
        invalidate();
    }

    public void setBlurcolor(int blurcolor) {
        this.blurcolor = blurcolor;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Log.i("Radhe","canvas");

        canvas.drawRect(getLeft(),0,getRight(),getHeight(),rectPaint);
    }
}

