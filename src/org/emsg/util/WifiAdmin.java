
package org.emsg.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.WifiLock;

import org.emsg.wifiauto.MyApplication;

import java.net.Inet4Address;
import java.util.List;

public class WifiAdmin {
    private WifiManager mWifiManager;
    private WifiInfo mWifiInfo;
    private List<ScanResult> mWifiList;
    private List<WifiConfiguration> mWifiConfigurations;
    WifiLock mWifiLock;
    private Context context;

    public WifiAdmin(Context context) {
        mWifiManager = (WifiManager) context
                .getSystemService(Context.WIFI_SERVICE);
        mWifiInfo = mWifiManager.getConnectionInfo();
        this.context = context;
    }

    public WifiInfo getWifiInfo (){
        return  mWifiManager.getConnectionInfo();
    }
    public enum WifiCipherType {
        WIFICIPHER_WEP, WIFICIPHER_WPA, WIFICIPHER_NOPASS, WIFICIPHER_INVALID;
        public static String getStringTypeName(WifiCipherType type) {
            if (type == WIFICIPHER_WPA) {
                return "WPA";
            } else if (type == WIFICIPHER_WEP) {
                return "WEP";
            } else if (type == WIFICIPHER_NOPASS) {
                return "";
            }
            return "WPA";
        }

        public static WifiCipherType getWifitTypeFromString(String type) {
            WifiCipherType wifiType = WifiCipherType.WIFICIPHER_NOPASS;
            if (type.contains("WPA")) {
                wifiType = WifiCipherType.WIFICIPHER_WPA;
            } else if (type
                    .contains("WEP")) {
                wifiType = WifiCipherType.WIFICIPHER_WEP;
            }
            return wifiType;
        }
    }

    public boolean closeWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            return mWifiManager.setWifiEnabled(false);
        }
        return false;
    }

    /**
     * Gets the Wi-Fi enabled state״̬
     * 
     * @return One of {@link WifiManager#WIFI_STATE_DISABLED},
     *         {@link WifiManager#WIFI_STATE_DISABLING},
     *         {@link WifiManager#WIFI_STATE_ENABLED},
     *         {@link WifiManager#WIFI_STATE_ENABLING},
     *         {@link WifiManager#WIFI_STATE_UNKNOWN}
     * @see #isWifiEnabled()
     */
    public int checkState() {
        return mWifiManager.getWifiState();
    }

    public void acquireWifiLock() {
        mWifiLock.acquire();
    }

    public void releaseWifiLock() {
        if (mWifiLock.isHeld()) {
            mWifiLock.acquire();
        }
    }

    public void createWifiLock() {
        mWifiLock = mWifiManager.createWifiLock("test");
    }

    public List<WifiConfiguration> getConfiguration() {
        return mWifiConfigurations;
    }

    public void connetionConfiguration(int index) {
        if (index > mWifiConfigurations.size()) {
            return;
        }
        mWifiManager.enableNetwork(mWifiConfigurations.get(index).networkId,
                true);
    }

    public void startScan() {
        mWifiManager.startScan();
        mWifiList = mWifiManager.getScanResults();
        mWifiConfigurations = mWifiManager.getConfiguredNetworks();

    }

    public List<ScanResult> getWifiList() {
        return mWifiList;
    }

    public StringBuffer lookUpScan() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < mWifiList.size(); i++) {
            sb.append("Index_" + new Integer(i + 1).toString() + ":");
            sb.append((mWifiList.get(i)).toString()).append("\n");
        }
        return sb;
    }

   /* public String getMacAddress() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getMacAddress();
    }
*/
    /**
     * Return the basic service set identifier (BSSID) of the current access
     * point. The BSSID may be {@code null} if there is no network currently
     * connected.
     * 
     * @return the BSSID, in the form of a six-byte MAC address:
     *         {@code XX:XX:XX:XX:XX:XX}
     */
  /*  public String getBSSID() {
        return (mWifiInfo == null) ? "NULL" : mWifiInfo.getBSSID();
    }
*/
    public int getIpAddress() {
        return (getWifiInfo() == null) ? 0 : getWifiInfo().getIpAddress();
    }

    /**
     * Each configured network has a unique small integer ID, used to identify
     * the network when performing operations on the supplicant. This method
     * returns the ID for the currently connected network.
     * 
     * @return the network ID, or -1 if there is no currently connected network
     */
    public int getNetWordId() {
        return (getWifiInfo() == null) ? 0 : getWifiInfo().getNetworkId();
    }


    public void addNetWork(WifiConfiguration configuration) {
        int wcgId = mWifiManager.addNetwork(configuration);
        mWifiManager.enableNetwork(wcgId, true);
    }

    public void disConnectionWifi(int netId) {
        mWifiManager.disableNetwork(netId);
        mWifiManager.disconnect();
    }

    public boolean openWifi() {
        boolean bRet = true;
        if (!mWifiManager.isWifiEnabled()) {
            bRet = mWifiManager.setWifiEnabled(true);
        }
        return bRet;
    }
    public boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isConnected();
            }
        }
        return false;
    }
    public synchronized boolean connect(String SSID, String Password, WifiCipherType Type){
    	return connect(SSID, Password, Type,null);
    }
    public synchronized boolean connect(String SSID, String Password, WifiCipherType Type, String bssid) {
        if (!this.openWifi()) {
            return false;
        }
        if(isWifiConnected(MyApplication.getInstance())){
            WifiInfo wifiInfo =mWifiManager.getConnectionInfo();
            if(wifiInfo.getBSSID() .equals(bssid))
            return true;
        }
        while (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            try {
                Thread.currentThread();
                Thread.sleep(100);
            } catch (InterruptedException ie) {
            }
        }

        WifiConfiguration wifiConfig = createWifiInfo(SSID, Password, Type);
        //
        if (wifiConfig == null) {
            return false;
        }

        WifiConfiguration tempConfig = this.isExsits(SSID);

        int tempId = wifiConfig.networkId;
        if (tempConfig != null) {
            tempId = tempConfig.networkId;
            mWifiManager.removeNetwork(tempConfig.networkId);
        }
        int netID = mWifiManager.addNetwork(wifiConfig);

        mWifiManager.disconnect();
        boolean bRet = mWifiManager.enableNetwork(netID, true);
        mWifiManager.reconnect();
        if (bRet && bssid!=null) {
            MyApplication.getInstance().getDbManager()
                    .insertWifiInfo(SSID, Password, WifiCipherType.getStringTypeName(Type), bssid);
        }
        return bRet;
    }

    private WifiConfiguration isExsits(String SSID) {
        List<WifiConfiguration> existingConfigs = mWifiManager
                .getConfiguredNetworks();
        if (existingConfigs == null)
            return null;
        for (WifiConfiguration existingConfig : existingConfigs) {
            if (existingConfig.SSID.equals("\"" + SSID + "\"")) {
                return existingConfig;
            }
        }
        return null;
    }

    private WifiConfiguration createWifiInfo(String SSID, String Password,
            WifiCipherType Type) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";
        if (Type == WifiCipherType.WIFICIPHER_NOPASS) {
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        if (Type == WifiCipherType.WIFICIPHER_WEP) {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers
                    .set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == WifiCipherType.WIFICIPHER_WPA) {

            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms
                    .set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers
                    .set(WifiConfiguration.PairwiseCipher.CCMP);

        }
        return config;
    }

    /**
     * Function:
     * 
     * @param result
     * @return<br>
     */
    public boolean isConnect(ScanResult result) {
        if (result == null) {
            return false;
        }

        mWifiInfo = mWifiManager.getConnectionInfo();
        if (mWifiInfo.getSSID() != null
                && mWifiInfo.getSSID().equals(result.SSID)) {
            return true;
        }
        return false;
    }

    /**
     * Function:
     * 
     * @param ip
     * @return<br>
     */
    public String ipIntToString(int ip) {
        try {
            byte[] bytes = new byte[4];
            bytes[0] = (byte) (0xff & ip);
            bytes[1] = (byte) ((0xff00 & ip) >> 8);
            bytes[2] = (byte) ((0xff0000 & ip) >> 16);
            bytes[3] = (byte) ((0xff000000 & ip) >> 24);
            return Inet4Address.getByAddress(bytes).getHostAddress();
        } catch (Exception e) {
            return "";
        }
    }

    public int getConnNetId() {
        // result.SSID;
        mWifiInfo = mWifiManager.getConnectionInfo();
        return mWifiInfo.getNetworkId();
    }

    /**
     * @param level <br>
     */
    public static String singlLevToStr(int level) {

        String resuString = "";

        if (level > 0) {
            if (level > 60) {
                resuString = "强";
            }
            if (Math.abs(level) > 50) {
                resuString = "良好";
            } else {
                resuString = "较弱";
            }
        } else {
            if (Math.abs(level) > 80) {
                resuString = "弱";
            } else if (Math.abs(level) > 70) {
                resuString = "较弱";
            } else if (Math.abs(level) > 60) {
                resuString = "良好";
            } else if (Math.abs(level) > 50) {
                resuString = "强";
            } else {
                resuString = "很强";
            }
        }
        return resuString;
    }

    public boolean isCurWifiConnected(ScanResult mResult) {
        WifiInfo mWifiInfo = mWifiManager.getConnectionInfo();
        String ipAddress = ipIntToString(mWifiInfo.getIpAddress());
        if (mResult.BSSID.equals(mWifiInfo.getBSSID()) && !ipAddress.equals("0.0.0.0")) {
            return true;
        } else {
            return false;
        }

    }
}
