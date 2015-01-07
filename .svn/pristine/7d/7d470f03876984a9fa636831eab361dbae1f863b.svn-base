
package org.emsg.wifiauto.modle;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.emsg.wificonnect.R;

import org.emsg.util.ViewHolder;

import java.util.List;

public class DownLoadFilesListAdapter extends ComListViewAdapter<Item> {

    public DownLoadFilesListAdapter(Context mContext, List<Item> mData) {
        super(mContext, mData);
    }

    public void onDataChanged(List<Item> mData) {
        this.mListData = mData;
        notifyDataSetChanged();
    }

    @Override
    public Item getItem(int args) {
        return super.getItem(args);
    }

    @Override
    public View getView(int posi, View viewTemp, ViewGroup father) {
        if (viewTemp == null) {
            viewTemp = LayoutInflater.from(context).inflate(
                    R.layout.item_listdownload_app, null);
        }
        Item mFileItem = getItem(posi);
        TextView mTvFileName = ViewHolder.get(viewTemp,
                R.id.tv_listhis_wifiname);
       
        mTvFileName.setText(mFileItem.value);
        ImageView img = ViewHolder.get(viewTemp,
                R.id.iv_item_icon);
        img.setVisibility(View.VISIBLE);
        img.setBackgroundResource(R.drawable.file_icon_default);
        
        ProgressBar progressBar = ViewHolder.get(viewTemp, R.id.pb_listItem);
        progressBar.setTag(posi);
        return viewTemp;
    }

    public void upDateProgressBar(ListView mListView,int posi,int max,int current){
        View mFatherView = mListView.getChildAt(
                posi - mListView.getFirstVisiblePosition());
        if(mFatherView ==null) return;
        ProgressBar mProgress = (ProgressBar) mFatherView.findViewById(
                R.id.pb_listItem);
        mProgress.setVisibility(View.VISIBLE);
        mProgress.setMax(max);
        mProgress.setProgress(current);
        if(max ==current){
            mProgress.setVisibility(View.GONE); 
        }
    }
    
    
}
