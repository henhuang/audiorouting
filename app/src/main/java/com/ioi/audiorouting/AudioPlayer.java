package com.ioi.audiorouting;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;

import java.io.IOException;

/**
 * Created by henhuang on 2/10/17.
 */
public class AudioPlayer {
    final static String TAG = "[AudioPlayer]";

    Context context;
    MediaPlayer mediaPlayer;

    public AudioPlayer(Context context) {
        this.context = context;
    }

    private void init() {
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
    }

    public void release() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    public void setLooping(boolean looping) {
        mediaPlayer.setLooping(looping);
    }

    public void setTrack(String assetFile) {
        try {
            if (mediaPlayer == null)
                init();
            AssetFileDescriptor file = context.getAssets().openFd(assetFile);
            mediaPlayer.setDataSource(file.getFileDescriptor());
            mediaPlayer.prepare();
            file.close();
        } catch (IOException e) {
            Log.e(TAG, "[start] ERROR: " + e.getMessage());
        }
    }

    public void start(String assetFile) {
        setTrack(assetFile);

        mediaPlayer.start();
    }

    public void stop() {
        //mediaPlayer.reset();
        release();
    }

    public void play() {
        mediaPlayer.start();
    }

    public void pause() {
        mediaPlayer.pause();
    }
}
