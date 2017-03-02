package com.ioi.lib.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Movie;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;


import com.ioi.tool.R;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by henhuang on 4/15/16.
 */
public class GifView extends View {

    private Movie gifMovie;
    private int movieWidth, movieHeight;
    private long mMovieStart;

    public GifView(Context context) {
        super(context);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public GifView(Context context, AttributeSet attrs,
                   int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);

        String resourceName = context.getString(R.string.waiting_resource);
        if (!TextUtils.isEmpty(resourceName)) {
            try {
                Log.d("GifView", "[init]");
                InputStream gifInputStream = context.getAssets().open(resourceName);

                gifMovie = Movie.decodeStream(gifInputStream);
                movieWidth = gifMovie.width();
                movieHeight = gifMovie.height();
//                long movieDuration = gifMovie.duration();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec,
                             int heightMeasureSpec) {
        setMeasuredDimension(movieWidth, movieHeight);
    }

//    public int getMovieWidth(){
//        return movieWidth;
//    }
//
//    public int getMovieHeight(){
//        return movieHeight;
//    }
//
//    public long getMovieDuration(){
//        return movieDuration;
//    }

    @Override
    protected void onDraw(Canvas canvas) {

        long now = android.os.SystemClock.uptimeMillis();
        if (mMovieStart == 0) {   // first time
            mMovieStart = now;
        }

        if (gifMovie != null) {

            int dur = gifMovie.duration();
            if (dur == 0) {
                dur = 1000;
            }

            int relTime = (int)((now - mMovieStart) % dur);

            gifMovie.setTime(relTime);

            gifMovie.draw(canvas, 0, 0);
            invalidate();

        }

    }

}

