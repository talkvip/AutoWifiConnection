
package org.emsg.wifiauto;

import android.app.Activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.TypedValue;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;

import org.emsg.views.ActionBarView;

import java.util.ArrayList;
import java.util.List;

public class BaseActivity extends FragmentActivity {
    public static final List<Activity> mActivityStack = new ArrayList<Activity>();
    public ActionBarView mActionBarView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewUtils.inject(this);
        mActivityStack.add(this);
    }
    protected int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                getResources().getDisplayMetrics());
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mActivityStack.remove(this);
        if(mActivityStack.size() == 0){
            MyApplication.getInstance().isClosed =true;
        }
    }
    protected String[] getStringArrayFromResource(int id){
        return getResources().getStringArray(id);
    }
    
    protected void showToastShort(int id){
        Toast.makeText(this, getString(id), Toast.LENGTH_SHORT).show();
    }
    
    protected void showToastLong(int id){
        Toast.makeText(this, getString(id), Toast.LENGTH_LONG).show();
    }
    
    protected void showToastShort(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
