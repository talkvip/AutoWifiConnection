
package org.emsg.wifiauto;

import android.content.BroadcastReceiver;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

import com.baoyz.swipemenulistview.SwipeMenu;
import com.baoyz.swipemenulistview.SwipeMenuCreator;
import com.baoyz.swipemenulistview.SwipeMenuItem;
import com.baoyz.swipemenulistview.SwipeMenuListView;
import com.baoyz.swipemenulistview.SwipeMenuListView.OnMenuItemClickListener;
import com.emsg.wificonnect.R;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;

import org.emsg.views.ActionBarView;
import org.emsg.wifiauto.modle.DownLoadFilesListAdapter;
import org.emsg.wifiauto.modle.Item;
import org.emsg.wifiauto.sambatask.SambaTaskCenter;

import java.util.LinkedList;
import java.util.List;

@ContentView(R.layout.activity_commonlist)
public class DownLoadFileListsActivity extends BaseActivity {
    @ViewInject(R.id.ll_comlistpage_head)
    private View mViewBody;
    @ViewInject(R.id.lv_commonlist)
    private SwipeMenuListView mSwipeMenuListView;
    private DownLoadBrodCast mBrodCastReciver;
    private DownLoadFilesListAdapter mDownLoadListAdapter;
    SambaTaskCenter mSambaCenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mViewBody.setVisibility(View.GONE);
        mSambaCenter = MyApplication.getInstance().mSambaTaskCenter;

        initListView();

        IntentFilter mFilter = new IntentFilter();
        mBrodCastReciver = new DownLoadBrodCast();
        mFilter.addAction(Constants.ACTION_INTENT_SAMBAQUEUE_CHANGED);
        mFilter.addAction(Constants.ACTION_INTENT_SAMBAQUEUE_PROGRESS);
        registerReceiver(mBrodCastReciver, mFilter);

        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(R.drawable.icon_left,
                getString(R.string.labler_title_downloadlist), -1);
        mActionBarView.setAction(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        }, null);
    }

    private void updateItemProgress(int max, int current, String url) {
        int position = mSambaCenter.mTaskQueue.mSambaTaskQueue.indexOf(url);
        mDownLoadListAdapter.upDateProgressBar(mSwipeMenuListView, position, max, current);
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
                    {
                        String url = mDownLoadListAdapter.getItem(position).key;
                        mSambaCenter.removeTask(url);
                    }

                        break;
                }
                return false;
            }
        });
        mDownLoadListAdapter = new DownLoadFilesListAdapter(this, getDatas());
        mSwipeMenuListView.setAdapter(mDownLoadListAdapter);
    }

    private List<Item> getDatas() {
        List<String> mListData = mSambaCenter.mTaskQueue.mSambaTaskQueue;
        if (mListData == null || mListData.size() == 0) {
            return null;
        }
        List<Item> tempListData = new LinkedList<Item>();
        for (int i = 0; i < mListData.size(); i++) {
            Item mItem = new Item();
            mItem.key = mListData.get(i);
            mItem.value = mSambaCenter.mTaskQueue.mHashMap.get(mItem.key);
            tempListData.add(mItem);
        }
        return tempListData;
    }

    class DownLoadBrodCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent mIntent) {
            if (mIntent.getAction().equals(Constants.ACTION_INTENT_SAMBAQUEUE_CHANGED)) {
                mDownLoadListAdapter.onDataChanged(getDatas());
            } else if (mIntent.getAction().equals(Constants.ACTION_INTENT_SAMBAQUEUE_PROGRESS)) {
                String url = mIntent.getStringExtra("url");
                int max = mIntent.getIntExtra("max", 100);
                int current = mIntent.getIntExtra("current", 1);
                updateItemProgress(max, current, url);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBrodCastReciver);
    }

}
