
package org.emsg.wifiauto;

import android.app.Service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.IBinder;

import org.emsg.util.Preferences;
import org.emsg.util.WifiAdmin.WifiCipherType;
import org.emsg.wifiauto.db.DbManager;
import org.emsg.wifiauto.db.WifiInfo;
import java.util.List;

public class WifiKeepService extends Service {
    MyNetBrodCastRevicer mNetBroadCastReciver;

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNetBroadCastReciver = new MyNetBrodCastRevicer();
        MyApplication.getInstance().getWifiAdmin().acquireWifiLock();
        registerBrodCastReciver();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isNeedToConn()) {
            autoConn();
        }
        if(MyApplication.getInstance().isClosed){
            return START_NOT_STICKY;
        }
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unRegisterBrodCastReciver();
        MyApplication.getInstance().getWifiAdmin().releaseWifiLock();
    }

    private void registerBrodCastReciver() {
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mNetBroadCastReciver, mIntentFilter);
    }

    private void unRegisterBrodCastReciver() {
        this.unregisterReceiver(mNetBroadCastReciver);
    }

    class MyNetBrodCastRevicer extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            context.startService(new Intent(context, WifiKeepService.class));
        }

    }

    private boolean isNeedToConn() {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()
                && netInfo.getType() == ConnectivityManager.TYPE_WIFI) {
            return false;
        } else {
            return true;
        }
    }

    public void autoConn() {
        String userName = Preferences.getString(this, "username");
        if (userName == null) {
            return;
        }
        MyApplication.getInstance().getWifiAdmin().openWifi();
        Thread mThread = new Thread(new MyRunableToConWifi());
        mThread.start();
    }

    class MyRunableToConWifi implements Runnable {
        @Override
        public void run() {
            connectLastWifi();
        }

        private void connectLastWifi() {
            synchronized (DbManager.class) {
                List<WifiInfo> mListData = MyApplication.getInstance().getDbManager()
                        .getUsedWifiInfo();
                if(mListData == null) return;
                for (WifiInfo mWifiInfo : mListData) {
                    boolean isConn = MyApplication
                            .getInstance()
                            .getWifiAdmin()
                            .connect(mWifiInfo.getSsid(), mWifiInfo.getPassword(),
                                    WifiCipherType.getWifitTypeFromString(mWifiInfo.getSsid()),mWifiInfo.getBssid());
                    if (isConn)
                        break;
                }
            }
        }
    }

    MyBinder mBinder = new MyBinder();

    public class MyBinder extends Binder {
        public WifiKeepService getService() {
            return WifiKeepService.this;
        }

    }

}
