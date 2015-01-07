package org.emsg.wifiauto;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import org.cybergarage.http.HTTPServerList;
import org.emsg.util.FileServer;

public class HttpService extends Service
{
	
	private FileServer fileServer = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	} 
	
	@Override
	public void onCreate()
	{
		super.onCreate(); 
		fileServer = new FileServer();
		fileServer.start();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    return super.onStartCommand(intent, flags, startId);
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
		
		HTTPServerList httpServerList = fileServer.getHttpServerList();
		httpServerList.stop(); 
		httpServerList.close(); 
		httpServerList.clear(); 
		fileServer.interrupt(); 
		
	}

}
