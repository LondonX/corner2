/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.lanjing.galleryView.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.lanjing.galleryView.IPhotoView;
import com.lanjing.galleryView.PhotoViewAttacher;
import com.lanjing.galleryView.PhotoViewAttacher.OnMatrixChangedListener;
import com.lanjing.galleryView.PhotoViewAttacher.OnPhotoTapListener;
import com.lanjing.galleryView.PhotoViewAttacher.OnViewTapListener;
import com.ljmob.corner.R;
import com.ljmob.corner.cache.FileCache;
import com.ljmob.corner.util.BitmapTool;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class PhotoView extends ImageView implements IPhotoView, Runnable,
		Callback {
	private final PhotoViewAttacher mAttacher;
	private static final int DOWNLOADING = 1;
	private static final int DOWNLOAD_COMPLETE = 2;

	private Context context;
	private ScaleType mPendingScaleType;
	private Handler handler;
	private URL url;
	private String cacheFolder;
	private String fileName;
	private File folder;
	private File imgFile;

	public PhotoView(Context context) {
		this(context, null);
	}

	public PhotoView(Context context, AttributeSet attr) {
		this(context, attr, 0);
	}

	public PhotoView(Context context, AttributeSet attr, int defStyle) {
		super(context, attr, defStyle);
		super.setScaleType(ScaleType.MATRIX);
		this.context = context;
		mAttacher = new PhotoViewAttacher(this);
		if (null != mPendingScaleType) {
			setScaleType(mPendingScaleType);
			mPendingScaleType = null;
		}
	}

	@Override
	public boolean canZoom() {
		return mAttacher.canZoom();
	}

	@Override
	public RectF getDisplayRect() {
		return mAttacher.getDisplayRect();
	}

	@Override
	public float getMinScale() {
		return mAttacher.getMinScale();
	}

	@Override
	public float getMidScale() {
		return mAttacher.getMidScale();
	}

	@Override
	public float getMaxScale() {
		return mAttacher.getMaxScale();
	}

	@Override
	public float getScale() {
		return mAttacher.getScale();
	}

	@Override
	public ScaleType getScaleType() {
		return mAttacher.getScaleType();
	}

	@Override
	public void setAllowParentInterceptOnEdge(boolean allow) {
		mAttacher.setAllowParentInterceptOnEdge(allow);
	}

	@Override
	public void setMinScale(float minScale) {
		mAttacher.setMinScale(minScale);
	}

	@Override
	public void setMidScale(float midScale) {
		mAttacher.setMidScale(midScale);
	}

	@Override
	public void setMaxScale(float maxScale) {
		mAttacher.setMaxScale(maxScale);
	}

	@Override
	// setImageBitmap calls through to this method
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	public void setImageUrl(URL url) {
		this.url = url;
		if (null == handler) {
			handler = new Handler(this);
		}
		new Thread(this).start();
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		if (null != mAttacher) {
			mAttacher.update();
		}
	}

	@Override
	public void setOnMatrixChangeListener(OnMatrixChangedListener listener) {
		mAttacher.setOnMatrixChangeListener(listener);
	}

	@Override
	public void setOnLongClickListener(OnLongClickListener l) {
		mAttacher.setOnLongClickListener(l);
	}

	@Override
	public void setOnPhotoTapListener(OnPhotoTapListener listener) {
		mAttacher.setOnPhotoTapListener(listener);
	}

	@Override
	public void setOnViewTapListener(OnViewTapListener listener) {
		mAttacher.setOnViewTapListener(listener);
	}

	@Override
	public void setScaleType(ScaleType scaleType) {
		if (null != mAttacher) {
			mAttacher.setScaleType(scaleType);
		} else {
			mPendingScaleType = scaleType;
		}
	}

	@Override
	public void setZoomable(boolean zoomable) {
		mAttacher.setZoomable(zoomable);
	}

	@Override
	public void zoomTo(float scale, float focalX, float focalY) {
		mAttacher.zoomTo(scale, focalX, focalY);
	}

	@Override
	protected void onDetachedFromWindow() {
		mAttacher.cleanup();
        Drawable drawable=getDrawable();
        if (drawable!=null){
            ((BitmapDrawable) drawable).getBitmap().recycle();
        }
		System.gc();
		super.onDetachedFromWindow();
	}

	@Override
	public boolean handleMessage(Message msg) {
		switch (msg.what) {
		case DOWNLOADING:
			break;
		case DOWNLOAD_COMPLETE:
			Bitmap bm = null;
			String path = imgFile.getAbsolutePath();
			try {
				bm = BitmapTool.getBitmapByPath(path, BitmapTool
						.getOptions(path), context.getResources()
						.getDisplayMetrics().widthPixels, context
						.getResources().getDisplayMetrics().heightPixels);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			if (bm != null) {
				setImageBitmap(bm);
			}
			setBackgroundResource(R.color.gray_light);
			invalidate();
			break;

		default:
			break;
		}
		return false;
	}

	@Override
	public void run() {
		handler.sendEmptyMessage(DOWNLOADING);
		if (null == cacheFolder) {
			cacheFolder = new FileCache(context).getSaveFilePath();
		}
		folder = new File(cacheFolder + "/artwork");
		if (folder.exists() == false) {
			folder.mkdirs();
		}
		fileName = String.valueOf(url.hashCode());
		imgFile = new File(folder, fileName);
		if (imgFile.exists() && imgFile.length() > 1024) {
			handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
		} else {
			try {
				URLConnection connection = url.openConnection();
				connection.connect();
				InputStream ips = connection.getInputStream();
				FileOutputStream fos = new FileOutputStream(imgFile);
				while (true) {
					byte[] bs = new byte[ips.available()];
					int read = ips.read(bs);
					if (read == -1) {
						break;
					}
					fos.write(bs);
					fos.flush();
				}
				ips.close();
				fos.close();
				handler.sendEmptyMessage(DOWNLOAD_COMPLETE);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}