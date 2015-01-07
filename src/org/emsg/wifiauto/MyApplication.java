
package org.emsg.wifiauto;

import android.app.Application;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.sromku.simple.storage.SimpleStorage;
import com.sromku.simple.storage.Storage;

import org.emsg.util.WifiAdmin;
import org.emsg.wifiauto.WifiKeepService.MyBinder;
import org.emsg.wifiauto.db.DbManager;
import org.emsg.wifiauto.sambatask.SambaTaskCenter;

public class MyApplication extends Application {
    private static MyApplication mApplication;

    private WifiAdmin mWifiAdmin;

    public SambaTaskCenter mSambaTaskCenter;

    MyServiceConnection mServiceConnection;
    Storage mStorage = null;

    public boolean isClosed = false;
    private DbManager mDbManager;

    public static MyApplication getInstance() {
        return mApplication;
    }

    public WifiAdmin getWifiAdmin() {
        return mWifiAdmin;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApplication = this;

        wifiAdminInit();
        mSambaCenterInit();

        mServiceConnection = new MyServiceConnection();
        // bindWifiKeepService();
        appDbManagerInit();
        storageManagerInit();

    }
 
    private void appDbManagerInit(){
        mDbManager = new DbManager(this);
    }
    private void storageManagerInit() {
        if (SimpleStorage.isExternalStorageWritable()) {
            mStorage = SimpleStorage.getExternalStorage();
        } else {
            mStorage = SimpleStorage.getInternalStorage(this);
        }
        mStorage.createDirectory(Constants.APP_MAIN_DIRECTORY, false);
    }

    private void mSambaCenterInit() {
        mSambaTaskCenter = new SambaTaskCenter();
    }

    private void wifiAdminInit() {
        mWifiAdmin = new WifiAdmin(this);
        mWifiAdmin.createWifiLock();
        mWifiAdmin.closeWifi();
        mWifiAdmin.openWifi();
    }

    public Storage getAppStorage() {
        return mStorage;
    }

    public DbManager getDbManager() {
        return mDbManager;
    }

    private void bindWifiKeepService() {
        Intent intent = new Intent(this, WifiKeepService.class);
        startService(intent);
    }

    class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceDisconnected(ComponentName componentname) {

        }
        @Override
        public void onServiceConnected(ComponentName componentname, IBinder ibinder) {
            MyBinder mBinder = (MyBinder) ibinder;
        }
    }

    private void stopWifiKeepService() {
        Intent intent = new Intent(this, CheckWifiService.class);
        stopService(intent);
    }

    public boolean isConEmgHotWifi() {
        String ssid = mWifiAdmin.getWifiInfo().getSSID();
        boolean isHotWifi = ssid.lastIndexOf(Constants.SSID) != -1
                || ssid.lastIndexOf(Constants.SSID2) != -1;
        return mWifiAdmin.isWifiConnected(this) && isHotWifi;
    }

    public void exitSystem() {
        mSambaTaskCenter.shutDown();
        stopWifiKeepService();
        stopDownLoadTaskService();
        System.exit(1);
    }
    
    private void stopDownLoadTaskService(){
        Intent intent = new Intent(this,SambaDownLoadService.class);
        stopService(intent);
    }
    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

}
