package com.ioi.audiorouting;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by henhuang on 2/9/17.
 */
public class HFPActivity extends Activity implements ServiceConnection,
        BluetoothXXXStateReceiver.OnBluetoothXXXStateListener {
    final static String TAG = "[CommLink][HFPActivity]";

    private TextView bluetoothState;
    private TextView aclConnection;
    private TextView hfpConnection;
    private TextView hfpAudioState;
    private TextView a2dpConnection;
    private TextView a2dpAudioState;

    private EditText duration;
    private EditText repeat;

    HFPService hfpService;

    BluetoothXXXStateReceiver bluetoothXXXStateReceiver;

    AudioPlayer audioPlayer;

    static void launch(Context context) {
        Intent intent = new Intent(context, HFPActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_hfp);

        audioPlayer = new AudioPlayer(this);

        bluetoothState = (TextView) findViewById(R.id.bluetoothState);
        //aclConnection = (TextView) findViewById(R.id.aclConnection);
        hfpConnection = (TextView) findViewById(R.id.hfpConnection);
        hfpAudioState = (TextView) findViewById(R.id.hfpAudioState);
        a2dpConnection = (TextView) findViewById(R.id.a2dpConnection);
        a2dpAudioState = (TextView) findViewById(R.id.a2dpAudioState);

        duration = (EditText) findViewById(R.id.duration);
        repeat = (EditText) findViewById(R.id.repeat);

        bluetoothXXXStateReceiver = new BluetoothXXXStateReceiver(this);
        bluetoothXXXStateReceiver.setOnBluetoothXXXStateListener(this);

        HFPService.startService(this); // keep service alive even if unbind
    }

    @Override
    public void onResume() {
        super.onResume();
        HFPService.bindService(this, this);
        bluetoothXXXStateReceiver.register();
    }

    @Override
    public void onPause() {
        super.onPause();
        HFPService.unbindService(this, this);
        bluetoothXXXStateReceiver.unregister();
    }

    void updateViewBluetoothState(final boolean on) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                bluetoothState.setText(on ? "on" : "off");
            }
        });
    }

    void updateViewA2dpState(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                a2dpConnection.setText(connected ? "connected" : "disconnected");
            }
        });
    }

    void updateViewA2dpAudioState(final boolean playing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                a2dpAudioState.setText(playing ? "playing" : "stopped");
            }
        });
    }

    void updateViewHfpState(final boolean connected) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hfpConnection.setText(connected ? "connected" : "disconnected");
            }
        });
    }

    void updateViewHfpAudioState(final boolean playing) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                hfpAudioState.setText(playing ? "playing" : "stopped");
            }
        });
    }

    ////////
    //////// implements ServiceConnection ////////
    @Override
    public void onServiceConnected(ComponentName name, IBinder iBinder) {
        Log.d(TAG, "[onServiceConnected]");

        // init states for view
        hfpService = ((HFPServiceBinder) iBinder).getService();
        updateViewBluetoothState(hfpService.isBluetoothOn());
        updateViewA2dpState(hfpService.isA2dpConnected());
        updateViewA2dpAudioState(hfpService.isA2dpAudioPlaying());
        updateViewHfpState(hfpService.isHfpConnected());
        updateViewHfpAudioState(hfpService.isHfpAudioPlaying());
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        Log.d(TAG, "[onServiceDisconnected]");
        hfpService = null;
    }

    //////// implements BluetoothXXXStateReceiver.OnBluetoothXXXStateListener

    @Override
    public void onBluetoothStateChanged(final boolean on) {
        updateViewBluetoothState(on);
    }

    @Override
    public void onBluetoothAclStateChanged(final boolean connected) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                aclConnection.setText(connected ? "connected" : "disconnected");
//            }
//        });
    }

    @Override
    public void onBluetoothA2dpServiceStateChanged(final boolean connected) {
        // Do NOTHING
    }

    @Override
    public void onBluetoothA2dpStateChanged(final boolean connected) {
        updateViewA2dpState(connected);
    }

    @Override
    public void onBluetoothA2dpAudioStateChanged(final boolean playing) {
        updateViewA2dpAudioState(playing);
    }


    @Override
    public void onBluetoothHfpServiceStateChanged(final boolean connected) {
        // Do NOTHING
    }

    @Override
    public void onBluetoothHfpStateChanged(final boolean connected) {
        updateViewHfpState(connected);
    }

    @Override
    public void onBluetoothHfpAudioStateChanged(final boolean playing) {
        updateViewHfpAudioState(playing);
    }

    //////////////////////////////////////////////////////////////

    public void onButtonMakeCall(View view) {
        HFPMan.makeCall(this, "0800080123");
    }

    public void onButtonEndCall(View view) {
        HFPMan.endCall(this);
    }

    public void onButtonRouteMedia2Speaker(View view) {
        HFPMan.routeMedia2Speaker(this);
    }

    public void onButtonRouteMedia2A2dp(View view) {
        HFPMan.routeMedia2A2dp(this);
    }

    public void onButtonRoutePhoneCall2Speaker(View view) {
        HFPMan.routePhoneCall2Speaker(this);
    }

    public void onButtonRoutePhoneCall2Hfp(View view) {
        HFPMan.routePhoneCall2HFP(this);
    }

    public void onButtonPlayTrack(View view) {
        audioPlayer.start("max315.mp3");
    }

    public void onButtonPauseTrack(View view) {
        audioPlayer.stop();
    }

    public void onButtonStartTest(View view) {
        Intent intent = new Intent(this, HFPService.class);
        intent.setAction(HFPService.ACTION_TEST_START);
        intent.putExtra(HFPService.PARAM_REPEAT, Integer.valueOf(repeat.getText().toString()));
        intent.putExtra(HFPService.PARAM_DURATION, Integer.valueOf(duration.getText().toString()));
        startService(intent);
    }

    public void onButtonStopTest(View view) {
        Intent intent = new Intent(this, HFPService.class);
        intent.setAction(HFPService.ACTION_TEST_STOP);
        startService(intent);
    }
}
