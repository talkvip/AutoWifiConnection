
package org.emsg.wifiauto.control;

import android.annotation.SuppressLint;
import android.app.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.emsg.wificonnect.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.emsg.util.Preferences;
import org.emsg.views.MyLoadingProgressBar;
import org.emsg.wifiauto.CheckWifiService;
import org.emsg.wifiauto.Constants;
import org.emsg.wifiauto.MyApplication;
import org.emsg.wifiauto.WebActivity;
import org.emsg.wifiauto.WifiListActivity;
import org.emsg.wifiauto.WifiLoginActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class EmsgLoginControl extends LoginControl {

    WifiLoginActivity mActivity;
    String imei;
    private LoginAccessNetWork mAccessNetWork;

    public EmsgLoginControl(Activity context) {
        super(context);
        this.mActivity = (WifiLoginActivity) context;
        TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        imei = tm.getDeviceId();

        mActivity.mRegisterButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onRegisterClick();
            }
        });
        mAccessNetWork = new LoginAccessNetWork(context, myHandler);
    }

    @Override
    public void login() {
        /*
         * if (access()) { }
         */
        // intentToWifiList();
        if (mLoadingProgress != null)
            mLoadingProgress.dialogDismiss();
        try {
            mLoadingProgress = new MyLoadingProgressBar(mActivity);
        } catch (Exception e) {
        }
        new Thread(mAccessNetWork.getMyLoadingRunable(mUserName, mPassword, imei)).start();
    }

    private void onRegisterClick() {
        boolean isFilter = filterLogin();
        // 执行注册步骤
        if (isFilter) {
            try {
                mLoadingProgress = new MyLoadingProgressBar(mActivity);
            } catch (Exception e) {
            }
            new Thread(new MyRegisterAccountRunable()).start();
        }

    }

    @SuppressLint("HandlerLeak")
    Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == LoginAccessNetWork.MSGOK) {
                mLoadingProgress.getOnLoadInterface().onLoadingSuccess();
                String url = (String) msg.obj;
                intentToWeb(url);
            } else if (msg.what == LoginAccessNetWork.MSGCLOSEDIALOG) {
                mLoadingProgress.dialogDismiss();
            } else if (msg.what == LoginAccessNetWork.NETOK) {
                String lastUrL = Preferences.getString(mActivity, "lasturl");
                intentToWeb(lastUrL);
            } else if (msg.what == LoginAccessNetWork.CONNECTERROR) {
                mLoadingProgress.dialogDismiss();
                String toastJsonError = mActivity.getString(R.string.toast_jsonerror);
                Toast.makeText(mActivity, toastJsonError, Toast.LENGTH_SHORT).show();
            } else if (msg.what == LoginAccessNetWork.AUTHERROR) {
                mLoadingProgress.dialogDismiss();
                String authError = mActivity.getString(R.string.toast_autherror);
                Toast.makeText(mActivity, authError, Toast.LENGTH_SHORT).show();
            }
            else if (msg.what == REGISTEROK) {
                String regSuss = mActivity.getString(R.string.toast_regsusandlogin);
                Toast.makeText(mActivity, regSuss, Toast.LENGTH_LONG).show();
                login();
            } else if (msg.what == REGISTERROR) {
                mLoadingProgress.dialogDismiss();
                String regFailed = mActivity.getString(R.string.toast_regfailed);
                Toast.makeText(mActivity, regFailed, Toast.LENGTH_SHORT).show();
            }
        }
    };

    private final int REGISTERROR = 5;
    private final int REGISTEROK = 6;
    MyLoadingProgressBar mLoadingProgress;

    private void intentToWifiList() {
        saveData();
        Intent mIntent = new Intent(mActivity, WifiListActivity.class);
        mActivity.startActivity(mIntent);
        mActivity.finish();
    }

    private void intentToWeb(String webUrl) {
        try {
            saveData();
            String savedUrl = (webUrl == null ? bdUrl : webUrl);
            Preferences.putString(mActivity, "lasturl", savedUrl);
            Intent intent = new Intent(mActivity, WebActivity.class);
            intent.putExtra("url", savedUrl);
            mActivity.startActivity(intent);
            mActivity.finish();
        } catch (Exception e) {
            intentToWifiList();
        }
    }

    private void saveData() {
        Preferences.putString(mActivity, "username", mUserName);
        Preferences.putString(mActivity, "password", mPassword);
    }

    final String bdUrl = "http://www.baidu.com";

    class MyRegisterAccountRunable implements Runnable {

        @Override
        public void run() {
            StringBuilder mStringRegister = new StringBuilder();
            mStringRegister.append("account=" + mUserName);
            mStringRegister.append("&password=" + mPassword);
            mStringRegister.append("&imei=" + imei);
            registerAccount(mStringRegister.toString());
        }
    }

    private void registerAccount(String authRegister) {
        String url = Constants.CLIENT_REGISTER_URL + authRegister;
        try {
            HttpGet get = new HttpGet(url);
            HttpClient client = new DefaultHttpClient();
            HttpContext context = new BasicHttpContext();
            client.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, 5000);
            // /* 读取超时 */
            client.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, 5000);
            HttpResponse resp = client.execute(get, context);
            if (resp.getStatusLine().getStatusCode() == 200) {
                String resultJson = EntityUtils.
                        toString(resp.getEntity());
                try {
                    JSONObject mjsonObje = new JSONObject(resultJson);
                    if (mjsonObje.getString("result").equals("true")) {
                        myHandler.sendEmptyMessage(REGISTEROK);
                        return;
                    }
                } catch (JSONException e) {
                }
            }
        } catch (IOException e) {
        }
        myHandler.sendEmptyMessage(REGISTERROR);
    }

    String mUserName;
    String mPassword;

    @Override
    public boolean filterLogin() {
        if (!MyApplication.getInstance().getWifiAdmin().isWifiConnected(mActivity)) {
            Toast.makeText(mActivity, "wifi未连接", Toast.LENGTH_SHORT).show();
            return false;
        }
        mUserName = mActivity.mAccInPutEditView.getInPutText();
        mPassword = mActivity.mPswInputEditView.getInPutText();
        if (TextUtils.isEmpty(mUserName)) {
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(mActivity.mEditTextAccount);
            return false;
        } else if (TextUtils.isEmpty(mPassword)) {
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(mActivity.mEditTextPsw);
            return false;
        }
        return true;
    }

    @Override
    public void onResume() {

        Intent intent = new Intent(mActivity, CheckWifiService.class);
        mActivity.startService(intent);

        String hisPaw = Preferences.getString(mActivity, "password");
        String hName = Preferences.getString(mActivity, "username");
        mActivity.mAccInPutEditView.getEditText().setInputType(
                InputType.TYPE_CLASS_NUMBER);
        mActivity.mPswInputEditView.getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        mActivity.mAccInPutEditView.getEditText().setText(hName);
        mActivity.mPswInputEditView.getEditText().setText(hisPaw);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

}
