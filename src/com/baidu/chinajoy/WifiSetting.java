package com.baidu.chinajoy;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2014/7/17.
 */
public class WifiSetting{
    private static final String WIFI_NAME = "Bullet";

    public static void removeWifis(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        List<WifiConfiguration> configuredNetworks = wifiManager.getConfiguredNetworks();
        Log.e(Utils.TAG,"removeing Wifis");
        for (WifiConfiguration wifiConfiguration: configuredNetworks) {
            String ssid = wifiConfiguration.SSID;
            Log.e(Utils.TAG,"ssid = " + ssid);
            /*if (ssid != null && !ssid.startsWith(WIFI_NAME)){
                wifiManager.removeNetwork(wifiConfiguration.networkId);
                Log.e(Utils.TAG,"ssid = " + ssid + " has remove.");
            }*/
        }

    }
}
