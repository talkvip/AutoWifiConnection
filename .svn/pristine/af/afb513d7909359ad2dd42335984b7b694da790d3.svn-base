
package org.emsg.wifiauto;

import android.app.Activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.util.Preferences;
import org.emsg.util.ViewHolder;
import org.emsg.views.ActionBarView;
import org.emsg.wifiauto.db.WifiInfo;

import com.emsg.wificonnect.R;

import java.util.List;

@ContentView(R.layout.activity_listwifihistory)
public class WifiHistoryActivity extends BaseActivity {
    private Activity mActivity;

    @ViewInject(R.id.tv_actlistwifihistory_curconn)
    private TextView mTextViewUname;

    @ViewInject(R.id.rl_wifilisthistory)
    private View mLinearLayout;

    @ViewInject(R.id.lv_wifilisthistory)
    private SwipeMenuListView mSwipeMenuListView;

    private List<WifiInfo> mListData;

    private MyWifiHistoryAdapter mWifiHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(R.drawable.icon_left, getString(R.string.labler_historyconn),
                R.drawable.icon_clear);

        mActionBarView.setAction(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent mIntent = new Intent(mActivity, WifiListActivity.class);
                mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                mActivity.startActivity(mIntent);
                finish();
            }
        });
        mTextViewUname.setText(Preferences.getString(this, "username"));

        actionModifyPassword();

        initListView();
    }

    private void initListView() {
        SwipeMenuCreator creator = new SwipeMenuCreator() {

            @Override
            public void create(SwipeMenu menu) {
                // create "delete" item
                SwipeMenuItem deleteItem = new SwipeMenuItem(
                        getApplicationContext());
                // set item background
                deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
                        0x3F, 0x25)));
                // set item width
                deleteItem.setWidth(dp2px(90));
                // set a icon
                deleteItem.setIcon(R.drawable.ic_delete);
                // add to menu
                menu.addMenuItem(deleteItem);
            }
        };
        mSwipeMenuListView.setMenuCreator(creator);

        mSwipeMenuListView.setOnMenuItemClickListener(new OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(int position, SwipeMenu menu, int index) {
                switch (index) {
                    case 0:
                        WifiInfo mWifiInfo = mWifiHistoryAdapter.getItem(position);
                        deleteItem(mWifiInfo.getSsid(), mWifiInfo.getBssid());
                        break;
                }
                return false;
            }
        });

        mWifiHistoryAdapter = new MyWifiHistoryAdapter();
        mSwipeMenuListView.setAdapter(mWifiHistoryAdapter);
    }

    private void actionModifyPassword() {
        mLinearLayout.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                /*
                 * Intent intent = new Intent(mActivity,
                 * WifiLoginActivity.class);
                 * intent.putExtra(Constants.INTENT_KEY_LOGIN,
                 * Constants.INTENT_PARAM_PASSWORD); startActivity(intent);
                 */
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        reflashDbData();
    }

    class MyWifiHistoryAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            if (mListData == null)
                return 0;
            return mListData.size();
        }

        @Override
        public WifiInfo getItem(int i) {
            return mListData.get(i);
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int i, View viewTemp, ViewGroup arg2) {

            if (viewTemp == null) {
                viewTemp = LayoutInflater.from(mActivity).inflate(
                        R.layout.item_listhistory_app, null);
            }
            WifiInfo mWifiInfo = getItem(i);
            TextView mTvFileName = ViewHolder.get(viewTemp,
                    R.id.tv_listhis_wifiname);
            mTvFileName.setText(mWifiInfo.getSsid());

            return viewTemp;
        }
    }

    private void reflashDbData() {
        mListData = MyApplication.getInstance().getDbManager().getUsedWifiInfo();
        mWifiHistoryAdapter.notifyDataSetChanged();
    }

    private void deleteItem(String ssid, String bssid) {
        boolean isDelSuss = MyApplication.getInstance().getDbManager()
                .deleteItemBySsid(ssid, bssid);
        if (isDelSuss) {
            reflashDbData();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
