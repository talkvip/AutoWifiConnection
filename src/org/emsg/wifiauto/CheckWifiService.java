
package org.emsg.wifiauto;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.emsg.wificonnect.R;

import org.emsg.util.Preferences;
import org.emsg.util.WifiAdmin;
import org.emsg.util.WifiAdmin.WifiCipherType;
import org.emsg.wifiauto.WifiHistoryActivity.MyWifiHistoryAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * wifi 检测服务
 */
public class CheckWifiService extends Service {

    protected static final String TAG = CheckWifiService.class.getSimpleName();
    public static final String BROAD_ACTION_WIFI_DISENABLE = "com.emsg.wificonnect.wifi_disenable";// wifi不可用
    int maxtime = 10;//
    private boolean quit = false;
    private WifiManager wifiManager;
    private int ipAddress;
    HashMap<String, String> parmap;
    private MyNetWorkBrodCast mNetWorkBrodCast;
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

   /* @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        new Thread() {
            @Override
            public void run() {
                // Looper.prepare();
                while (!quit) {
                    try {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        ipAddress = wifiInfo == null ? 0 : wifiInfo
                                .getIpAddress();
                        wifiInfo.getBSSID();
                        String ssid = "";
                        // String ca = "";
                        if (wifiInfo.getSSID() != null
                                && wifiInfo.getSSID().length() > 0) {
                            ssid = wifiInfo.getSSID().replace("\"", "");
                        } else {
                        }
                        if (wifiManager.isWifiEnabled() && ipAddress != 0
                                && ssid.length() > 3
                                && (ssid.lastIndexOf(Constants.SSID) != -1
                                || ssid.lastIndexOf(Constants.SSID2) != -1)) {// wifi
                        } else {// wifi 断开 提示
                            seachWifi();
                        }
                        Thread.sleep(1000 * maxtime); // 每10秒检查一次
                    } catch (InterruptedException e) {
                    }
                }
            }
        }.start();
        return super.onStartCommand(intent, flags, startId);
    }*/

    ProgressDialog mProgressDialog;
    WifiAdmin mWifiAdmin ;
    @Override
    public void onCreate() {
        super.onCreate();
        wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        mWifiAdmin =  MyApplication.getInstance().getWifiAdmin();
        mNetWorkBrodCast = new MyNetWorkBrodCast();
       
    }
    
    private void onSearchWifi(){
        mWifiAdmin.openWifi();
        mWifiAdmin.startScan();
        showProgress();
        searchWifi = new SearchWifi();
        searchWifi.start();
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try{
            dissmissProgress();
            onSearchWifi(); 
        }catch(Exception e){
            dissmissProgress();
        }
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mNetWorkBrodCast, mIntentFilter);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showProgress() {
        try{
            mProgressDialog = ProgressDialog.show(BaseActivity.mActivityStack.get(0),
                    getString(R.string.app_name), "正在查询附近热点...");
        }catch(Exception e){
            
        }
     
    }
    private ScanResult wifiScan;
    private Thread searchWifi;
    private List<ScanResult> allscan;

    @Override
    public void onDestroy() {
        super.onDestroy();
        quit = true;
        unregisterReceiver(mNetWorkBrodCast);
    }

    class SearchWifi extends Thread {
        private boolean haveWifi = true;
        private long limit;
        long start = System.currentTimeMillis();

        @Override
        public void run() {
            while (haveWifi) {
                try {
                    allscan = wifiManager.getScanResults();
                    if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
                            && wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
                        limit = System.currentTimeMillis() - start;
                        if (limit > 8 * 1000) {
                            haveWifi = false;
                            myHandler.sendEmptyMessage(1);
                            break;
                        }
                        if (allscan != null) {
                            if (authHaveWifi()) {
                                haveWifi = false;
                                checkConnectWifi();
                            }
                        }
                    }
                    Thread.sleep(500);
                } catch (InterruptedException e) {//
                }
            }
        }
    }

    private void dissmissProgress() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            dissmissProgress();
            Context context = CheckWifiService.this;
            if (msg.what == 0) {
                String username = Preferences.getString(context, "username");
                if (username == null) {
                    Toast.makeText(context, "已连接上热点,请认证。。。", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(context, "已经为您连上免费热点。", Toast.LENGTH_SHORT).show();
                }
            } else if (msg.what == 1) {
                Toast.makeText(context, "未搜索到热点", Toast.LENGTH_SHORT).show();
                intentVoWifiList();
            }
        }
    };

    private void intentVoWifiList() {
        Intent intent = new Intent(CheckWifiService.this, WifiListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        try{
            BaseActivity.mActivityStack.get(0).finish();
        }catch(Exception e){
            
        }
       
    }

    void seachWifi() {
        openWifi();
        if (allscan != null) {
            if (authHaveWifi()) {// 有定制wifi
                checkConnectWifi();
            } else {
            }
        }
    }

    protected void checkConnectWifi() {
        if (searchWifi != null)
            searchWifi.interrupt();// 搜索线程关闭
        if (wifiScan.BSSID.equals(wifiManager.getConnectionInfo().getBSSID())) {// mac相同
        } else {// 未连wifi接
            MyApplication.getInstance().getWifiAdmin().connect(wifiScan.SSID, "", WifiCipherType.WIFICIPHER_NOPASS, wifiScan.BSSID);
        }
    }
    
    class MyNetWorkBrodCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                boolean isWifiConn = MyApplication.getInstance().getWifiAdmin()
                        .isWifiConnected(context);
                if (isWifiConn) {
                    String curBssid = mWifiAdmin.getWifiInfo().getBSSID();
                    String ssid = mWifiAdmin.getWifiInfo().getSSID();
                    if(wifiScan!=null){
                        if(ssid.equals(wifiScan.SSID)&&curBssid.equals(wifiScan.BSSID)){
                            myHandler.sendEmptyMessage(0);
                        }
                    }
                }
            }
        }
    }

    protected boolean authHaveWifi() {
        ArrayList<ScanResult> slist = new ArrayList<ScanResult>();
        if (allscan.size() > 0) {
            Iterator<ScanResult> list = allscan.iterator();
            while (list.hasNext()) {
                ScanResult scan = list.next();
                String ssid = scan.SSID;
                if (ssid.length() >= 3) {
                    if (ssid.lastIndexOf(Constants.SSID) != -1
                            || ssid.lastIndexOf(Constants.SSID2) != -1) {
                        slist.add(scan);
                        Log.d(TAG, "搜索到的ssid" + scan.SSID);
                    }
                }
            }
        }
        Log.d(TAG, "搜索到的定制wifi数:" + slist.size());
        double olevle = -1110.00;
        for (int i = 0; i < slist.size(); i++) {
            double nlevel = slist.get(i).level;
            olevle = olevle > nlevel ? olevle : nlevel;
            wifiScan = olevle > nlevel ? wifiScan : slist.get(i);
        }
        if (wifiScan != null) {
            return true;
        }
        return false;
    }

    private void openWifi() {
        if (wifiManager != null
                && wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
            wifiManager.startScan();
            allscan = wifiManager.getScanResults();
        }
    }

}
