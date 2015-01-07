
package org.emsg.wifiauto;

import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.emsg.wificonnect.R;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.http.HttpHandler;
import com.lidroid.xutils.view.annotation.ContentView;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.sromku.simple.storage.Storage;

import org.emsg.util.FileUtil;
import org.emsg.util.OpenFiles;
import org.emsg.util.ResourceUtil;
import org.emsg.util.SambaUtil;
import org.emsg.util.SambaUtil.ISambFileTrans;
import org.emsg.views.ActionBarView;
import org.emsg.wifiauto.modle.FileItem;
import org.emsg.wifiauto.modle.SambaFileListAdapter;
import org.emsg.wifiauto.sambatask.SambaTaskCenter;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;

import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

@ContentView(R.layout.activity_commonlist)
public class SambaSharedFilesListAcitivity extends BaseActivity implements OnItemClickListener
{
    @ViewInject(R.id.lv_commonlist)
    private ListView mlistView;
    private ProgressDialog dialog = null;
    private SambaFileListAdapter adapter = null;
    private ArrayList<FileItem> al = new ArrayList<FileItem>();
    private SearchTask task = null;
    private String root = "/";
    @ViewInject(R.id.tv_currentpath)
    private TextView mTvFather;
    @ViewInject(R.id.iv_pathback)
    private ImageView mIvBackPath;
    private Context mContext;

    String currentPath;

    HttpUtils mHttpUtils;
    HttpHandler<File> mHttpHandler;
    SambaTaskCenter mSambaTaskCenter;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = this;
        init();
        mActionBarView = new ActionBarView(this);
        mActionBarView.setActionBar(R.drawable.icon_left, getString(R.string.labler_sharfiles),
                R.drawable.icon_right);
        mActionBarView.setAction(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                finish();
            }
        }, new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                intentToDownLoadLists();
            }
        });

        Intent intent = new Intent(this, HttpService.class);
        startService(intent);

        mIvBackPath.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                actionBackPath();
            }
        });
        mHttpUtils = new HttpUtils();
        mSambaTaskCenter = MyApplication.getInstance().mSambaTaskCenter;
    }
    
    private void intentToDownLoadLists(){
        Intent mIntent = new Intent(this,DownLoadFileListsActivity.class);
        startActivity(mIntent);
    }

    public boolean actionBackPath() {
        String fileRoot = mTvFather.getText().toString();
        if (fileRoot != null && !fileRoot.equals("/") && !fileRoot.equals("//")) {
            searchFile(getLasterPath());
            return true;
        }
        return false;
    }

    private String getLasterPath() {
        if (currentPath == null)
            return root;
        int lastIndexOfspit = currentPath.lastIndexOf("/");
        if (lastIndexOfspit < 6)
            return root;
        else {
            int lastSecondSpit = currentPath.substring(0, lastIndexOfspit).lastIndexOf("/");
            return currentPath.substring(0, lastSecondSpit + 1);
        }
    }

    private void showProgress() {
        dialog = new ProgressDialog(this);
        dialog.setMessage("loading...");
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void dismissProgress() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void downLoadFile(final String url, final String fileName) {

        boolean isEmsgHotWifi = MyApplication.getInstance().isConEmgHotWifi();
        if (!isEmsgHotWifi) {
            showToastShort(R.string.toast_notconnecthotwifi);
            return;
        }
        if (mSambaTaskCenter.isTaskInQueue(url)) {
            showToastShort(R.string.toast_task_isinquee);
            return;
        }
        if (mSambaTaskCenter.isTaskOverStackFlow()) {
            showToastShort(R.string.toast_task_isoverflow);
            return;
        }
        Intent intent = new Intent(this, SambaDownLoadService.class);
        intent.putExtra("fileName", fileName);
        intent.putExtra("url", url);
        startService(intent);
    }

    private void init()
    {
        String ip = getIntent().getStringExtra("homeip");
        adapter = new SambaFileListAdapter(this, al);
        mlistView.setAdapter(adapter);
        mlistView.setOnItemClickListener(this);
        root = "smb://" + ip;
        setTitle(root);
        searchFile(root);
    }

    private void setTitle(String path) {
        String fatherPath = path.replace(root, "/").replace("$", ":");
        mTvFather.setText(fatherPath);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
            long id)
    {

        FileItem fileItem = al.get(position);
        if (fileItem.isFile())
        {
            actionItemFileClick(fileItem);
        }
        else
        {
            String path = fileItem.getPath();
            searchFile(path);
        }
    }

    private enum FileType {
        TEXT, AUDIO, IMAGE, PDF, VIDEO, WEB, EXCEL, WORD, PPT, OTHER
    }

    private boolean actionLocalDownLoaded(String filaName, FileType mFileType) {
        Storage mStorage = MyApplication.getInstance().getAppStorage();
        if (mStorage.isFileExist(Constants.APP_MAIN_DIRECTORY, filaName)) {
            File file = mStorage.getFile(Constants.APP_MAIN_DIRECTORY, filaName);
            Intent mIntent = null;
            switch (mFileType) {
                case TEXT:
                    mIntent = OpenFiles.getTextFileIntent(file);
                    break;
                case VIDEO:
                    mIntent = OpenFiles.getVideoFileIntent(file);
                    break;
                case IMAGE:
                    mIntent = OpenFiles.getImageFileIntent(file);
                    break;
                case PDF:
                    mIntent = OpenFiles.getPdfFileIntent(file);
                    break;
                case AUDIO:
                    mIntent = OpenFiles.getAudioFileIntent(file);
                    break;
                case WEB:
                    mIntent = OpenFiles.getHtmlFileIntent(file);
                    break;
                case PPT:
                    mIntent = OpenFiles.getPPTFileIntent(file);
                    break;
                case EXCEL:
                    mIntent = OpenFiles.getExcelFileIntent(file);
                    break;
                case WORD:
                    mIntent = OpenFiles.getWordFileIntent(file);
                    break;
                default:
                    String lastOverName = filaName.substring(filaName.lastIndexOf(".") + 1,
                            filaName.length());
                    int last = ResourceUtil.getStringId(lastOverName);

                    if (last == R.string.app_name) {
                        break;
                    }
                    String type = getString(last);
                    mIntent = OpenFiles.getOtherFileIntent(file, type);
                    break;
            }
            try {
                startActivity(mIntent);
            } catch (Exception e) {
                showToastShort(R.string.toast_error_noapptouse);
            }
            return true;
        } else {
            return false;
        }
    }

    private boolean checkEndsWithInStringArray(String checkItsEnd,
            String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;

    }

    private void actionItemFileClick(final FileItem mFileItem) {
        final String fileName = mFileItem.getName();
        final FileType mFileType = getFileTypeFromFileName(fileName);
        if (mFileType == null) {
            showToastShort(R.string.toast_unknownfile);
        } else {
            boolean isLocal = actionLocalDownLoaded(fileName, mFileType);
            if (!isLocal) {
                String path = mFileItem.getPath().substring(6);
                final String url = getTransUrl(path);
                if (mFileType == FileType.AUDIO || mFileType == FileType.VIDEO) {

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(mContext);
                    dialogBuilder.setTitle(R.string.dialog_title_choseplaytype);

                    CharSequence[] menuItemArray = new CharSequence[] {
                            mContext.getString(R.string.playtype_online),
                            mContext.getString(R.string.playtype_download)
                    };
                    dialogBuilder.setItems(menuItemArray,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            actionOnLineMedia(url);
                                            break;
                                        case 1:
                                            downLoadFile(mFileItem.getPath(), fileName);
                                            break;

                                    }
                                }
                            });
                    dialogBuilder.show();

                } else {
                    downLoadFile(mFileItem.getPath(), fileName);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        boolean isHaveRoot = actionBackPath();
        if (!isHaveRoot)
            finish();
    }

    private String getTransUrl(String path) {
        try
        {
            path = URLEncoder.encode(path, "UTF-8");
        } catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
        }
        String ipVal = FileUtil.ip;
        int portVal = FileUtil.port;
        String httpReq = "http://" + ipVal + ":" + portVal + "/smb=";
        String url = httpReq + path;
        return url;
    }

    private FileType getFileTypeFromFileName(String fileName) {
        FileType fileType = null;

        if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingAudio))) {
            fileType = FileType.AUDIO;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingVideo))) {
            fileType = FileType.VIDEO;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingText))) {
            fileType = FileType.TEXT;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingPdf))) {
            fileType = FileType.PDF;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingImage))) {
            fileType = FileType.IMAGE;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingWebText))) {
            fileType = FileType.WEB;
        }
        else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingWord))) {
            fileType = FileType.WORD;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingExcel))) {
            fileType = FileType.EXCEL;
        } else if (checkEndsWithInStringArray(fileName,
                getStringArrayFromResource(R.array.fileEndingPPT))) {
            fileType = FileType.PPT;
        } else {
            fileType = FileType.OTHER;
        }

        return fileType;
    }

    private void actionOnLineMedia(String url) {
        Intent it = new Intent(Intent.ACTION_VIEW);
        Uri uri = Uri.parse(url);
        it.setDataAndType(uri, "video/mp4");
        startActivity(it);
    }

    private void searchFile(String path)
    {
        if (task == null || task.getStatus().equals(AsyncTask.Status.FINISHED))
        {
            new SearchTask().execute(path);
        }
    }

    class SearchTask extends AsyncTask<String, Void, String>
    {
        ArrayList<FileItem> item = new ArrayList<FileItem>();

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
            showProgress();
        }

        @Override
        protected String doInBackground(String... params)
        {
            String path = params[0];
            try
            {
                SmbFile smbFile = new SmbFile(path);
                ArrayList<SmbFile> dirList = new ArrayList<SmbFile>();
                ArrayList<SmbFile> fileList = new ArrayList<SmbFile>();
                SmbFile[] fs = smbFile.listFiles();
                for (SmbFile f : fs)
                {
                    if (f.isDirectory())
                    {
                        dirList.add(f);
                    }
                    else
                    {
                        fileList.add(f);
                    }
                }

                dirList.addAll(fileList);

                for (SmbFile f : dirList)
                {
                    String filePath = f.getPath();
                    String fileName = f.getName().replace("$", ":");
                    if (fileName.equalsIgnoreCase("IPC:/"))
                        continue;
                    boolean isFile = f.isFile();
                    if (isFile)
                        item.add(new FileItem(fileName, filePath, isFile, f.getContentLength()));
                    else {
                        item.add(new FileItem(fileName, filePath, isFile));
                    }
                }
                currentPath = path;
            } catch (MalformedURLException e)
            {
            } catch (SmbException e)
            {
                System.out.println("");
            } catch (IOException e) {
            }

            return params[0];
        }

        @Override
        protected void onPostExecute(String result)
        {
            super.onPostExecute(result);

            if (!item.isEmpty())
            {
                al.clear();
                for (FileItem i : item)
                {
                    al.add(i);
                }
                setTitle(result);
                adapter.onDataChanged(al);
            }
            else
            {
                showToastShort(R.string.toast_error_connecterror);
            }
            dismissProgress();
        }

    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        Intent intent = new Intent(this, HttpService.class);
        stopService(intent);
    }

}
