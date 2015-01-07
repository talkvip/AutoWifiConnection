
package org.emsg.wifiauto;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.util.ResourceUtil;
import org.emsg.util.WifiAdmin;
import org.emsg.util.WifiAdmin.WifiCipherType;
import org.emsg.views.ActionBarView;

import com.emsg.wificonnect.R;

import java.util.List;

@ContentView(R.layout.activity_listwifi)
public class WifiListActivity extends BaseActivity {
    @ViewInject(R.id.lv_wifilist)
    private ListView mListView;
    private Activity context;
    private List<ScanResult> mListWifiData;
    private WifiAdmin mWifiManager;
    private BroadcastReceiver mBrodWifiState;
    private WifiInfoAdapter mWifiInfiAdapter;
    @ViewInject(R.id.tv_actlistwifi_curconn)
    private TextView mConWifiTextView;
    ScanResult mResult;
    @ViewInject(R.id.img_wifilist_state)
    private ImageView mWifiConnState;
    @ViewInject(R.id.rl_wifilist_)
    private RelativeLayout rl_wifilist_;

    private void intentToWifiState(String ssid, int lever) {
        Intent mIntent = new Intent(context, WifiStateActivity.class);
        mIntent.putExtra("ssid", ssid);
        mIntent.putExtra("lever", lever);
        context.startActivity(mIntent);
    }

    public void onRlClicked(View v) {
        WifiInfo wifiInfo = mWifiManager.getWifiInfo();
        if (wifiInfo == null) {
            showToastShort(R.string.toast_wifiisunconn);
            return;
        }
        int lever = wifiInfo.getLinkSpeed();
        String ssid = wifiInfo.getSSID();
        intentToWifiState(ssid, lever);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        mWifiManager = MyApplication.getInstance().getWifiAdmin();
        mBrodWifiState = new WifiScanReciver();
        mWifiInfiAdapter = new WifiInfoAdapter();
        mListView.setAdapter(mWifiInfiAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int i, long arg3) {
                mResult = mWifiInfiAdapter.getItem(i);
                if (MyApplication.getInstance().getWifiAdmin().isCurWifiConnected(mResult)) {
                    // Toast.makeText(context,
                    // context.getString(R.string.toast_clickwificonned),
                    // Toast.LENGTH_SHORT).show();
                    intentToWifiState(mResult.SSID, mResult.level);
                    mResult = null;
                    return;
                }
                if (!mWifiInfiAdapter.isWifiLocked(mResult.capabilities)) {
                    MyApplication
                            .getInstance()
                            .getWifiAdmin()
                            .connect(mResult.SSID, "", WifiCipherType.WIFICIPHER_NOPASS,
                                    mResult.BSSID);
                    return;
                }
                org.emsg.wifiauto.db.WifiInfo mWifiInfo = MyApplication.getInstance()
                        .getDbManager()
                        .getWifiInfoBaseSsid(mResult.SSID, mResult.BSSID);
                if (mWifiInfo != null) {
                    MyApplication
                            .getInstance()
                            .getWifiAdmin()
                            .connect(
                                    mResult.SSID,
                                    mWifiInfo.getPassword(),
                                    WifiCipherType.getWifitTypeFromString(mWifiInfo
                                            .getWifiCipherType()), mResult.BSSID);
                    return;
                }
                Intent intent = new Intent(context, WifiLoginActivity.class);
                intent.putExtra("ssid", mResult.SSID);
                intent.putExtra("bssid", mResult.BSSID);
                intent.putExtra("type", mResult.capabilities);
                intent.putExtra(Constants.INTENT_KEY_LOGIN, Constants.INTENT_PARAM_WIFILOGIN);
                startActivityForResult(intent, 0);
            }
        });

        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(-1, getString(R.string.hint_emsgwifi), -1);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBrodWifiState);
        if (mActivityStack.size() == 0) {
            MyApplication.getInstance().exitSystem();
        }
    }

    class WifiScanReciver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {
            String action = intent.getAction();
            dismissProgressDialog();
            if (action.equals(
                    WifiManager.SCAN_RESULTS_AVAILABLE_ACTION))
            {
                getCurrConBassId();
                mListWifiData = mWifiManager.getWifiList();
                mWifiInfiAdapter.notifyDataSetChanged();
            } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                mWifiInfiAdapter.notifyDataSetChanged();
                boolean isWifiConn = MyApplication.getInstance().getWifiAdmin()
                        .isWifiConnected(context);
                if (isWifiConn) {
                    getCurrConBassId();
                    mWifiConnState.setBackgroundResource(R.drawable.icon_wificon);

                } else {
                    mWifiConnState.setBackgroundResource(R.drawable.icon_wifidiscon);
                    mConWifiTextView
                            .setText(getString(R.string.lable_youlastconnect));
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private String getCurrConBassId() {
        WifiInfo mWifiInfo = mWifiManager.getWifiInfo();
        if (mWifiInfo != null) {
            mConnBssid = mWifiInfo.getBSSID();
        }
        if (mWifiInfo != null && mWifiInfo.getSSID() != null)
            mConWifiTextView
                    .setText(getString(R.string.lable_youlastconnect) + mWifiInfo.getSSID());
        return mConnBssid;
    }

    protected void onResume() {
        super.onResume();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mIntentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mBrodWifiState, mIntentFilter);
        startScanWifi();
    };

    ProgressDialog mProgressDialog;

    private void startScanWifi() {
        mWifiManager.openWifi();
        mWifiManager.startScan();
        showProgressDialog();
    }

    private void showProgressDialog() {
        mProgressDialog = ProgressDialog.show(this, "", "searching....");
    }

    private void dismissProgressDialog() {
        mProgressDialog.dismiss();
    }

    String mConnBssid;

    class WifiInfoAdapter extends BaseAdapter {

        public WifiInfoAdapter() {

        }

        @Override
        public int getCount() {
            if (mListWifiData == null)
                return 0;
            return mListWifiData.size();
        }

        @Override
        public ScanResult getItem(int i) {
            return mListWifiData.get(i);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public synchronized void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }

        @Override
        public synchronized View getView(int poi, View v, ViewGroup mViewGroup) {
            ViewHolder mViewHolder;
            mViewHolder = new ViewHolder();
            v = LayoutInflater.from(context).inflate(R.layout.item_listview_wifilist, null);
            ViewUtils.inject(mViewHolder, v);

            ScanResult mResult = getItem(poi);
            mViewHolder.mWifiName.setText(mResult.SSID);
            mViewHolder.mWifiStrength.setImageDrawable(ResourceUtil.getImageResourceIDFromLeve(
                    getWifiLever(mResult.level), context));
            Object mWifiInfo = MyApplication.getInstance().getDbManager()
                    .getWifiInfoBaseSsid(mResult.SSID, mResult.BSSID);
            if (mWifiInfo != null) {
                mViewHolder.mWifiLockedstate.setVisibility(View.VISIBLE);
                mViewHolder.mWifiLockedstate.setBackgroundResource(R.drawable.icon_curconwifi);
            } else {
                setWifiStateIcon(mViewHolder.mWifiLockedstate, mResult.capabilities);
            }
            return v;
        }

        @SuppressLint("NewApi")
        private void setWifiStateIcon(ImageView imageView, String state) {
            if (!isWifiLocked(state)) {
                imageView.setVisibility(View.GONE);
            } else {
                imageView.setVisibility(View.VISIBLE);
                imageView.setBackgroundResource(R.drawable.icon_lockedwifi);
            }
        }

        public boolean isWifiLocked(String state) {
            return !state.trim().equals("[ESS]");
        }

        private int getWifiLever(int level) {
            int realver = Math.abs(level);
            if (realver < 40)
                return 4;
            if (realver < 60)
                return 3;
            if (realver < 70)
                return 2;
            if (realver < 80)
                return 4;
            return 4;
        }

        final class ViewHolder {
            @ViewInject(R.id.img_listitem_wifistrength)
            ImageView mWifiStrength;
            @ViewInject(R.id.tv_listitem_wifiname)
            TextView mWifiName;
            @ViewInject(R.id.img_listitem_wifilockstate)
            ImageView mWifiLockedstate;
        }
    }

}
