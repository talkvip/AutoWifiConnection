
package org.emsg.wifiauto.modle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.emsg.wificonnect.R;

import org.emsg.util.ByteUtils;
import org.emsg.util.ViewHolder;

import java.util.List;

public class SambaFileListAdapter extends ComListViewAdapter<FileItem> {

    public SambaFileListAdapter(Context mContext, List<FileItem> mData) {
        super(mContext, mData);
    }

    public void onDataChanged(List<FileItem> mData) {
        this.mListData = mData;
        notifyDataSetChanged();
    }

    @Override
    public FileItem getItem(int args) {
        return super.getItem(args);
    }

    @Override
    public View getView(int posi, View viewTemp, ViewGroup father) {
        if (viewTemp == null) {
            viewTemp = LayoutInflater.from(context).inflate(
                    R.layout.item_listhistory_app, null);
        }
        FileItem mFileItem = getItem(posi);
        TextView mTvFileName = ViewHolder.get(viewTemp,
                R.id.tv_listhis_wifiname);
        int fizeSize = mFileItem.getFileSize();
        if (fizeSize != 0) {
            mTvFileName.setText(mFileItem.getName() + "  ("
                    + ByteUtils.bytes2kb(mFileItem.getFileSize()) + ")");
        } else {
            mTvFileName.setText(mFileItem.getName());
        }
        ImageView img = ViewHolder.get(viewTemp,
                R.id.iv_item_icon);
        img.setVisibility(View.VISIBLE);
        if(mFileItem.isFile())
        img.setBackgroundResource(R.drawable.file_icon_default);else{
            img.setBackgroundResource(R.drawable.folder);
        }
        return viewTemp;
    }

  
    
    
}
