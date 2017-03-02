package com.ioi.audiorouting;

import android.content.Context;

/**
 * Created by henhuang on 2/10/17.
 */
public class AudioSystemAvator {

    public static final int ERROR = 0xFFFFFFFF;
    public static final String DEVICE_OUT_SPEAKER          = "DEVICE_OUT_SPEAKER";
    public static final String DEVICE_OUT_WIRED_HEADPHONE  = "DEVICE_OUT_WIRED_HEADPHONE";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER = "DEVICE_OUT_BLUETOOTH_A2DP_SPEAKER";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP = "DEVICE_OUT_BLUETOOTH_A2DP";
    public static final String DEVICE_OUT_BLUETOOTH_A2DP_HEADPHONES = "DEVICE_OUT_WIRED_HEADPHONE";
    public static final String DEVICE_OUT_BLUETOOTH_SCO = "DEVICE_OUT_BLUETOOTH_SCO";
    public static final String DEVICE_OUT_BLUETOOTH_SCO_HEADSET = "DEVICE_OUT_BLUETOOTH_SCO_HEADSET";

    // device states, must match AudioSystem::device_connection_state
    public static final int DEVICE_STATE_UNAVAILABLE = 0;
    public static final int DEVICE_STATE_AVAILABLE = 1;

    Class<?> audioSystem;

    private Context context;

    AudioSystemAvator(Context context) {
        this.context = context;

        try {
            audioSystem = Class.forName("android.media.AudioSystem");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    private int setDeviceConnectionState(int i, int j, String s) {
        try {
            return (Integer) audioSystem.getMethod("setDeviceConnectionState",
                    int.class, int.class, String.class).invoke(audioSystem, i, j, s);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    private int getConstantValue(String s) {
        try {
            //return ((Integer)audioSystem.getDeclaredField(s).get(int.class)).intValue();
            return (Integer)audioSystem.getDeclaredField(s).get(int.class);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return ERROR;
    }

    public int setDeviceConnectionState(String deviceName, Boolean state) {
        return setDeviceConnectionState(getConstantValue(deviceName),
                state ? DEVICE_STATE_AVAILABLE : DEVICE_STATE_UNAVAILABLE, "");
    }
}
