
package org.emsg.wifiauto.sambatask;

import com.sromku.simple.storage.Storage;

import org.emsg.wifiauto.Constants;
import org.emsg.wifiauto.MyApplication;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Future;

public class SambaTaskQueue {

    public  volatile LinkedList<String> mSambaTaskQueue;
    public volatile HashMap<String, String> mHashMap;
    public volatile HashMap<String,Future<?>> mHashMapFuture;

    private final int MAX_QUEUE_SIZE = 20;

    SambaTaskQueue() {
        mSambaTaskQueue = new LinkedList<String>();
        mHashMap = new HashMap<String, String>();
        mHashMapFuture =  new HashMap<String, Future<?>>();
    }

    public synchronized void offer(String url, String value,Future<?> mFuture) {
        mSambaTaskQueue.offer(url);
        mHashMap.put(url,value);
        mHashMapFuture.put(url, mFuture);
    }

    public synchronized String peek() {
        return mSambaTaskQueue.peek();
    }

    public synchronized int size() {
        return mSambaTaskQueue.size();
    }

    public synchronized boolean isTaskExists(String url) {
        return mSambaTaskQueue.contains(url);
    }

    public synchronized boolean isCanEnter() {
        return size() <= MAX_QUEUE_SIZE;
    }

    public synchronized void removeTask(String url) {
        mSambaTaskQueue.remove(url);
        mHashMap.remove(url);
        mHashMapFuture.remove(url).cancel(true);
    }

    public void onQueueSupportsError() {
        Storage mStorage = MyApplication.getInstance().getAppStorage();
        for (int i = 0; i <= 3; i++) {
            try {
                File mFile = mStorage.getFile(Constants.APP_MAIN_DIRECTORY, mHashMap.get(peek())
                        + ".emsg");
                mFile.delete();
            } catch (Exception e) {
                continue;
            }
        }
    }
}
