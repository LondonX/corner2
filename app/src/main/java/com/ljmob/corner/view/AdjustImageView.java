package com.ljmob.corner.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

public class AdjustImageView extends ImageView {

	public AdjustImageView(Context context) {
		super(context);
	}

	public AdjustImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public AdjustImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
		float i = ((float) getWidth()) / ((float) bitmap.getWidth());
		float imageHeight = i * (bitmap.getHeight());
		LayoutParams params = getLayoutParams();
		params.height = (int) imageHeight;
		setLayoutParams(params);
		super.setImageDrawable(drawable);
	}
}
