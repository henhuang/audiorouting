package com.ioi.lib.view;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by henhuang on 2/7/17.
 */
public class FrameAnimation {
    final static String TAG = "[FrameAnimation]";

    private Context context;
    private ImageView view;
    private int[] frameResource;
    private int repeat;
    private int duration; // milliseconds
    private int repeatIndex;
    private int frameIndex;
    private Handler handler = new Handler();


    public FrameAnimation(Context context, ImageView view, int[] framesResource, int duration, int repeat) {
        this.context = context;
        this.view = view;
        this.frameResource = framesResource;
        this.duration = duration;
        this.repeat = repeat;
    }

    public int getDuration() {
        return duration;
    }

    public int getRepeat() {
        return repeat;
    }

    private synchronized void reset() {
        frameIndex = 0;
        repeatIndex = 0;
    }

    public void restart() {
        stop();
        play();
    }

    public void start() {
        play();
    }

    public void play() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final int index = frameIndex;
                final int rindex = repeatIndex;

                if (Build.VERSION.SDK_INT > 21) {
                    view.setImageDrawable(context.getResources().getDrawable(frameResource[index], context.getTheme()));
                }
                else {
                    view.setImageDrawable(context.getResources().getDrawable(frameResource[index]));
                }

                synchronized (FrameAnimation.this) {
                    frameIndex++;
                    if (frameIndex == frameResource.length) {
                        frameIndex = 0;
                        repeatIndex++;
                    }
                }

                if (rindex < repeat)
                    play();
                else {
                    Log.d(TAG, "animation DONE....");
                }

            }
        }, duration);
    }

    public void stop() {
        handler.removeCallbacksAndMessages(null);
        reset();
    }
}
