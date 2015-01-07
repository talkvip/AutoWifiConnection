
package org.emsg.wifiauto.control;

import android.app.Activity;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.emsg.util.Preferences;
import org.emsg.wifiauto.WifiLoginActivity;
import com.emsg.wificonnect.R;

public class ModifyPasswodControl extends LoginControl {
    WifiLoginActivity mAcitivity;

    public ModifyPasswodControl(Activity context) {
        super(context);
        this.mAcitivity = (WifiLoginActivity) context;
    }

    @Override
    public void onResume() {
        mAcitivity.mRegisterButton.setVisibility(View.GONE);
        mAcitivity.mLoginButton.setText(mAcitivity.getString(R.string.labler_button_suremodify));
        mAcitivity.mPswInputEditView.getEditText().setInputType(
                InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        EditText mEditText = mAcitivity.mAccInPutEditView.getEditText();
        mEditText.setEnabled(false);
        mEditText.setText(Preferences.getString(mAcitivity, "username"));
        mAcitivity.mActionBarView.setActionBar(R.drawable.icon_left,
                mAcitivity.getString(R.string.labler_modifypassword), -1);
        mAcitivity.mActionBarView.setAction(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mAcitivity.finish();
            }
        }, null);
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onDestroy() {

    }

    @Override
    protected void login() {
    }

    String mUserName;
    String mPassword;

    @Override
    public boolean filterLogin() {
        mUserName = mAcitivity.mAccInPutEditView.getInPutText();
        mPassword = mAcitivity.mPswInputEditView.getInPutText();
        if (TextUtils.isEmpty(mPassword)) {
            YoYo.with(Techniques.Tada)
                    .duration(700)
                    .playOn(mAcitivity.mEditTextPsw);
            return false;
        }
        return true;
    }

}
