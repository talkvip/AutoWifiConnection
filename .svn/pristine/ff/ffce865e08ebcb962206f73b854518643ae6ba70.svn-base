package org.emsg.wifiauto.modle;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

public class ComListViewAdapter <T> extends BaseAdapter{

    protected List<T> mListData;
    protected Context context;
    public ComListViewAdapter(Context mContext, List<T> mData){
        this.context = mContext;
        this.mListData = mData;
    }
    @Override
    public int getCount() {
        if(mListData==null)return 0;
        return mListData.size();
    }

    @Override
    public T getItem(int args) {
        return mListData.get(args);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int arg0, View arg1, ViewGroup arg2) {
        return null;
    }

}
