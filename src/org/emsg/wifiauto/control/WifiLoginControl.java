
package org.emsg.wifiauto.control;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.emsg.util.WifiAdmin;
import org.emsg.util.WifiAdmin.WifiCipherType;
import org.emsg.wifiauto.MyApplication;
import org.emsg.wifiauto.WifiLoginActivity;

public class WifiLoginControl extends LoginControl {
    WifiLoginActivity mActivity;
    MyNetWorkBrodCast mNetWorkBrodCast;
    WifiAdmin mWifiAdmin;
    ProgressDialog mPrgoressDialog;

    public WifiLoginControl(Activity context) {
        super(context);
        this.mActivity = (WifiLoginActivity) context;
        mActivity.mRegisterButton.setVisibility(View.GONE);
        mNetWorkBrodCast = new MyNetWorkBrodCast();
        mWifiAdmin = MyApplication.getInstance().getWifiAdmin();
    }

    String bssid;
    WifiCipherType wifitype;

    @Override
    public void login() {
        wifitype = WifiCipherType.WIFICIPHER_WPA;
        String ssid = mActivity.getIntent().getStringExtra("ssid");
        String type = mActivity.getIntent().getStringExtra("type").toUpperCase();
        bssid = mActivity.getIntent().getStringExtra("bssid");
        if (type.contains("WPA")) {
            wifitype = WifiCipherType.WIFICIPHER_WPA;
        } else if (type
                .contains("WEP")) {
            wifitype = WifiCipherType.WIFICIPHER_WEP;
        }
        WifiAdmin mWifiAdmin = MyApplication.getInstance().getWifiAdmin();
        showProgDialog();
        boolean isConn = mWifiAdmin.connect(ssid, mTextPassword, wifitype, null);
        if (!isConn) {
            playYoyo(Techniques.Landing);
            dissProgDialog();
        }
    }

    private void showProgDialog() {
        dissProgDialog();
        mPrgoressDialog = ProgressDialog.show(mActivity, "", "正在连接...");
        mPrgoressDialog.setCancelable(true);
    }

    private void dissProgDialog() {
        if (mPrgoressDialog != null && mPrgoressDialog.isShowing()) {
            mPrgoressDialog.dismiss();
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
                    dissProgDialog();
                    String curBssid = mWifiAdmin.getWifiInfo().getBSSID();
                    String ssid = mWifiAdmin.getWifiInfo().getSSID();
                    if (curBssid.equals(bssid)) {
                        MyApplication
                                .getInstance()
                                .getDbManager()
                                .insertWifiInfo(ssid, mTextPassword,
                                        WifiCipherType.getStringTypeName(wifitype), bssid);
                        mActivity.finish();
                    } else {
                        playYoyo(Techniques.Landing);
                    }
                }
            }
        }
    }

    String mTextPassword;

    @Override
    public boolean filterLogin() {
        mTextPassword = mActivity.mPswInputEditView.getInPutText().toString().trim();
        if (TextUtils.isEmpty(mTextPassword)) {
            playYoyo(Techniques.Tada);
            return false;
        }
        return true;
    }

    private void playYoyo(Techniques mTech) {
        YoYo.with(mTech)
                .duration(700)
                .playOn(mActivity.mEditTextPsw);
    }

    @Override
    public void onResume() {
        mActivity.mEditTextAccount.setVisibility(View.GONE);
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mActivity.registerReceiver(mNetWorkBrodCast, mIntentFilter);
    }

    @Override
    public void onPause() {
    }

    @Override
    public void onDestroy() {
        mActivity.unregisterReceiver(mNetWorkBrodCast);
    }

}
