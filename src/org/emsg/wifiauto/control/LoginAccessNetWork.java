
package org.emsg.wifiauto.control;

import android.content.Context;
import android.os.Handler;
import android.os.Message;


import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.emsg.wifiauto.Constants;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class LoginAccessNetWork {
    
    Handler myHandler ;
    HashMap<String, String> mHashMap = new HashMap<String, String>();
    public LoginAccessNetWork(Context context ,Handler myHandler){
        this.myHandler = myHandler;
    }
    public final static int MSGOK = 0;
    public final static int MSGCLOSEDIALOG = 1;
    public final static int NETOK = 2;
    public final static int CONNECTERROR = 3;
    public final static int AUTHERROR = 4;
    
    public MyLoadingRunable getMyLoadingRunable(String uName ,String password,String imei){
        return new MyLoadingRunable(uName, password, imei);
    }
    class MyLoadingRunable implements Runnable {
        String uName ;String password ;String imei;
        public MyLoadingRunable(String uName ,String password,String imei){
            this.uName = uName;
            this.password = password;
            this.imei = imei;
        }
        public void run() {
            try {
                int typeIntent = tryToIntent();
                if (typeIntent == -1) {
                    myHandler.sendEmptyMessage(NETOK);
                    return;
                }
                Map<String,String> isAuth = auth(imei,uName,password);
                if (isAuth.size() == 0) {
                    myHandler.sendEmptyMessage(CONNECTERROR);
                    return;
                } else {
                    boolean isAccess = access(isAuth.get("token"));
                    if (isAccess) {
                        Message msg = Message.obtain();
                        msg.obj = isAuth.get("url");
                        myHandler.sendEmptyMessage(MSGOK);
                        myHandler.sendEmptyMessageDelayed(MSGCLOSEDIALOG, 2000);
                    } else {
                        myHandler.sendEmptyMessage(AUTHERROR);
                    }
                }

            } catch (Exception e) {
            }
        }
    }
    public Map<String,String> auth(String imei ,String mUserName ,String mPassword) {
        Map<String,String> mDataResponse = new HashMap<String,String>();
        try {
            StringBuffer sb = new StringBuffer();
            sb.append("imei=" + imei);
            sb.append("&account=" + mUserName);
            sb.append("&password=" + mPassword);
            sb.append("&gw_id=" + mHashMap.get("gw_id"));
            sb.append("&gw_address=" + mHashMap.get("gw_address"));
            sb.append("&gw_port=" + mHashMap.get("gw_port"));
            sb.append("&url=" + mHashMap.get("url"));
            HttpGet get = new HttpGet(Constants.CLIENT_AUTH_URL + sb.toString());
            HttpClient httpClient = new DefaultHttpClient();
            HttpContext context = new BasicHttpContext();
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // /* 读取超时 */
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpResponse mResponse = httpClient.execute(get, context);
            if (mResponse.getStatusLine().getStatusCode() == 200) {
                String resultJson = EntityUtils.
                        toString(mResponse.getEntity());
                try {
                    JSONObject mJsonObject = new JSONObject(resultJson);
                    JSONObject mJsonData = mJsonObject.getJSONObject("data");
                    String mJsontoken = mJsonData.getString("token");
                    mDataResponse.put("token", mJsontoken);
                    String url = mJsonData.getString("url");
                    mDataResponse.put("url", url);
                    
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
        }
        return mDataResponse;
    }
    
    protected boolean access(String token) {
        String url = "http://" + mHashMap.get("gw_address") + ":"
                + mHashMap.get("gw_port") + "/wifidog/auth?token=" + token;
        try {
            HttpGet get = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpContext context = new BasicHttpContext();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // /* 读取超时 */
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpResponse resp = client.execute(get, context);
            if (resp.getStatusLine().getStatusCode() == 200) {
                return true;
            }
        } catch (IOException e) {
            return false;
        }
        return false;
    }
    
    
    public int tryToIntent() {
        try {
            HttpGet httpGet = new HttpGet(Constants.TEST_URL);
            HttpContext context = new BasicHttpContext();
            HttpClient httpClient = new DefaultHttpClient();
            // /* 请求超时 */
            httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // /* 读取超时 */
            httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            httpClient.execute(httpGet, context); // 发起GET请求
            HttpUriRequest curReq = (HttpUriRequest) context
                    .getAttribute(ExecutionContext.HTTP_REQUEST);
            HttpHost curHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
            String curUrl = (curReq.getURI().isAbsolute()) ? curReq.getURI().toString() : (curHost
                    .toURI() + curReq.getURI());

            if (curUrl != null) {
                String responseStr = curUrl;
                if (responseStr.equals(Constants.TEST_URL))
                    return -1;/* 已经可以正常上网了 */
                if (responseStr.indexOf(Constants.BASE_AUTH_PATH) > 0) {
                    int index = responseStr.indexOf("?");
                    responseStr = responseStr.substring(index + 1, responseStr.length());
                    String[] array = responseStr.split("&");
                    for (int i = 0; i < array.length; i++) {
                        String[] array2 = array[i].split("=");
                        mHashMap.put(array2[0], array2[1]);
                    }
                    return 0;
                } else {
                    return 1;
                }
            } else
            {
                return 1;
            }
        } catch (Exception e) {
            return 1;
        }
    }
}
