package com.baidu.chinajoy;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Created by yangmengrong on 14-7-11.
 */
public class Utils {

    public static final String CHINAJOY = "chinajoy";
    public static final String IP = "ip";
    public static final String PORT = "port";
    public static final String NUMBER = "number";
    public static final String TAG = "yangmengrong";

    public static void saveConfig(Context context, Config config) {
        context.getSharedPreferences(CHINAJOY,Context.MODE_MULTI_PROCESS).edit()
                .putString(IP,config.getIp())
                .putInt(PORT,config.getPort())
                .putString(NUMBER,config.getNumber()).commit();
        Log.i(TAG,"saved config = " + config);
    }

    public static Config getConfigFromLocal(Context context) {
        SharedPreferences sp = context.getSharedPreferences(CHINAJOY, Context.MODE_MULTI_PROCESS);
        Config result = new Config(sp.getString(IP,""),sp.getInt(PORT,-1),sp.getString(NUMBER,""));
        return result;
    }
}
