package com.ioi.audiorouting;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;


/**
 * Created by henhuang on 2/9/17.
 */
public class HFPService extends Service {
    final static String TAG = "[HFPService]";

    final static String ACTION_TEST_START = "HFPService.ACTION_TEST_START";
    final static String ACTION_TEST_STOP = "HFPService.ACTION_TEST_STOP";
    final static String PARAM_REPEAT = "HFPService.PARAM_REPEAT";
    final static String PARAM_DURATION = "HFPService.PARAM_DURATION";

    HFPServiceBinder hfpServiceBinder;

    BluetoothAdapter bluetoothAdapter;
    TelephonyManager tm;

    BluetoothXXXManager bluetoothXXXManager;

    AudioPlayer audioPlayer;


    final static int MSG_MAKE_CALL = 0x0;
    final static int MSG_END_CALL = 0x1;
    final static int MSG_ROUTE_CALL_2_HFP = 0x2;
    final static int MSG_ROUTE_CALL_2_SPEAKER = 0x3;
    final static int MSG_PLAY_MEDIA = 0x4;
    final static int MSG_STOP_MEDIA = 0x5;
    final static int MSG_ROUTE_MEDIA_2_A2DP = 0x6;
    final static int MSG_ROUTE_MEDIA_2_SPEAKER = 0x7;

    interface OnTestFunctionFinishCallback {
        public void onFinish();
    }

    private int duration = 0;
    private int repeat = 0;
    HandlerThread handlerThread;
    Handler handler;

    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(int state, String incomingNumber) {
            // default implementation empty
            switch (state) {
                case TelephonyManager.CALL_STATE_IDLE:
                    Log.d(TAG, "call is inactive (CALL_STATE_IDLE)");
                    break;
                case TelephonyManager.CALL_STATE_RINGING:
                    Log.d(TAG, "call is ringing (CALL_STATE_RINGING)");
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    Log.d(TAG, "call is active (CALL_STATE_OFFHOOK)");
                    HFPActivity.launch(HFPService.this);
                    break;
            }
        }
    };



    @Override
    public void onCreate() {
        super.onCreate();

        Log.d(TAG, "[onCreate]");

        tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        tm.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);

        bluetoothXXXManager = new BluetoothXXXManager(this);

        audioPlayer = new AudioPlayer(this);

        // enable bluetooth
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled())
            bluetoothAdapter.enable();

        handlerThread = new HandlerThread("HFPService");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper(), new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                switch (msg.what) {
                    case MSG_MAKE_CALL: {
                        funcMakeCall("0800080123", duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_ROUTE_CALL_2_SPEAKER);
                            }
                        });

                        break;
                    }
                    case MSG_END_CALL:
                        funcEndCall(2000, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_PLAY_MEDIA);
                            }
                        });
                        break;
                    case MSG_ROUTE_CALL_2_HFP:
                        funcRoutePhoneCall2Hfp(duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_END_CALL);
                            }
                        });
                        break;
                    case MSG_ROUTE_CALL_2_SPEAKER:
                        funcRoutePhoneCall2Speaker(duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_ROUTE_CALL_2_HFP);
                            }
                        });
                        break;
                    case MSG_PLAY_MEDIA:
                        //funcRouteMedia2A2dp(0, null);
                        funcPlayMedia("max315.mp3", duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_ROUTE_MEDIA_2_SPEAKER);
                            }
                        });
                        break;
                    case MSG_STOP_MEDIA:
                        funcStopMedia(2000, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                repeat--;
                                if (repeat > 0)
                                    testStart();
                            }
                        });
                        break;
                    case MSG_ROUTE_MEDIA_2_A2DP:
                        funcRouteMedia2A2dp(duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_STOP_MEDIA);
                            }
                        });
                        break;
                    case MSG_ROUTE_MEDIA_2_SPEAKER:
                        funcRouteMedia2Speaker(duration, new OnTestFunctionFinishCallback() {
                            @Override
                            public void onFinish() {
                                handler.sendEmptyMessage(MSG_ROUTE_MEDIA_2_A2DP);
                            }
                        });
                        break;

                }
                return false;
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "[onDestroy]");
        handler.removeCallbacksAndMessages(null);
        handlerThread.quitSafely();

        bluetoothXXXManager.release();
        audioPlayer.release();
    }

    @Override
    public IBinder onBind(Intent intent) {
        if (hfpServiceBinder == null)
            hfpServiceBinder = new HFPServiceBinder(this);
        return hfpServiceBinder;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        final String action = intent.getAction();
        if (TextUtils.equals(action, ACTION_TEST_START)) {
            repeat = intent.getIntExtra(PARAM_REPEAT, 0);
            duration = intent.getIntExtra(PARAM_DURATION, 5) * 1000;
            Log.d(TAG, "ACTION_TEST_START: repeat= " + repeat + ", duration= " + duration);
            testStart();
        }
        else if (TextUtils.equals(action, ACTION_TEST_STOP)) {
            Log.d(TAG, "ACTION_TEST_STOP");
            repeat = 0;

            if (isHfpAudioPlaying())
                HFPMan.endCall(this);

            if (audioPlayer != null) {
                audioPlayer.release();
                audioPlayer = null;
            }

            testStop();
        }
        return START_NOT_STICKY;
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, HFPService.class);
        context.startService(intent);
    }
    /////////

    private void testStart() {
        Log.d(TAG, "testStart: =========== " + repeat + " ===========");
        handler.sendEmptyMessage(MSG_MAKE_CALL);
    }

    private void testStop() {
        Log.d(TAG, "testStop");
        handler.removeCallbacksAndMessages(null);
    }

    private void invokeOnDelayFinishCallback(final int duration, final OnTestFunctionFinishCallback callback) {
        if (callback == null)
            return;

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callback.onFinish();
            }
        }, duration);
    }

    private void funcMakeCall(final String phoneNumber, final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcMakeCall: " + phoneNumber);
        handler.post(new Runnable() {
            @Override
            public void run() {
                HFPMan.makeCall(HFPService.this, phoneNumber);
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    private void funcEndCall(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcEndCall");
        handler.post(new Runnable() {
            @Override
            public void run() {
                HFPMan.endCall(HFPService.this);
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    Handler mainHandler = new Handler();
    private void funcRoutePhoneCall2Speaker(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcRoutePhoneCall2Speaker");
        HFPMan.routePhoneCall2Speaker(HFPService.this);
        invokeOnDelayFinishCallback(duration, callback);
//        mainHandler.post(new Runnable() {
//            @Override
//            public void run() {
//
//
//            }
//        });

    }

    private void funcRoutePhoneCall2Hfp(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcRoutePhoneCall2Hfp");
        handler.post(new Runnable() {
            @Override
            public void run() {
                HFPMan.routePhoneCall2HFP(HFPService.this);
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    private void funcPlayMedia(final String assetFile, final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcPlayMedia");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (audioPlayer == null)
                    audioPlayer = new AudioPlayer(HFPService.this);
                audioPlayer.start(assetFile);
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    private void funcStopMedia(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcStopMedia");
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (audioPlayer != null)
                    audioPlayer.stop();
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    private void funcRouteMedia2Speaker(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcRouteMedia2Speaker");
        handler.post(new Runnable() {
            @Override
            public void run() {
                HFPMan.routeMedia2Speaker(HFPService.this);
            }
        });
        invokeOnDelayFinishCallback(duration, callback);
    }

    private void funcRouteMedia2A2dp(final int duration, final OnTestFunctionFinishCallback callback) {
        Log.d(TAG, "funcRouteMedia2A2dp");
        handler.post(new Runnable() {
            @Override
            public void run() {
                HFPMan.routeMedia2A2dp(HFPService.this);
            }
        });

        invokeOnDelayFinishCallback(duration, callback);
    }


    //////////


    public static void bindService(Context context, ServiceConnection serviceConnection) {
        context.bindService(new Intent(context, HFPService.class), serviceConnection, Context.BIND_AUTO_CREATE);
    }

    public static void unbindService(Context context, ServiceConnection serviceConnection) {
        context.unbindService(serviceConnection);
    }

    public boolean isBluetoothOn() {
        return bluetoothXXXManager.isBluetoothOn();
    }

    public boolean isA2dpServiceReady() {
        return bluetoothXXXManager.isA2dpServiceReady();
    }

    public boolean isHfpServiceReady() {
        return bluetoothXXXManager.isHfpServiceReady();
    }

    public boolean isA2dpConnected() {
        return bluetoothXXXManager.isA2dpConnected();
    }

    public boolean isHfpConnected() {
        return bluetoothXXXManager.isHfpConnected();
    }

    public boolean isA2dpAudioPlaying() {
        return bluetoothXXXManager.isA2dpAudioPlaying();
    }

    public boolean isHfpAudioPlaying() {
        return bluetoothXXXManager.isHfpAudioPlaying();
    }
}
