
package org.emsg.wifiauto;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.emsg.wificonnect.R;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.util.Preferences;
import org.emsg.util.WifiAdmin;
import org.emsg.views.ActionBarView;
import org.emsg.views.AlertDialogView;
import org.emsg.views.MyLoadingProgressBar;
import org.emsg.wifiauto.control.LoginAccessNetWork;

@ContentView(R.layout.activity_web)
public class WebActivity extends BaseActivity
{
    @ViewInject(R.id.wv_webact)
    private WebView webview;
    @ViewInject(R.id.ll_webact_bott)
    private View mView;
    @ViewInject(R.id.headactionbar)
    private View mViewHead;
    @ViewInject(R.id.progbar_webview)
    private ProgressBar mProgressBar;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        WebSettings webSettings = webview.getSettings();
        webSettings.setJavaScriptEnabled(true);
        // 设置可以访问文件
        webSettings.setAllowFileAccess(true);
        // 设置支持缩放
        webSettings.setBuiltInZoomControls(true);
        // 加载需要显示的网页
        String url = getIntent().getStringExtra("url");
        // 设置Web视图
        webview.setWebViewClient(new webViewClient());
        webview.setWebChromeClient(new MyWebChromeClient());
        webview.loadUrl(url);
        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(-1, getString(R.string.app_name), -1, R.drawable.icon_menu);
        mActionBarView.setAction(null, null, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                shwoPopuWindow();
            }
        });

        mAutoLogin = new AutoToLogin(this);
        
    }

    PopupWindow mPopuWindow;

    @SuppressLint("NewApi")
    private void shwoPopuWindow() {
        if (mPopuWindow != null && mPopuWindow.isShowing()) {
            mPopuWindow.dismiss();
            return;
        }
        View mPopView = this.getLayoutInflater().inflate(R.layout.popu_menu_layout, null);
        mPopuWindow = new PopupWindow(mPopView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopuWindow.setOutsideTouchable(true);
        mPopuWindow.showAtLocation(mViewHead, Gravity.RIGHT | Gravity.TOP, 8, dp2px(78));

        View mViewShare = mPopView.findViewById(R.id.tv_popufirst);
        mViewShare.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                goToSharedFiles();
            }
        });
        View mViewWifiList = mPopView.findViewById(R.id.tv_popusecond);
        mViewWifiList.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                intentToList();
            }
        });
        View mViewState = mPopView.findViewById(R.id.tv_popthir);
        mViewState.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                intentToState();
            }
        });
    }

    private void intentToList() {
        Intent mIntent = new Intent(this, WifiListActivity.class);
        startActivity(mIntent);
    }

    private void intentToState() {
        Intent mIntent = new Intent(this, WifiStateActivity.class);
        startActivity(mIntent);
    }

    public void goToSharedFiles() {

        if (!MyApplication.getInstance().isConEmgHotWifi()) {
            showToastShort(R.string.toast_notconnecthotwifi);
            return;
        }

        WifiAdmin mWifiAdmin = MyApplication.getInstance().getWifiAdmin();
        String currIp = mWifiAdmin.ipIntToString(mWifiAdmin.getIpAddress());

        if (currIp == null || !currIp.contains(".")) {
            return;
        }
        Intent intent = new Intent(this, SambaSharedFilesListAcitivity.class);
        String homeLocal = currIp.substring(0, currIp.lastIndexOf("."));
        StringBuffer sb = new StringBuffer();
        sb.append(homeLocal);
        sb.append(".1");
        intent.putExtra("homeip", sb.toString());
        startActivity(intent);
    }

    @Override
    // 设置回退
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mPopuWindow != null && mPopuWindow.isShowing()) {
            mPopuWindow.dismiss();
            return true;
        }
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webview.canGoBack()) {
            webview.goBack();
            return true;
        }
        if (MyApplication.getInstance().mSambaTaskCenter.currentQueSize() > 0) {
            final AlertDialogView mAlertDialog = new AlertDialogView(this);
            mAlertDialog.showAlertDialog(getString(R.string.alert_title_protmte),
                    getString(R.string.alert_message_taskunfinish),
                    getString(R.string.alert_lablerbut_cancle),
                    getString(R.string.alert_lablerbut_exitsure),
                    new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            mAlertDialog.dismiss();
                        }

                    }, new android.content.DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            finishWithExit();
                        }
                    });
            return true;
        }
        finishWithExit();
        return false;
    }

    private void finishWithExit() {
        finish();
        MyApplication.getInstance().exitSystem();
    }

    AutoToLogin mAutoLogin;

    private class webViewClient extends WebViewClient {

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.startsWith(Constants.BASE_AUTH_ROOT)) {
                // 到登录页面去
                intentToLogin();
                mAutoLogin.loginAuto();
                return true;
            }
            view.loadUrl(url);
            return true;
        }
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (newProgress == 100) {
                mProgressBar.setVisibility(View.GONE);
                return;
            }
            mProgressBar.setVisibility(View.VISIBLE);
            mProgressBar.setProgress(newProgress);
        }
    }

    private void intentToLogin() {
        showToastLong(R.string.toast_session_outoftime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    class AutoToLogin {
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == LoginAccessNetWork.MSGOK) {
                    mLoadingProgress.getOnLoadInterface().onLoadingSuccess();
                    String url = (String) msg.obj;
                    webview.loadUrl(url);
                } else if (msg.what == LoginAccessNetWork.MSGCLOSEDIALOG) {
                    mLoadingProgress.dialogDismiss();
                } else if (msg.what == LoginAccessNetWork.CONNECTERROR) {
                    String toastJsonError = getString(R.string.toast_jsonerror);
                    Toast.makeText(mContext, toastJsonError, Toast.LENGTH_SHORT).show();
                } else if (msg.what == LoginAccessNetWork.AUTHERROR) {
                    String authError = getString(R.string.toast_autherror);
                    Toast.makeText(mContext, authError, Toast.LENGTH_SHORT).show();
                }
                mLoadingProgress.dialogDismiss();
            }

        };
        private Context mContext;

        public AutoToLogin(Context context) {
            this.mContext = context;
            mAccessNetWork = new LoginAccessNetWork(WebActivity.this, mHandler);
        }

        private LoginAccessNetWork mAccessNetWork;
        MyLoadingProgressBar mLoadingProgress;

        public void loginAuto() {
            if (mLoadingProgress != null)
                mLoadingProgress.dialogDismiss();
            try {
                mLoadingProgress = new MyLoadingProgressBar(WebActivity.this);
            } catch (Exception e) {
            }
            String uName = Preferences.getString(mContext, "username");
            String password = Preferences.getString(mContext, "password");
            TelephonyManager tm = (TelephonyManager) mContext
                    .getSystemService(Context.TELEPHONY_SERVICE);
            String imei = tm.getDeviceId();
            new Thread(mAccessNetWork.getMyLoadingRunable(uName, password, imei)).start();
        }
    }
}
