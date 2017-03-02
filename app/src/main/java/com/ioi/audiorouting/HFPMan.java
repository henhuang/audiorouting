package com.ioi.audiorouting;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.Uri;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by henhuang on 2/9/17.
 */
public class HFPMan {
    // audio routing ONLY works on Android 5.x

    final static String TAG = "[HFPMan]";

    public static void makeCall(Context context, String phoneNumber) {
        try {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setData(Uri.parse("tel:" + phoneNumber));
            context.startActivity(intent);

        } catch (SecurityException e) {
            Log.e(TAG, "[makeCall] ERROR: need permission to make a call");
            Log.e(TAG, "[makeCall] ERROR: " + e.getMessage());
        }
    }
    public static void endCall(Context context) {
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        try {
            Method mGetITelephony = tm.getClass().getDeclaredMethod("getITelephony");
            mGetITelephony.setAccessible(true);
            Object iTelephony = mGetITelephony.invoke(tm);

            Method mEndCall = iTelephony.getClass().getDeclaredMethod("endCall");

            mEndCall.invoke(iTelephony);
        } catch (NoSuchMethodException e) {
            Log.e(TAG, "[endCall] ERROR: " + e.getMessage());

        } catch (InvocationTargetException e1) {
            Log.e(TAG, "[endCall] ERROR: " + e1.getMessage());

        } catch (IllegalAccessException e2) {
            Log.e(TAG, "[endCall] ERROR: " + e2.getMessage());
        }
    }

    public static void routeMedia2Speaker(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
//        audioManager.setBluetoothScoOn(false);
//        audioManager.stopBluetoothSco();


        AudioSystemAvator audioSystemAvator = new AudioSystemAvator(context);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_SPEAKER, true);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_WIRED_HEADPHONE, false);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER, false);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES, false);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP, false);
        audioSystemAvator.setDeviceConnectionState(AudioSystemAvator.DEVICE_OUT_SPEAKER, true);

        audioManager.setSpeakerphoneOn(true); // tricky hack to make route phone call to speaker work....
    }

    public static void routeMedia2A2dp(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
//        audioManager.setSpeakerphoneOn(false);
//        audioManager.setBluetoothScoOn(true);
//        audioManager.startBluetoothSco();

        AudioSystemAvator audioSystemAvator = new AudioSystemAvator(context);
        audioSystemAvator.setDeviceConnectionState(audioSystemAvator.DEVICE_OUT_WIRED_HEADPHONE, true);
        audioSystemAvator.setDeviceConnectionState(audioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER, true);
        audioSystemAvator.setDeviceConnectionState(audioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES, true);
        audioSystemAvator.setDeviceConnectionState(audioSystemAvator.DEVICE_OUT_BLUETOOTH_A2DP, true);
        audioSystemAvator.setDeviceConnectionState(audioSystemAvator.DEVICE_OUT_SPEAKER, false);
    }

    public static void routePhoneCall2Speaker(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.playSoundEffect(SoundEffectConstants.CLICK); // simulate user click
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setSpeakerphoneOn(true);
        audioManager.setBluetoothScoOn(false);
        audioManager.stopBluetoothSco();
    }

    public static void routePhoneCall2HFP(Context context) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        //audioManager.playSoundEffect(SoundEffectConstants.CLICK); // simulate user click
        audioManager.setMode(AudioManager.MODE_NORMAL);
        audioManager.setBluetoothScoOn(true);
        audioManager.startBluetoothSco();
        audioManager.setSpeakerphoneOn(false);
    }

}
