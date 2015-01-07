
package org.emsg.views;

import android.app.AlertDialog;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.emsg.wificonnect.R;

public class MyLoadingProgressBar {
    public interface OnLoading {
        public void onLoadingSuccess();

        public void onLoadFailed();

    }

    CircularProgressBar mCycleProgressBar;

    public CircularProgressBar getRateProgressBar() {
        return mCycleProgressBar;
    }

    AlertDialog mAlertDialog;
    OnLoading mOnLoading = new OnLoading() {

        @Override
        public void onLoadingSuccess() {
            OnShowSuccess();
        }

        @Override
        public void onLoadFailed() {
            dialogDismiss();
        }
    };
    
    public boolean isProgressShowing(){
        return mAlertDialog != null && mAlertDialog.isShowing();
    }
    ProgressBar mProgressBar;
    ImageView mImageView;
    PrgressType prgType;
    public MyLoadingProgressBar(Context mContext) {
        
        this.prgType = PrgressType.CYCYLE;
        init(mContext);
        
    }
    public MyLoadingProgressBar(Context mContext, PrgressType type) {
        this.prgType = type;
        init(mContext);
    }

    public enum PrgressType {
        CYCYLE, CYCLEWITHPROG
    }

    public void init(Context mContext) {
        mAlertDialog = new AlertDialog.Builder(mContext).create();
        mAlertDialog.show();
        mAlertDialog.setCanceledOnTouchOutside(false);
        Window window = mAlertDialog.getWindow();
        window.setContentView(R.layout.dialog_loading);
        window.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = window.getAttributes(); 
        lp.alpha = 0.6f;
        mProgressBar = (ProgressBar) window.findViewById(R.id.dialog_progress);
        mImageView = (ImageView) window.findViewById(R.id.iv_dialog_ok);
        mCycleProgressBar = (CircularProgressBar) window
                .findViewById(R.id.rate_progress_bar);
        if (prgType == PrgressType.CYCYLE) {
            mCycleProgressBar.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.VISIBLE);
        } else {
            mProgressBar.setVisibility(View.GONE);
            mCycleProgressBar.setVisibility(View.VISIBLE);
        }
        mImageView.setVisibility(View.GONE);
    }

    public OnLoading getOnLoadInterface() {
        return mOnLoading;
    }

    public void dialogDismiss() {
        if (isProgressShowing()) {
            try {
                mAlertDialog.cancel();
            } catch (Exception e) {
            }
        }
    }

    public void OnShowSuccess() {
        mProgressBar.setVisibility(View.GONE);
        mImageView.setVisibility(View.VISIBLE);
    }
}
