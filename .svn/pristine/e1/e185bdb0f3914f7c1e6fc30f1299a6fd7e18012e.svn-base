
package org.emsg.wifiauto.sambatask;

import android.content.Intent;

import org.emsg.wifiauto.Constants;
import org.emsg.wifiauto.MyApplication;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * to build the task for download the samba file , whilc is put on samba-server
 * targert , for the target -server is not support the breakpoint down manager ,
 * so when we find the task is on error we should delete the error file on task
 * , then the task will be reload the nexttime!
 */
public class SambaTaskCenter {

    private ExecutorService mThreadPoolExecutor;

    public SambaTaskQueue mTaskQueue;

    public SambaTaskCenter() {
        mThreadPoolExecutor = Executors.newFixedThreadPool(3);
        mTaskQueue = new SambaTaskQueue();
    }

    public void sendTask(Runnable mRunable, String url, String value) {
        Future<?> mFuture = mThreadPoolExecutor.submit(mRunable);
        mTaskQueue.offer(url, value, mFuture);
        MyApplication.getInstance().sendBroadcast(
                new Intent(Constants.ACTION_INTENT_SAMBAQUEUE_CHANGED));
    }

    public void removeTask(String url) {
        mTaskQueue.removeTask(url);
        MyApplication.getInstance().sendBroadcast(
                new Intent(Constants.ACTION_INTENT_SAMBAQUEUE_CHANGED));
    }

    public boolean isTaskInQueue(String url) {
        return mTaskQueue.isTaskExists(url);
    }

    public boolean isTaskOverStackFlow() {
        return !mTaskQueue.isCanEnter();
    }

    public int currentQueSize() {
        return mTaskQueue.size();
    }

    public void shutDown() {
        if (!mThreadPoolExecutor.isShutdown()) {
            mThreadPoolExecutor.shutdown();
            mTaskQueue.onQueueSupportsError();
        }
    }

}
