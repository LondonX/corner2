package com.ljmob.corner.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

public class MacUtil {
	private Context context;
	private Handler handler;

	public MacUtil(Context context) {
		this.context = context;
	}

	/**
	 * 取得mac地址
	 * 
	 * @return
	 */
	public String getMac() {
		String macSerial = "";
		String str = "";
		try {
			Process pp = Runtime.getRuntime().exec(
					"cat /sys/class/net/wlan0/address ");
			InputStreamReader ir = new InputStreamReader(pp.getInputStream());
			LineNumberReader input = new LineNumberReader(ir);

			for (; null != str;) {
				str = input.readLine();
				if (str != null) {
					macSerial = str.trim();// 去空格
					break;
				}
			}
		} catch (IOException ex) {
			// 赋予默认值
			ex.printStackTrace();
		}
		return macSerial;
	}

	/**
	 * 取得本机当前ip
	 * 
	 * @return
	 */
	public String getIp() {
		// 网络是否可用
		if (isNetworkConnected()) {
			// 获取wifi服务
			WifiManager wifiManager = (WifiManager) context
					.getSystemService(Context.WIFI_SERVICE);
			// 判断wifi是否开启
			System.out.println("isOpenWifi:" + wifiManager.isWifiEnabled());
			if (wifiManager.isWifiEnabled()) {
				// wifiManager.setWifiEnabled(true);
				WifiInfo wifiInfo = wifiManager.getConnectionInfo();
				int ipAddress = wifiInfo.getIpAddress();
				return intToIp(ipAddress);
			} else {
				try {
					for (Enumeration<NetworkInterface> en = NetworkInterface
							.getNetworkInterfaces(); en.hasMoreElements();) {
						NetworkInterface intf = en.nextElement();
						for (Enumeration<InetAddress> enumIpAddr = intf
								.getInetAddresses(); enumIpAddr
								.hasMoreElements();) {
							InetAddress inetAddress = enumIpAddr.nextElement();
							if (!inetAddress.isLoopbackAddress()) {
								return inetAddress.getHostAddress().toString();
							}
						}
					}
				} catch (SocketException ex) {
					ex.printStackTrace();
				}
			}

		}
		return "";
	}

	private String intToIp(int i) {
		return (i & 0xFF) + "." + ((i >> 8) & 0xFF) + "." + ((i >> 16) & 0xFF)
				+ "." + (i >> 24 & 0xFF);
	}

	/**
	 * 判断网络是否连接
	 * 
	 * @return
	 */
	public boolean isNetworkConnected() {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	// public void sendMacAndIp() {
	// final String url = UrlStaticUtil.getMacAndIpUrl();
	// final Map<String, String> rawParams = new HashMap<String, String>();
	// rawParams.put("ip", getIp());
	// rawParams.put("mac_addr", getMac());
	// rawParams.put("platform", "android");
	// rawParams.put("app_version", UpdateUtil.getCurrentVersion(context));
	// rawParams.put("os_version", Build.VERSION.SDK_INT + "");
	// serviceImpl.sendMacAndIp(url, rawParams);
	// }

}
