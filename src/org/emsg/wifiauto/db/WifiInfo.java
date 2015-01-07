package org.emsg.wifiauto.db;

import com.lidroid.xutils.db.annotation.Column;
import com.lidroid.xutils.db.annotation.Table;

@Table(name = "wifiInfo", execAfterTableCreated = "CREATE UNIQUE INDEX index_name ON wifiInfo(ssid,bssid)")  // 建议加上注解， 混淆后表名不受影响
public class WifiInfo extends EntityBase{
    public static final String TAG = "wifiInfo";
   
    
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
    public String getWifiCipherType() {
        return wifiCipherType;
    }
    
    public void setWifiCipherType(String wifiCipherType) {
        this.wifiCipherType = wifiCipherType;
    }
    @Column(column = "bssid")
    private String bssid;
    @Column(column = "ssid")
    private String ssid;
    @Column(column = "password")
    private String password;
    @Column(column = "type")
    private String wifiCipherType;
    @Column(column = "time")
    private long time;
    public String getBssid() {
        return bssid;
    }
    public void setBssid(String bssid) {
        this.bssid = bssid;
    }
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

}
