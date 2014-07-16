package com.baidu.chinajoy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

/**
 * Created by yangmengrong on 14-7-15.
 */
public class NetStateReceiver extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Log.e(Utils.TAG,"networkInfo = " + networkInfo.getExtraInfo());
        boolean isOurNet = networkInfo.getExtraInfo().equals("tp-camera-test");
        Log.e(Utils.TAG,"isOurNet = " + isOurNet);
        if (networkInfo.isAvailable()) {
            Config configFromLocal = Utils.getConfigFromLocal(context);
            if (configFromLocal != null) {
                Intent service = new Intent(context, SocketService.class);
                service.putExtra(SocketService.ISFROM_RECEIVER,true);
                context.getApplicationContext().startService(service);
            }
        }
       /* NetworkInfo mobileInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo activeInfo = manager.getActiveNetworkInfo();
        Toast.makeText(context, "mobile:" + mobileInfo.isConnected() + "\n" + "wifi:" + wifiInfo.isConnected()
                + "\n" + "active:" + activeInfo.getTypeName(), Toast.LENGTH_LONG).show();*/
    }
}
