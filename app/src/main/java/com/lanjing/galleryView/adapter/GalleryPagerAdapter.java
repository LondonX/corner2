package com.lanjing.galleryView.adapter;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

import com.lanjing.galleryView.PhotoViewAttacher.OnPhotoTapListener;
import com.lanjing.galleryView.view.PhotoView;

public class GalleryPagerAdapter extends PagerAdapter {
	private ArrayList<String> urls;
	private OnPhotoTapListener onPhotoTapListener;

	public GalleryPagerAdapter(Context context, ArrayList<String> urls,
			OnPhotoTapListener onPhotoTapListener) {
		super();
		this.onPhotoTapListener = onPhotoTapListener;
		this.urls = urls;
	}

	@Override
	public int getCount() {
		return urls.size();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return arg0 == arg1;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		PhotoView photoView = (PhotoView) object;
		((BitmapDrawable) photoView.getDrawable()).getBitmap().recycle();
		container.removeView(photoView);
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		PhotoView photoView = new PhotoView(container.getContext());
		try {
			photoView.setImageUrl(new URL(urls.get(position)));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		if (onPhotoTapListener != null) {
			photoView.setOnPhotoTapListener(onPhotoTapListener);
		}
		container.addView(photoView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT);
		return photoView;
	}
}
