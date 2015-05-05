package com.ljmob.corner.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.View;

import com.ljmob.corner.R;
import com.ljmob.corner.cache.FileCache;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class BitmapTool {
    public static final int UNCONSTRAINED = -1;
    private static long maxMem = -1;

    public static Bitmap read(InputStream ips, Config config) {
        if (maxMem == -1) {
            maxMem = Runtime.getRuntime().maxMemory();
        }

        long freeMem = Runtime.getRuntime().freeMemory();
        BitmapFactory.Options opt = new BitmapFactory.Options();
        if (maxMem / 10 > freeMem) {
            Log.i("LondonX", "最大内存:" + maxMem + ",剩余:" + freeMem);
            System.gc();
            opt.inJustDecodeBounds = false;
            opt.inSampleSize = 2;
        }
        opt.inPreferredConfig = config;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        // 获取资源图片
        return BitmapFactory.decodeStream(ips, null, opt);
    }

    public static Bitmap getRoundCornerBitmap(Bitmap bitmap, float roundPX) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        Bitmap bitmap2 = Bitmap.createBitmap(width, height, Config.ARGB_4444);
        Canvas canvas = new Canvas(bitmap2);

        final int color = 0xFFEFEFEF;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, width, height);
        final RectF rectF = new RectF(rect);

        paint.setColor(color);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawRoundRect(rectF, roundPX, roundPX, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return bitmap2;
    }

    public static File saveView2File(View view) {
        Bitmap bitmap = view2Bitmap(view);
        File file = new File(new FileCache(view.getContext()).getSaveFilePath() +
                "viewShot.png");
        try {
            bitmap.compress(CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return file;
    }

    public static Bitmap view2Bitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        canvas.drawRGB(255, 255, 255);
        view.draw(canvas);
        return bitmap;
    }

    public static void addTrank(Context context, Canvas canvas) {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setColor(context.getResources().getColor(R.color.gray_dark));
        textPaint.setTextSize(32);
        textPaint.setShadowLayer(8, 4, 4,
                context.getResources().getColor(R.color.gray_dark));
        textPaint.setStrokeWidth(4);
        canvas.drawText(context.getResources().getString(R.string.app_name),
                16, 16, textPaint);
    }

    /*
     * 获得设置信息
     */
    public static Options getOptions(String path) {
        Options options = new Options();
        options.inJustDecodeBounds = true;// 只描边，不读取数据
        BitmapFactory.decodeFile(path, options);
        return options;
    }

    /**
     * 获得图像
     *
     * @param path
     * @param options
     * @return
     * @throws FileNotFoundException
     */
    public static Bitmap getBitmapByPath(String path, Options options,
                                         int screenWidth, int screenHeight) throws FileNotFoundException {
        System.gc();
        File file = new File(path);
        if (!file.exists()) {
            throw new FileNotFoundException();
        }
        FileInputStream in = null;
        in = new FileInputStream(file);
        if (options != null) {
            Rect r = getScreenRegion(screenWidth, screenHeight);
            int w = r.width();
            int h = r.height();
            int maxSize = w > h ? w : h;
            int inSimpleSize = computeSampleSize(options, maxSize, w * h);
            inSimpleSize = inSimpleSize * 2 / 3;
            options.inSampleSize = inSimpleSize; // 设置缩放比例
            options.inJustDecodeBounds = false;
            options.inPurgeable = true;
            options.inDither = false;
            options.inPreferredConfig = Config.RGB_565;
            options.inPreferQualityOverSpeed = true;
            options.inTempStorage = new byte[1024 * 1024 * 4];
        }
        Bitmap b = BitmapFactory.decodeStream(in, null, options);
        try {
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 获取需要进行缩放的比例，即options.inSampleSize
     *
     * @param options
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    public static int computeSampleSize(BitmapFactory.Options options,
                                        int minSideLength, int maxNumOfPixels) {
        int initialSize = computeInitialSampleSize(options, minSideLength,
                maxNumOfPixels);

        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }

        return roundedSize;
    }

    private static Rect getScreenRegion(int width, int height) {
        return new Rect(0, 0, width, height);
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options,
                                                int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;

        int lowerBound = (maxNumOfPixels == UNCONSTRAINED) ? 1 : (int) Math
                .ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == UNCONSTRAINED) ? 128 : (int) Math
                .min(Math.floor(w / minSideLength),
                        Math.floor(h / minSideLength));

        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }

        if ((maxNumOfPixels == UNCONSTRAINED)
                && (minSideLength == UNCONSTRAINED)) {
            return 1;
        } else if (minSideLength == UNCONSTRAINED) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }
}
