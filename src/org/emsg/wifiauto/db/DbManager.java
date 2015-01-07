
package org.emsg.wifiauto.db;

import android.content.Context;

import android.database.Cursor;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DbUpgradeListener;
import com.lidroid.xutils.exception.DbException;

import java.util.ArrayList;
import java.util.List;

public class DbManager {

    private DbUtils mWifiInfoDbUtil;

    private Context mcontext;

    public DbManager(Context context) {
        this.mcontext = context;
        getWifiInfoDb();
    }

    public DbUtils getWifiInfoDb() {
        mWifiInfoDbUtil = DbUtils.create(mcontext, "wifiInfo", 0, new DbUpgradeListener() {

            @Override
            public void onUpgrade(DbUtils db, int oldVersion, int newVersion) {
            }
        });
        return mWifiInfoDbUtil;
    }

    public synchronized void insertWifiInfo(String ssid, String password, String type, String bssid) {
        WifiInfo wifiInfo = new WifiInfo();
        wifiInfo.setPassword(password);
        wifiInfo.setWifiCipherType(type);
        wifiInfo.setSsid(ssid);
        wifiInfo.setTime(System.currentTimeMillis());
        wifiInfo.setBssid(bssid);
        try {
            mWifiInfoDbUtil.save(wifiInfo);
        } catch (DbException e) {
        }
    }

    public synchronized List<WifiInfo> getUsedWifiInfo() {
        List<WifiInfo> mListData = null;
        Cursor mCursor = null;
        try {
            mCursor = mWifiInfoDbUtil.execQuery("select * from wifiInfo order by time desc");
            mCursor.moveToPosition(-1);
            mListData = new ArrayList<WifiInfo>();
            while (mCursor.moveToNext()) {
                String type = mCursor.getString(4);
                String ssid = mCursor.getString(1);
                String bssid = mCursor.getString(2);
                String passw = mCursor.getString(5);
                WifiInfo wifiInfo = new WifiInfo();
                wifiInfo.setSsid(ssid);
                wifiInfo.setBssid(bssid);
                wifiInfo.setPassword(passw);
                wifiInfo.setWifiCipherType(type);
                mListData.add(wifiInfo);
            }
        } catch (DbException e) {
        } finally {
            if (mCursor != null)
                mCursor.close();
        }
        return mListData;
    }

    public synchronized boolean deleteItemBySsid(String ssid, String bssid) {
        boolean isDelSuccess = false;
        try {
            mWifiInfoDbUtil.execNonQuery("delete from wifiInfo where ssid = '" + ssid + "'"
                    + " and bssid = '" + bssid + "'");
            isDelSuccess = true;
        } catch (DbException e) {
            isDelSuccess = false;
        }
        return isDelSuccess;
    }

    public WifiInfo getWifiInfoBaseSsid(String ssid, String bssid) {
        WifiInfo wifiInfo =null;
        Cursor mCursor = null;
        try {
            mCursor = mWifiInfoDbUtil.execQuery("select * from wifiInfo where ssid = '" + ssid
                    + "'"
                    + " and bssid = '" + bssid + "'");
            mCursor.moveToPosition(-1);
            while (mCursor.moveToNext()) {
                String type = mCursor.getString(4);
                String passw = mCursor.getString(5);
                wifiInfo = new WifiInfo();
                wifiInfo.setSsid(ssid);
                wifiInfo.setPassword(passw);
                wifiInfo.setBssid(bssid);
                wifiInfo.setWifiCipherType(type);
            }
        } catch (DbException e) {
        } finally {
            if (mCursor != null)
                mCursor.close();
        }
        return wifiInfo;
    }

}
