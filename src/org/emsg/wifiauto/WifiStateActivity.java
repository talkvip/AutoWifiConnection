
package org.emsg.wifiauto;

import android.app.Activity;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.util.Preferences;
import org.emsg.util.WifiAdmin;
import org.emsg.views.ActionBarView;
import com.emsg.wificonnect.R;

@ContentView(R.layout.activity_wifistate)
public class WifiStateActivity extends BaseActivity {
    @ViewInject(R.id.tv_wifistate_name)
    private TextView mTextSsid;
    @ViewInject(R.id.tv_wifistate_speed)
    private TextView mTextSpeed;
    private Activity mThis;
    @ViewInject(R.id.tv_wifistate_ip)
    private TextView mTextIp;
    private String currIp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mThis = this;
        setCurrentWifiState();
        mActionBarView = new ActionBarView(this);
        final String userName = Preferences.getString(this, "username");
        if (userName != null)
            mActionBarView.setActionBar(R.drawable.icon_left, getString(R.string.hint_emsgwifi),
                    R.drawable.icon_right);
        else {
            mActionBarView.setActionBar(R.drawable.icon_left, getString(R.string.hint_emsgwifi),
                    -1);
        }
        mActionBarView.setAction(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                if (userName != null) {
                    Intent intent = new Intent(mThis, WifiHistoryActivity.class);
                    mThis.startActivity(intent);
                }
            }
                
        });
    }
    private void setCurrentWifiState(){
        WifiInfo mWifiInfo = MyApplication.getInstance().getWifiAdmin().getWifiInfo();
        if(mWifiInfo ==null) {
            showToastShort(R.string.toast_notconnecthotwifi);
            return; 
        }
        final String ssid = mWifiInfo.getSSID();
        int lever = mWifiInfo.getLinkSpeed();
        if (ssid != null) {
            mTextSsid.setText(getString(R.string.hint_conwifiname) + ssid);
        }
        WifiAdmin mWifiAdmin = MyApplication.getInstance().getWifiAdmin();
        currIp = mWifiAdmin.ipIntToString(mWifiAdmin.getIpAddress()) ;
        
        mTextSpeed.setText(getString(R.string.hint_conwifispeed)  +WifiAdmin.singlLevToStr(lever) );
        mTextIp.setText(getString(R.string.hint_conwifiip)
                + currIp);
    }

  
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
