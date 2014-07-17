package com.baidu.chinajoy;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2014/7/17.
 */
public class WifiSetting{
    private static final String WIFI_NAME = "Bullet";
    private static final int TYPE_WEP = 0;
    private static final int TYPE_WPA = 1;

    private Context mContext;
    private final WifiManager mWifiManager;
    private Wifi wifi;

    private Handler mHandler = new Handler();

    public WifiSetting(Context context) {
        this.mContext = context;
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        wifi = new Wifi("Baiyi_Mobile","vivaour100c",TYPE_WPA);
    }

    public void removeWifis() {

        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        Log.e(Utils.TAG, "removeing Wifis");
        for (WifiConfiguration wifiConfiguration: configuredNetworks) {
            String ssid = wifiConfiguration.SSID;
            Log.e(Utils.TAG,"ssid = " + ssid);
            mWifiManager.removeNetwork(wifiConfiguration.networkId);
        }
    }

    public void apply() {
        Log.e(Utils.TAG,"apply");
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                if (mWifiManager.isWifiEnabled()) {
                    Log.e(Utils.TAG,"line ...");
                    if (!openOurWifi()) {
                        mHandler.postDelayed(this,1000);
                    }
                } else {
                    Log.e(Utils.TAG,"opening ...");
                    mWifiManager.setWifiEnabled(true);
                    mHandler.postDelayed(this,500);
                }
            }
        });
    }

    private boolean openOurWifi() {
        removeWifis();
        boolean result;
        WifiConfiguration wifiConfig = isExist(wifi.getSsid());
        if (wifiConfig != null) {
            Log.e(Utils.TAG, "exist wifiConfig : " + wifiConfig.toString());
            mWifiManager.disableNetwork(wifiConfig.networkId);
            mWifiManager.removeNetwork(wifiConfig.networkId);
        }
        wifiConfig = createWifiInfo(wifi.getSsid(),wifi.getPassword(),wifi.getType());
        int netId = mWifiManager.addNetwork(wifiConfig);
        result = mWifiManager.enableNetwork(netId, true);
        Log.e(Utils.TAG, "add netId : " + netId + ", result : " + result);
        return result;
    }

    private WifiConfiguration isExist(String SSID) {
        SSID = "\"" + SSID + "\"";
        List<WifiConfiguration> existingConfigs = mWifiManager.getConfiguredNetworks();
        if (existingConfigs == null) {
            return null;
        }
        for (WifiConfiguration existingConfig : existingConfigs) {
            Log.e(Utils.TAG,"existingConfig.SSID : " + existingConfig.SSID + ", SSID : " + SSID);
            if (TextUtils.equals(existingConfig.SSID, SSID)) {
                Log.e(Utils.TAG, "find same SSID");
                return existingConfig;
            }
        }
        return null;
    }

    public WifiConfiguration createWifiInfo(String SSID, String password, int type) {

        Log.v(Utils.TAG, "SSID = " + SSID + "## Password = " + password + "## Type = " + type);

        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = isExist(SSID);
        if (tempConfig != null) {
            mWifiManager.removeNetwork(tempConfig.networkId);
        }

        // ·ÖÎªÈýÖÖÇé¿ö£º1Ã»ÓÐÃÜÂë2ÓÃwepŒÓÃÜ3ÓÃwpaŒÓÃÜ
/*        if (type == TYPE_NO_PASSWD) {// WIFICIPHER_NOPASS
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;

        } else */if (type == TYPE_WEP) {  //  WIFICIPHER_WEP
            config.wepKeys[0] = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        } else if (type == TYPE_WPA) {   // WIFICIPHER_WPA
            config.preSharedKey = "\"" + password + "\"";
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }

        return config;
    }

    public Wifi getWifi() {
        if (wifi == null) {
            wifi = new Wifi("Baiyi_Mobile","vivaour100c",TYPE_WPA);
        }
        return wifi;
    }

    public static class Wifi{
        private String ssid;
        private String password;
        private int type;

        public Wifi(String ssid, String password, int type) {
            this.ssid = ssid;
            this.password = password;
            this.type = type;
        }

        public String getSsid() {
            return ssid;
        }

        public void setSsid(String ssid) {
            this.ssid = ssid;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Wifi{" +
                    "ssid='" + ssid + '\'' +
                    ", password='" + password + '\'' +
                    ", type=" + type +
                    '}';
        }
    }
}
