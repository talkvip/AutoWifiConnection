
package org.emsg.wifiauto;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

public class StartupIntentReceiver extends BroadcastReceiver {
    String ACTION_BOOT = "android.intent.action.BOOT_COMPLETED";// 开机自启动
    String WIFI_CHANGE = "android.net.wifi.WIFI_STATE_CHANGED";
    private String TAG = "定制WIF-Receiver";

    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, intent.getAction());
        // Wifi设置改变系统发送的广播
        if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
            int wifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
            Log.d(TAG, "wifiState" + wifiState);
            switch (wifiState) {
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d(TAG, "监听wifi打开");
                    Intent intent2 = new Intent();
                    intent.setAction("com.emsg.wificonnect.service.CHECKWIFI");
                    context.startService(intent2);
                    Log.d(TAG, "监听wifi状态service已启动");
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    // UtilsDebug.Log("StartupIntentReceiver", "监听wifi关闭");
                    // UtilsDebug.Log("StartupIntentReceiver",
                    // "监听wifi状态service 已关闭");
                    // Intent intent3 = new Intent();
                    // intent.setAction("com.zby.service.CHECKWIFI");
                    // context.stopService(intent3);
                    break;
            }
        } else if (WIFI_CHANGE.equals(intent
                .getAction())) {// wifi切换监听
        } else if (ACTION_BOOT.equals(intent
                .getAction())) {
            // 开机开启服务
            Intent intent2 = new Intent();
            intent.setAction("com.emsg.wificonnect.service.CHECKWIFI");
            context.startService(intent2);
        }
    }
}
