package com.baidu.chinajoy;

import android.app.KeyguardManager;
import android.app.Service;
import android.content.*;
import android.os.*;
import android.util.Log;
import android.widget.Toast;
import com.baidu.camera.chinajoy.MessageReceiver;
import org.java_websocket.drafts.Draft_10;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by yangmengrong on 14-7-11.
 */
public class SocketService extends Service implements SocketClient.MessageListener {

    public static final String DO_ONRECEIVE = "do.onreceive";
    public static final String MESSAGE = "message";
    public static final String CHINAJOY_SERVICE = "chinajoy.service";
    public static final String ISFROM_RECEIVER = "isfrom_receiver";
    private Binder mBinder = new LocalBinder();

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private MessageReceiver mCameraService;
    private boolean mBound;
    ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // TODO Auto-generated method stub
            mCameraService = null;
            Toast.makeText(getApplicationContext(), "no", Toast.LENGTH_SHORT).show();
            Log.d("IRemote", "Binding - Service disconnected");
            mBound = true;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service)
        {
            // TODO Auto-generated method stub
            mCameraService = MessageReceiver.Stub.asInterface((IBinder) service);
            try {
                mCameraService.setDeviceNumber(mConfig.getNumber());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(), "yes", Toast.LENGTH_SHORT).show();
            Log.d("IRemote", "Binding is done - Service connected");
            mBound = false;
        }
    };
    private Config mConfig;
    private SocketClient mSocketClient;
    private PowerManager mPowerManager;
    private KeyguardManager mKeyguardManager;

    @Override
    public void onCreate() {
        super.onCreate();
        mPowerManager = (PowerManager)getSystemService(POWER_SERVICE);
        mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            boolean isFromReceiver = intent.getBooleanExtra(ISFROM_RECEIVER, false);
            if (isFromReceiver) {
                setConfig(Utils.getConfigFromLocal(this));
            }
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onReceive(String message) {
        try {
            Log.e("", "yangmengrong " + (message.startsWith("start") || message.startsWith("stop")));
            if (message.startsWith("start") || message.startsWith("stop")) {
                doStartOrStop(message);
            } else if (message.equals("wake")) {
                unlockScreen(mPowerManager,mKeyguardManager);
            } else {
                mCameraService.onReceive(message);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void doStartOrStop(final String message) {
        final String[] split = message.split("_");
        long timeDelay = Long.parseLong(split[split.length-1]) - System.currentTimeMillis();
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.e(Utils.TAG,"run delay start");
                try {
                    mCameraService.onReceive(split[0]);
                    Log.e(Utils.TAG,"run delay end");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        },timeDelay);
    }

    public class LocalBinder extends Binder {
        public SocketService getService() {
            return SocketService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public void setConfig(Config config) {
        this.mConfig = config;
        try {
            if (mSocketClient != null) {
                mSocketClient.close();
            }
            mSocketClient = new SocketClient(new URI( "ws://"+ config.getIp() +":" + config.getPort() ), new Draft_10());
            mSocketClient.setMessageListener(this);

            Intent intent = new Intent(CHINAJOY_SERVICE);
            bindService(intent, mServiceConnection, BIND_AUTO_CREATE);

            mSocketClient.connect();
        } catch (URISyntaxException e) {
            e.printStackTrace();
            Log.i(Utils.TAG,"URISyntaxException:" + e.toString());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mBound) {
            unbindService(mServiceConnection);
        }
    }

    private void unlockScreen(PowerManager pm, KeyguardManager km) {
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unlock");
        kl.disableKeyguard();
        PowerManager.WakeLock wl = pm.newWakeLock(
                PowerManager.ACQUIRE_CAUSES_WAKEUP
                        |PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        wl.acquire();
        wl.release();
    }
}
