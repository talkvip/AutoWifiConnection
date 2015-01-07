package org.emsg.wifiauto;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.emsg.wifiauto.WifiAutoConnectManager.WifiCipherType;
import com.emsg.wificonnect.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	
	private TextView mTvInfo;
	private TextView mTvTitle;
	private TextView mBtnInternet;
	private String imei;
	
	// wifi
	private WifiManager wifiManager;
	private List<ScanResult> allscan;
	private SearchWifi searchWifi;
	
	private ScanResult wifiScan;
	HashMap<String, String> parmap;// wifi的请求参数
	
	protected static final int GETCHECKSUS = 300;
	protected static final int GETCHECKFAIL = 301;
	protected static final int TOALERT = 302;
	private static final int INITFAIL = 1000;
	protected static final int INITSUS = 2000;
	
	Context mContext;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {

			case Constants.SEND_INTERNET_SUSS:
				// 是否是定制wifi 返回是否正常上网状态
				if (parmap != null) {
					Log.d(TAG, "请求互联网成功,发送登录广播...");
					// 判断是定制wifi 登录尝试
					login();
				} else {
					Log.d(TAG, "已连接网络,获取检测页广告");
					mTvInfo.setText(mTvInfo.getText() + "\n" + "连接成功");
					mBtnInternet.setVisibility(View.VISIBLE);
					startWifiService();
				}
				break;
			case Constants.SEND_INTERNET_ERROR:
				Log.d(TAG, "请求互联网失败返回内容失败！在发送一次！");
				sendToInternet();
				break;
			case Constants.lOGIN_SUSS:
				if (token.length() > 0) {
					Log.d(TAG, "登录成功...正在验证路由器...");
					auth();
				} else {
					Log.d(TAG, "wifi...token为空...");
				}
				break;
			case Constants.LOGIN_ERROR:
				Log.d(TAG, "登录失败...未验证...");
				break;
			case Constants.AUTH_SUSS:
				Log.d(TAG, "...通过路由器验证成功..正常上网状态...");
				Log.d(TAG, "已连接网络，可以随心所欲的上网了");
				mTvInfo.setText(mTvInfo.getText() + "\n" + "连接成功");
				mBtnInternet.setVisibility(View.VISIBLE);
				startWifiService();
				break;
			case GETCHECKFAIL:
			case Constants.NO_NETWORK:
				Toast.makeText(MainActivity.this, "请检查您的网络", Toast.LENGTH_SHORT).show();
				break;
			case INITFAIL:
				Toast.makeText(MainActivity.this, "请检查您的网络", Toast.LENGTH_SHORT).show();
				break;
			}
		}
	};
	private WifiAutoConnectManager wifiAutoConnectManager;
	private String token;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = getApplicationContext();
		setContentView(R.layout.activity_main);
		StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		mTvTitle = (TextView) findViewById(R.id.mTvTitle);
		mTvInfo = (TextView) findViewById(R.id.mTvInfo);
		mBtnInternet = (Button) findViewById(R.id.mBtnInternet);
		TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
		imei = tm.getDeviceId();
		mBtnInternet.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				intent.setData(Uri.parse(Constants.TEST_URL));
				intent.setAction(Intent.ACTION_VIEW);
				startActivity(intent); //启动浏览器
			}
		});
		//startWifiService();
	}
	
	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StringBuffer sb = new StringBuffer();
		sb.append("认证服务器地址:" + Constants.BASE_AUTH_ROOT);
		sb.append("\n");
		sb.append("认证服务器路径:" + Constants.BASE_AUTH_PATH);
		mTvTitle.setText(sb.toString());
		mTvInfo.setText("努力连接中\nimei:"+imei + "\n");
		mBtnInternet.setVisibility(View.GONE);
		openWifi();
		
	}

	private void startWifiService() {
		Log.d(TAG, "定制WIFI检测服务开启!!!");
		try{
			Intent intent = new Intent();
			intent.setAction("com.emsg.wificonnect.CHECKWIFI");
			startService(intent);
		}catch(Exception ex){
			Log.e(TAG, ex.getMessage(),ex);
		}
		
	}


	private void openWifi() {
		// TODO Auto-generated method stub
		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
		if (!wifiManager.isWifiEnabled())
			wifiManager.setWifiEnabled(true);
		wifiManager.startScan();
		Log.d(TAG, "wifi打开,搜索WiFi列表");
		allscan = wifiManager.getScanResults();
		searchWifi = new SearchWifi();
		searchWifi.start();// 开启搜索 wifi
	}
	
	/**
	 * 是否存在定制wifi 选择信号最强的连接
	 */
	protected boolean authHaveWifi() {
		ArrayList<ScanResult> slist = new ArrayList<ScanResult>();
		if (allscan.size() > 0) {
			Iterator<ScanResult> list = allscan.iterator();
			while (list.hasNext()) {
				ScanResult scan = list.next();
				String ssid = scan.SSID;
				if (ssid.length() >= 3) {
					if (ssid.lastIndexOf(Constants.SSID) !=-1
							|| ssid.lastIndexOf(Constants.SSID2) != -1) {
						slist.add(scan);
						Log.d(TAG, "搜索到的ssid" + scan.SSID);
					}
				}
			}
		}
		Log.d(TAG, "搜索到的定制wifi数:" + slist.size());
		// 获取定制wifi信号最强值 的wifi
		double olevle = -1110.00;
		for (int i = 0; i < slist.size(); i++) {
			double nlevel = slist.get(i).level;
			olevle = olevle > nlevel ? olevle : nlevel;
			wifiScan = olevle > nlevel ? wifiScan : slist.get(i);
		}
		if (wifiScan != null) {
			return true;
		}
		return false;
	}

	class SearchWifi extends Thread {
		private boolean haveWifi = true;
		private long limit;
		long start = System.currentTimeMillis();

		@Override
		public void run() {
			// TODO Auto-generated method stub
			// // TODO Auto-generated method stub
			while (haveWifi) {
				try {
					allscan = wifiManager.getScanResults();
					Log.d(TAG, "开始搜索是否包含定制wifi！！！");
					if (wifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLED
							&& wifiManager.getWifiState() != WifiManager.WIFI_STATE_ENABLING) {
						limit = System.currentTimeMillis() - start;
						Log.d(TAG, "已搜索" + limit / 1000 + "s");
						if (allscan != null) {
							if (authHaveWifi()) {// 有定制wifi
								haveWifi = false;
								Log.d(TAG, "搜索到定制wifi！！！");
								checkConnectWifi();
							} else if (limit > 8 * 1000) {// 8秒 搜到 没有定制wifi
								Log.d(TAG, "8秒没有搜到定制wifi！！！");
								haveWifi = false;
							}
						}
					}
					//Log.d(TAG, ">>>>>>>>>>>>>>>>>执行了丫一次.");
					// 更新搜索ui
					Thread.sleep(500);
				} catch (InterruptedException e) {//
					// TODO Auto-generated catch block
					e.printStackTrace();
					Log.d(TAG, "SearchWifi InterruptedException");
				}
			}// while
		}
	}

	protected void checkConnectWifi() {
		// TODO Auto-generated method stub
		Log.d(TAG, "停止【searchWifi】检查连接搜索到的定制wifi");
		if (searchWifi != null)
			searchWifi.interrupt();// 搜索线程关闭
		// 已连接 是否联网状态
		if (wifiScan.BSSID.equals(wifiManager.getConnectionInfo().getBSSID())) {// mac相同
			// 判断是否联网
			Log.d(TAG, "已连接搜索到的定制wifi,验证是否上网");
			sendToInternet();
		} else {// 未连wifi接
			Log.d(TAG, "尝试连接定制wifi");
			/**
			 * 连接wifi
			 */
			wifiAutoConnectManager = new WifiAutoConnectManager(wifiManager);
			wifiAutoConnectManager.connect(wifiScan.SSID, "",WifiCipherType.WIFICIPHER_NOPASS);

			new Thread(new Runnable() {
				private boolean isconnect = true;
				private long limit;
				long start = System.currentTimeMillis();

				@Override
				public void run() {
					// TODO Auto-generated method stub
					while (isconnect) {
						try {
							limit = System.currentTimeMillis() - start;
							/**
							 * wifi已连接到热点
							 */
							if (wifiAutoConnectManager
									.isWifiConnected(MainActivity.this)
									&& wifiScan.BSSID.equals(wifiManager
											.getConnectionInfo().getBSSID())) {
								isconnect = false;
								Log.d(TAG, "尝试连接定制wifi连接成功,验证是否上网");

								sendToInternet();
							} else if (limit > 8000) {
								isconnect = false;
								Log.d(TAG, "尝试连接定制wifi连接失败");
								
							}
							Thread.sleep(1000);// 1秒检查一次
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							Log.d(TAG,"SearchWifi InterruptedException");
						}
					}
				}
			}).start();
		}
	}

	public void sendToInternet() {
		Log.d(TAG, "****************发送互联网****************************");
		final Message msg = new Message();
		msg.what = Constants.SEND_INTERNET_ERROR;
		try {
			HttpGet httpGet = new HttpGet(Constants.BASE_AUTH_ROOT);
			HttpContext context = new BasicHttpContext();
			HttpClient httpClient = new DefaultHttpClient();
			HttpResponse resp = httpClient.execute(httpGet,context); //发起GET请求 
			HttpUriRequest curReq = (HttpUriRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
	        HttpHost curHost = (HttpHost) context .getAttribute(ExecutionContext.HTTP_TARGET_HOST);
	        String curUrl = (curReq.getURI().isAbsolute()) ? curReq.getURI().toString() : (curHost.toURI() + curReq.getURI());
	        Log.i(TAG, ">>>>>>>>>>>>>>>>>" + curUrl);
	        
			if (curUrl != null) {
				String responseStr = curUrl;
				if (responseStr.indexOf(Constants.BASE_AUTH_PATH) > 0) {
					int index = responseStr.indexOf("?");
					// 截取字符串
					responseStr = responseStr.substring(index + 1,responseStr.length());
					Log.d(TAG, "*截取的字符串*" + responseStr);
					String[] array = responseStr.split("&");
					parmap = new HashMap<String, String>();
					for (int i = 0; i < array.length; i++) {
						String[] array2 = array[i].split("=");
						parmap.put(array2[0], array2[1]);
					}
					Log.d(TAG, "**被路由器拦截**");
				} else {
					Log.d(TAG, "***正常上网状态----或者非定制wifi***");
				}
				msg.what = Constants.SEND_INTERNET_SUSS;
				Log.d(TAG, "SEND_INTERNET_SUSS");
			} else {
				Log.d(TAG, "发送internet response为null");
			}
			mHandler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mHandler.sendMessage(msg);
			Log.d(TAG, "*******SEND_INTERNET_ERROR-1*************");
		}
	}

	/**
	 * 登录
	 */
	protected void login() {
		// TODO Auto-generated method stub

		final Message msg = new Message();
		msg.what = Constants.LOGIN_ERROR;
		try {
			StringBuffer sb = new StringBuffer();
			sb.append("imei="+imei);
			sb.append("&gw_id="+parmap.get("gw_id"));
			sb.append("&gw_address="+parmap.get("gw_address"));
			sb.append("&gw_port="+parmap.get("gw_port"));
			sb.append("&url="+parmap.get("url"));
			HttpGet get = new HttpGet(Constants.CLIENT_AUTH_URL + sb.toString());
			HttpClient httpClient = new DefaultHttpClient();
			HttpContext context = new BasicHttpContext();
			Log.d(TAG, ">>>>>>>>>>>>login request : " +get.getURI());
			HttpResponse resp = httpClient.execute(get,context); //发起GET请求 
			
			Log.d(TAG, ">>>>>>>>>>>>login result : " + resp.getStatusLine());
			
			//String str = EntityUtils.toString(resp.getEntity(), "utf-8");
			//if (str != null) {
				token = imei;
				// 保存 路由器 mac
				msg.what = Constants.lOGIN_SUSS;
			//}
			mHandler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mHandler.sendMessage(msg);
		} 
	}

	/**
	 * 验证路由器
	 */
	protected void auth() {
		// TODO Auto-generated method stub
		String url = "http://" + parmap.get("gw_address") + ":"
				+ parmap.get("gw_port") + "/wifidog/auth?token=" + token;
		final Message msg = new Message();
		msg.what = Constants.AUTH_ERROR;
		try {
			HttpGet get = new HttpGet(url);
			HttpClient client = new DefaultHttpClient();
			Log.d(TAG, ">>>>>>>>>>>>auth request : " +get.getURI());
			HttpResponse resp = client.execute(get);
			Log.d(TAG, ">>>>>>>>>>>>auth result : " + resp.getStatusLine());
			// 无法判断验证成功还是失败
			if (resp.getStatusLine().getStatusCode() == 200) {
				msg.what = Constants.AUTH_SUSS;
			}
			mHandler.sendMessage(msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			mHandler.sendMessage(msg);
		}
	}
}
