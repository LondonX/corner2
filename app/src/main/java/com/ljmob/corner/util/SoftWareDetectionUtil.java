package com.ljmob.corner.util;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SoftWareDetectionUtil {
	/** 
     * 检查手机上是否安装了指定的软件 
     * @param context 
     * @param packageName：应用包名 
     * @return 
     */  
    public static boolean isAvilible(Context context, String packageName){   
        //获取packagemanager   
        final PackageManager packageManager = context.getPackageManager();  
      //获取所有已安装程序的包信息   
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(0);  
      //用于存储所有已安装程序的包名   
        List<String> packageNames = new ArrayList<String>();  
        //从pinfo中将包名字逐一取出，压入pName list中   
        if(packageInfos != null){   
            for(int i = 0; i < packageInfos.size(); i++){   
                String packName = packageInfos.get(i).packageName;   
                packageNames.add(packName);   
            }   
        }   
      //判断packageNames中是否有目标程序的包名，有TRUE，没有FALSE   
        return packageNames.contains(packageName);  
  }   
    
    /**
	 * 判断高德地图app是否已经安装
	 */
	public static boolean getAppIn(Context context) {
		PackageInfo packageInfo = null;
		try {
			packageInfo = context.getPackageManager().getPackageInfo(
					"com.autonavi.minimap", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		// 本手机没有安装高德地图app
		if (packageInfo != null) {
			return true;
		}
		// 本手机成功安装有高德地图app
		else {
			return false;
		}
	}

	/**
	 * 获取当前app的应用名字
	 */
	public static String getApplicationName(Context context) {
		PackageManager packageManager = null;
		ApplicationInfo applicationInfo = null;
		try {
			packageManager = context.getPackageManager();
			applicationInfo = packageManager.getApplicationInfo(
					context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			applicationInfo = null;
		}
		String applicationName = (String) packageManager
				.getApplicationLabel(applicationInfo);
		return applicationName;
	}
    
}
