package org.emsg.views;

import android.app.Activity;


import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.emsg.wificonnect.R;


public class ActionBarView implements IView{
    View mBaseView ;
    Button mImageViewLeft;
    Button mImageViewRight;
    TextView mTextView;
    Button mViewRight;
    public ActionBarView(Activity activity){
        mBaseView =  activity.findViewById(R.id.headactionbar);
        mImageViewLeft = (Button) mBaseView.findViewById(R.id.img_headview_left);
        mImageViewRight = (Button) mBaseView.findViewById(R.id.img_headview_right);
        mViewRight = (Button) mBaseView.findViewById(R.id.img_headview_myright);
        mTextView = (TextView) mBaseView.findViewById(R.id.tv_headview_middle);
    }
    @Override
    public View getView() {
        return mBaseView;
    }
    public void setActionBar(int id,String title,int rightId){
        if(id ==-1){
            mImageViewLeft.setVisibility(View.GONE);
        }else {
            mImageViewLeft.setBackgroundResource(id);
        }
        if(rightId == -1){
            mImageViewRight.setVisibility(View.GONE);
        }else{
            mImageViewRight.setBackgroundResource(rightId);
        }
        mTextView.setText(title);
    }
    
    public void setActionBar(int id,String title,int rightId,int myId){
        setActionBar(id,title,rightId);
        if(myId!=-1){
            mViewRight.setVisibility(View.VISIBLE);
            mViewRight.setBackgroundResource(myId);
        }
        mTextView.setText(title);
    }
    
    public void setButtonText(String left,String  right){
        if(left!=null)mImageViewLeft.setText(left);
        if(right!=null)mImageViewRight.setText(right);
    }
    
    public void setAction(OnClickListener left,OnClickListener right){
        if(left!=null)mImageViewLeft.setOnClickListener(left);
        if(right!=null)mImageViewRight.setOnClickListener(right);
    }
    public void setAction(OnClickListener left,OnClickListener right,OnClickListener myRight){
        if(left!=null)mImageViewLeft.setOnClickListener(left);
        if(right!=null)mImageViewRight.setOnClickListener(right);
        if(myRight!=null)mViewRight.setOnClickListener(myRight);
    }
}
