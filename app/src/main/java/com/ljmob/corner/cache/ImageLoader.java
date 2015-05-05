package com.ljmob.corner.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

import com.ljmob.corner.R;
import com.ljmob.corner.util.BitmapTool;

public class ImageLoader {
	public static final int M_PICTURE = 100;
	public static final int L_PICTURE = 300;
	public static final int X_PICTURE = 600;
	public static final int XX_PICTURE = 900;
	// 当进入listview时默认的图片，可换成你自己的默认图片
	final int stub_id = R.drawable.img_loading_pic;
	private static Bitmap loadingBitmap;
	private static Bitmap loadingRoundBitmap;
	// MemoryCacheSr memoryCache = new MemoryCacheSr();//使用软引用
	MemoryCache memoryCache = new MemoryCache();
	FileCache fileCache;
	private Context context;
	private Bitmap bitmap;
	private boolean isBitmapLoadComplete = false;
	private Map<ImageView, String> imageViews = Collections
			.synchronizedMap(new WeakHashMap<ImageView, String>());
	// 线程池
	ExecutorService executorService;

	public ImageLoader(Context context) {
		this.context = context;
		fileCache = new FileCache(context);
		executorService = Executors.newFixedThreadPool(5);
		if (loadingBitmap == null || loadingRoundBitmap == null) {
			loadingBitmap = BitmapTool.read(context.getResources()
					.openRawResource(stub_id), Config.ALPHA_8);
			loadingRoundBitmap = BitmapTool.getRoundCornerBitmap(loadingBitmap,
					loadingBitmap.getWidth());
		}
	}

	// 最主要的方法
	/**
	 * 显示图片
	 * 
	 * @param url
	 *            图片地址
	 * @param imageView
	 *            需要显示的imageView控件
	 * @param isCyclo
	 *            是否加载圆形图片
	 */
	public void DisplayImage(String url, ImageView imageView, boolean isCyclo,
			int required_size) {
		imageViews.put(imageView, url);
		// 先从内存缓存中查找
		imageView.setScaleType(ScaleType.CENTER_INSIDE);
		Bitmap bitmap = memoryCache.get(url);
		if (bitmap != null) {
			if (isCyclo) {
				bitmap = BitmapTool.getRoundCornerBitmap(bitmap,
						bitmap.getWidth());
			}
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setImageBitmap(bitmap);
			setBitmapLoadComplete(true);
		} else {
			// 若没有的话则开启新线程加载图片
			queuePhoto(url, imageView, isCyclo, required_size);
			if (isCyclo) {
				// bitmap = ((BitmapDrawable)
				// context.getResources().getDrawable(
				// stub_id)).getBitmap();
				imageView.setImageBitmap(loadingRoundBitmap);
			} else {
				imageView.setImageBitmap(loadingBitmap);
			}
		}
		setBitmap(bitmap);
	}

	// /**
	// * 设置是否加载圆形图片
	// *
	// * @param isCyclo
	// * @return
	// */
	// private void setCyclo(boolean isCyclo) {
	// this.isCyclo = isCyclo;
	// }

	private void queuePhoto(String url, ImageView imageView, boolean isCyclo,
			int required_size) {
		PhotoToLoad p = new PhotoToLoad(url, imageView, isCyclo, required_size);
		executorService.submit(new PhotosLoader(p));
	}

	private Bitmap getBitmap(String url, int required_size) {
		File f = fileCache.getFile(url);

		// 先从文件缓存中查找是否有
		Bitmap b = decodeFile(f, required_size);
		if (b != null)
			return b;

		// 最后从指定的url中下载图片
		try {
			Bitmap bitmap = null;
			URL imageUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection) imageUrl
					.openConnection();
			conn.setConnectTimeout(30000);
			conn.setReadTimeout(30000);
			conn.setInstanceFollowRedirects(true);
			InputStream is = conn.getInputStream();
			OutputStream os = new FileOutputStream(f);
			CopyStream(is, os);
			os.close();
			bitmap = decodeFile(f, required_size);
			// HttpGet httpRequest = new HttpGet(url);
			// HttpClient httpclient = new DefaultHttpClient();
			// HttpResponse response = (HttpResponse) httpclient
			// .execute(httpRequest);
			// HttpEntity entity = response.getEntity();
			// BufferedHttpEntity bufferedHttpEntity = new BufferedHttpEntity(
			// entity);
			// InputStream is = bufferedHttpEntity.getContent();
			// OutputStream os = new FileOutputStream(f);
			// CopyStream(is, os);
			// os.close();
			// bitmap = decodeFile(f);
			// bitmap = BitmapFactory.decodeStream(is);
			return bitmap;
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	// decode这个图片并且按比例缩放以减少内存消耗，虚拟机对每张图片的缓存大小也是有限制的
	private Bitmap decodeFile(File f, int required_size) {
		try {
			// decode image size
			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			o.inPreferredConfig = Config.ARGB_8888;
			o.inPurgeable = true;
			o.inInputShareable = true;
			BitmapFactory.decodeStream(new FileInputStream(f), null, o);

			// Find the correct scale value. It should be the power of 2.
			final int REQUIRED_SIZE = required_size;
			int width_tmp = o.outWidth, height_tmp = o.outHeight;
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < REQUIRED_SIZE
						|| height_tmp / 2 < REQUIRED_SIZE)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}

			// decode with inSampleSize
			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
		} catch (FileNotFoundException e) {
		}
		return null;
	}

	// Task for the queue
	private class PhotoToLoad {
		public String url;
		public ImageView imageView;
		public boolean isCyclo;
		public int required_size;

		public PhotoToLoad(String u, ImageView i, boolean isCyclo,
				int required_size) {
			url = u;
			imageView = i;
			this.isCyclo = isCyclo;
			this.required_size = required_size;
		}
	}

	class PhotosLoader implements Runnable {
		PhotoToLoad photoToLoad;

		PhotosLoader(PhotoToLoad photoToLoad) {
			this.photoToLoad = photoToLoad;
		}

		@Override
		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			Bitmap bmp = getBitmap(photoToLoad.url, photoToLoad.required_size);
			memoryCache.put(photoToLoad.url, bmp);
			if (imageViewReused(photoToLoad))
				return;
			BitmapDisplayer bd = new BitmapDisplayer(bmp, photoToLoad);
			// 更新的操作放在UI线程中
			Activity a = (Activity) photoToLoad.imageView.getContext();
			a.runOnUiThread(bd);
		}
	}

	/**
	 * 防止图片错位
	 * 
	 * @param photoToLoad
	 * @return
	 */
	boolean imageViewReused(PhotoToLoad photoToLoad) {
		String tag = imageViews.get(photoToLoad.imageView);
		if (tag == null || !tag.equals(photoToLoad.url))
			return true;
		return false;
	}

	// 用于在UI线程中更新界面
	class BitmapDisplayer implements Runnable {
		Bitmap bitmap;
		PhotoToLoad photoToLoad;

		public BitmapDisplayer(Bitmap b, PhotoToLoad p) {
			bitmap = b;
			photoToLoad = p;
		}

		public void run() {
			if (imageViewReused(photoToLoad))
				return;
			if (bitmap != null) {
				if (photoToLoad.isCyclo) {
					bitmap = BitmapTool.getRoundCornerBitmap(bitmap,
							bitmap.getWidth());
				}
				photoToLoad.imageView.setScaleType(ScaleType.CENTER_CROP);
				photoToLoad.imageView.setImageBitmap(bitmap);
			} else {
				if (photoToLoad.isCyclo) {
					photoToLoad.imageView.setImageBitmap(loadingRoundBitmap);
				} else {
					photoToLoad.imageView.setImageBitmap(loadingBitmap);
				}

			}
			setBitmap(bitmap);
			setBitmapLoadComplete(true);
		}
	}

	public void clearCache() {
		memoryCache.clear();
		fileCache.clear();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public boolean isBitmapLoadComplete() {
		return isBitmapLoadComplete;
	}

	public void setBitmapLoadComplete(boolean isBitmapLoadComplete) {
		this.isBitmapLoadComplete = isBitmapLoadComplete;
	}

	private void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}

}
