package com.baidu.chinajoy;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.os.RemoteException;
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

    private Binder mBinder = new LocalBinder();

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

    @Override
    public void onReceive(String message) {
        try {
            mCameraService.onReceive(message);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
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
            Log.i(Utils.TAG,"setConfig:1");
            if (mSocketClient != null) {
                mSocketClient.close();
            }
            mSocketClient = new SocketClient(new URI( "ws://"+ config.getIp() +":" + config.getPort() ), new Draft_10());
            mSocketClient.setMessageListener(this);
            Log.i(Utils.TAG,"setConfig:2");

            Intent intent = new Intent("chinajoy.service");
            bindService(intent,mServiceConnection,BIND_AUTO_CREATE);
            Log.i(Utils.TAG,"setConfig:3");

            mSocketClient.connect();
            Log.i(Utils.TAG,"setConfig:4");
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
}
