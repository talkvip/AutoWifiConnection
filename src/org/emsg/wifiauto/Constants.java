
package org.emsg.wifiauto;

public class Constants {

    // net state
    public static final int NO_NETWORK = 0;// 无网络

    // wifi state
    public static final int LOGIN = 19;
    public static final int AUTHCODE = 6;
    public static final int SEARCH = 7;
    public static final int SEACHRESULT = 8;
    public static final int NO_WIFI_START_LOCATION = 9;
    public static final int NOW_CONNECT = 10;
    public static final int NOW_START_LOCATION = 11;
    public static final int SEND_INTERNET_ERROR = 12;
    public static final int SEND_INTERNET_SUSS = 13;

    // login
    public static final int LOGIN_ERROR = 14;
    public static final int lOGIN_SUSS = 15;
    public static final int AUTH_ERROR = 16;
    public static final int AUTH_SUSS = 17;
    public static final int ALERT = 18;

    public static String TEST_URL = "http://www.baidu.com/";
    // public static String BASE_AUTH_ROOT = "http://202.85.221.165:8080";
    // public static String BASE_AUTH_ROOT = "http://192.168.1.188:8080";
    public static String BASE_AUTH_ROOT = "http://113.9.158.34:888";
    public static String BASE_AUTH_PATH = "/wifidog_auth/";
    public static String CLIENT_AUTH_URL = BASE_AUTH_ROOT + BASE_AUTH_PATH + "clientauth?";
    public static String CLIENT_REGISTER_URL = BASE_AUTH_ROOT + BASE_AUTH_PATH + "register?";

    public static final String SSID = "_WXCZH";
    public static final String SSID2 = "_wxczh";

    public static final String INTENT_PARAM_EMSGLOGIN = "emgslogin";
    public static final String INTENT_PARAM_WIFILOGIN = "wifilogin";
    public static final String INTENT_PARAM_PASSWORD = "wifipasswordmodify";
    public static final String INTENT_KEY_LOGIN = "login";

    // storage

    public static final String APP_MAIN_DIRECTORY = "emsg-wifi";

    //intent action
    
    public static final String ACTION_INTENT_SAMBAQUEUE_CHANGED = "emsg.sambaqueuechanged";
    public static final String ACTION_INTENT_SAMBAQUEUE_PROGRESS = "emsg.sambaqueueprogress";
    
}
