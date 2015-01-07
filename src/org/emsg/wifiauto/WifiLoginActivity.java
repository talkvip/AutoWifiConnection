
package org.emsg.wifiauto;

import android.os.Bundle;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.views.ActionBarView;
import org.emsg.views.MyInputEditView;
import org.emsg.wifiauto.control.LoginControl;
import org.emsg.wifiauto.control.ViewControlManager;
import com.emsg.wificonnect.R;

@ContentView(R.layout.activity_login)
public class WifiLoginActivity extends BaseActivity implements OnClickListener {
    @ViewInject(R.id.ll_login_account)
    public LinearLayout mEditTextAccount;
    @ViewInject(R.id.ll_login_psw)
    public LinearLayout mEditTextPsw;

    @ViewInject(R.id.bt_login_actionl)
    public Button mLoginButton;

    public  MyInputEditView mAccInPutEditView;
    public  MyInputEditView mPswInputEditView;
    private LoginControl mViewControl;
    
    @ViewInject (R.id.bt_register_actionl)
    public  Button mRegisterButton;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       
        mLoginButton.setOnClickListener(this);
        String tagControl = Constants.INTENT_PARAM_EMSGLOGIN;
        String intentTag = getIntent().getStringExtra(Constants.INTENT_KEY_LOGIN);
        if (intentTag != null) {
            tagControl = intentTag;
        }
        mViewControl = ViewControlManager.getLoginControl(tagControl, this);
        addEditText();
        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(-1, getString(R.string.app_name), -1);
       
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_login_actionl:
                mViewControl.actionLogin();
                break;
        }
    }

    private void addEditText() {
        mAccInPutEditView = new MyInputEditView(this);
        mAccInPutEditView.setHint(getString(R.string.hint_inputphonenum));
        mEditTextAccount.addView(mAccInPutEditView.getView());
        mPswInputEditView = new MyInputEditView(this);
        mPswInputEditView.setHint(getString(R.string.hint_inputpsw));
        mEditTextPsw.addView(mPswInputEditView.getView());
    }

    @Override
    protected void onPause() {
        super.onPause();
        mViewControl.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mViewControl.onResume();
    }
}
