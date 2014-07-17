package com.baidu.chinajoy;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class MyActivity extends Activity implements View.OnClickListener {

    private EditText etPort;
    private EditText etIp;
    private EditText etNumber;

    private Config mConfig;
    private boolean mBound;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.i(Utils.TAG,"onServiceConnected");
            SocketService.LocalBinder binder = (SocketService.LocalBinder) service;
            mService = binder.getService();
            mService.setConfig(mConfig);
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.i(Utils.TAG,"onServiceDisconnected");
            mBound = false;
        }
    };
    private SocketService mService;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        etPort = (EditText) findViewById(R.id.et_port);
        etIp = (EditText) findViewById(R.id.et_ip);
        etNumber = (EditText) findViewById(R.id.et_number);
        findViewById(R.id.bt_sure).setOnClickListener(this);
        WifiSetting.removeWifis(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateTexts();
    }

    private void updateTexts() {
        Config configFromLocal = Utils.getConfigFromLocal(this);
        if (configFromLocal != null) {
            etIp.setText(configFromLocal.getIp());
            etPort.setText(configFromLocal.getPort()+"");
            etNumber.setText(configFromLocal.getNumber());
        }
    }

    @Override
    public void onClick(View v) {
        initConfig();
        if (mService == null) {
            bindSocketService();
        } else {
            mService.setConfig(mConfig);
        }
    }

    private void bindSocketService() {
        Intent serviceIntent = new Intent(this,SocketService.class);
        boolean state = this.getApplicationContext().bindService(serviceIntent, mConnection, BIND_AUTO_CREATE);
        Log.i(Utils.TAG,"state = " + state);
    }

    private void initConfig() {
        Integer port = Integer.parseInt(etPort.getText().toString());
        String ip = etIp.getText().toString();
        String number = etNumber.getText().toString();
        Log.e(Utils.TAG, "port = " + etPort.getText().toString() + " ip = " + ip + " number = " + number);
        mConfig = new Config(ip, port, number);
        Utils.saveConfig(this, mConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBound) {
            getApplicationContext().unbindService(mConnection);
        }
    }
}
