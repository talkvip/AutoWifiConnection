
package org.emsg.views;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface.OnClickListener;

import com.emsg.wificonnect.R;

public class AlertDialogView {

    private Builder mDialog;
    private AlertDialog mAlertDialog;
    private Context mContext;
    public AlertDialogView(Activity mContext) {
        mDialog = new AlertDialog.Builder(mContext);
        this.mContext = mContext;
    }

    public void showAlertDialog(String title,String body,
            String nativebut,String ngBut,OnClickListener mpoListener,OnClickListener mNgListener){
        mDialog.setIcon(mContext.getResources().getDrawable(R.drawable.ic_launcher));
        mDialog.setTitle(title);
        mDialog.setMessage(body);
        mDialog.setPositiveButton(nativebut, mpoListener );
        mDialog.setNegativeButton(ngBut, mNgListener);
        mAlertDialog = mDialog.create();
        mAlertDialog.show();
    }
    
    public void dismiss(){
        if(mAlertDialog!=null&&mAlertDialog.isShowing())
        mAlertDialog.dismiss();
    }
}
