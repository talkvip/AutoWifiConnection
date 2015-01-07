
package org.emsg.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;

import com.emsg.wificonnect.R;

public class MyInputEditView implements IView{

    View mViewBase;

    public MyInputEditView(Context context) {
        mViewBase = LayoutInflater.from(context).inflate(R.layout.layout_edittextcleardata,
                null);
        mEditText = (EditText) mViewBase.findViewById(R.id.et_layout_input);
        final View mClearDataView = mViewBase.findViewById(R.id.iv_layoutinput_del);
        mClearDataView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mEditText.setText("");
            }
        });
        mClearDataView.setVisibility(View.GONE);
        mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean isOnFocus) {
                if (isOnFocus)
                    mClearDataView.setVisibility(View.VISIBLE);
                else {
                    mClearDataView.setVisibility(View.GONE);
                }
            }
        });

    }
    public void setHint(String hint ){
        mEditText.setHint(hint);
    }
    public View getView() {
        return mViewBase;
    }
    private EditText mEditText;
    public EditText getEditText(){
        return mEditText;
    }
    public String getInPutText() {
        return mEditText.getText().toString().trim();
    }

}
