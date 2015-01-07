
package org.emsg.wifiauto;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.widget.Toast;

import com.emsg.wificonnect.R;
import com.sromku.simple.storage.Storage;

import org.emsg.util.SambaUtil;
import org.emsg.util.SambaUtil.ISambFileTrans;
import org.emsg.wifiauto.sambatask.SambaTaskCenter;

import java.io.File;

public class SambaDownLoadService extends Service {

    SambaTaskCenter mSambaTaskCenter;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSambaTaskCenter = MyApplication.getInstance().mSambaTaskCenter;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String fileName = intent.getStringExtra("fileName");
        String url = intent.getStringExtra("url");
        mSambaTaskCenter.sendTask(new MyTaskRunable(url, fileName), url, fileName);
        showToast(fileName + " " + getString(R.string.toast_filedownloadbegan));
        return super.onStartCommand(intent, flags, startId);
    }

    private void showToast(int id) {
        Toast.makeText(MyApplication.getInstance(), getString(id), Toast.LENGTH_SHORT)
                .show();
    }

    private void showToast(String text) {
        Toast.makeText(MyApplication.getInstance(), text, Toast.LENGTH_SHORT)
                .show();
    }

    public void onTaskSuccess(final String fileName, final String url) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                mSambaTaskCenter.removeTask(url);
                showToast(fileName + " " + getString(R.string.toast_filedownloadsuccess));
            }
        });
    }

    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }

    };

    public void onTaskError(final String fileName, final String url) {
        mHandler.post(new Runnable() {

            @Override
            public void run() {
                showToast(fileName + getString(R.string.toast_error_downloaderror));
                MyApplication.getInstance().getAppStorage()
                        .deleteFile(Constants.APP_MAIN_DIRECTORY, fileName + ".emsg");
            }
        });

    }

    public void onProgress(String url, int max, int current) {
        Intent mIntent = new Intent(Constants.ACTION_INTENT_SAMBAQUEUE_PROGRESS);
        mIntent.putExtra("url", url);
        mIntent.putExtra("max", max);
        mIntent.putExtra("current", current);
        sendBroadcast(mIntent);

    }

    class MyTaskRunable implements Runnable {
        private String url;
        private String fileName;

        public MyTaskRunable(String url, String fileName) {
            this.url = url;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            final Storage mStorage = MyApplication.getInstance().getAppStorage();
            File mFile = mStorage
                    .getFile(Constants.APP_MAIN_DIRECTORY, fileName);
            final String filePath = mFile.getAbsolutePath();
            SambaUtil.smbGet(url, filePath, new ISambFileTrans() {

                @Override
                public void onSuccess() {
                    onTaskSuccess(fileName, url);
                }

                @Override
                public void onError() {
                    onTaskError(fileName, url);
                }

                @Override
                public void onDownloading(final int total, final int current) {
                    onProgress(url, total, current);
                }
            });
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSambaTaskCenter.shutDown();
    }

}
