package com.ljmob.corner.cache;

import android.content.Context;

import java.io.File;

public class FileCache {

	private File cacheDir;
	private File mainCache;

	public FileCache(Context context) {
		// 如果有SD卡则在SD卡中建一个LazyList的目录存放缓存的图片
		// 没有SD卡就放在系统的缓存目录中
		if (android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED))
			cacheDir = new File(
					android.os.Environment.getExternalStorageDirectory(),
					".Corner/Files");
		else
			cacheDir = context.getCacheDir();
		if (!cacheDir.exists())
			cacheDir.mkdirs();
		mainCache = new File(cacheDir.getAbsoluteFile() + "/mainCache");
		if (!mainCache.exists())
			mainCache.mkdirs();
	}

	public File getFile(String url) {
		// 将url的hashCode作为缓存的文件名
		String filename = String.valueOf(url.hashCode());
		// Another possible solution
		// String filename = URLEncoder.encode(url);
		File f = new File(mainCache, filename);
		return f;

	}

	public void clear() {
		File[] files = cacheDir.listFiles();
		if (files == null)
			return;
		for (File f : files)
			f.delete();
	}

	public String getSaveFilePath() {
		return cacheDir.getAbsolutePath();
	}
}
