/*
 *
 *  *
 *  *  * ****************************************************************************
 *  *  * Copyright (c) 2015. Muriel Kamgang Mabou
 *  *  * All rights reserved.
 *  *  *
 *  *  * This file is part of project AndroidWPTemplate.
 *  *  * It can not be copied and/or distributed without the
 *  *  * express permission of Muriel Kamgang Mabou
 *  *  * ****************************************************************************
 *  *
 *  *
 *
 */

package hr.mk.wpmagazine.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;
import android.util.TypedValue;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import hr.mk.wpmagazine.android.component.R;

/**
 * Created by Mur0 on 5/16/2015.
 */
public class ImageGetterUtils implements Html.ImageGetter {

    private static final String TAG = TagFactoryUtils.getTag(ImageGetterUtils.class);

    private final Object lock = new Object();
    private final WeakReference<TextView> weakTextView;
    private final boolean download;

    private ExecutorService executorService;
    private int counter;
    private ThreadLocal<Integer> localCount;
    private Map<String, Bitmap> map;// to keep track of all bitmap downloaded in order to recycle them on demand

    private final Runnable updateViewRunnable = new Runnable() {
        @Override
        public void run() {
            if (executorService != null && !executorService.isShutdown()
                    && weakTextView.get() != null) {
                Log.d(TAG, "resetting the TextView");
                final TextView view = weakTextView.get();
                view.setText(view.getText());
            }
        }
    };

    public ImageGetterUtils(TextView view, boolean download) {
        weakTextView = new WeakReference<>(view);
        this.download = download;
    }

    @Override
    public Drawable getDrawable(String source) {
        final URLDrawable drawable = new URLDrawable(); // provide at least an empty drawable to remove the default image provided by the parser
        if (download) {
            init();
            queueDownload(source, drawable);
        }
        return drawable;
    }

    public void recycleBitmaps() { // To improve ram usage, should be called when context destroyed.
        if (map == null || executorService == null)
            return;

        for (Map.Entry<String, Bitmap> entry : map.entrySet()) {
            Log.d(TAG, String.format("recycling bitmap: %s", entry.getKey()));
            entry.getValue().recycle();
        }

        map.clear();
        executorService.shutdownNow();
    }

    private void init() {
        if (executorService == null)
            executorService = Executors.newFixedThreadPool(3);
        if (map == null)
            map = new ConcurrentHashMap<>();
        if (localCount == null)
            localCount = new ThreadLocal<>();
    }

    private void queueDownload(final String source, final URLDrawable target) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    synchronized (lock) { //probably useless but at least it feel safer!
                        counter++;
                        localCount.set(counter);
                    }

                    final TextView view = weakTextView.get();
                    if (view == null) return;

                    final Context context = view.getContext();

                    Bitmap bitmapTemp = Picasso.with(context).load(source).config(Bitmap.Config.RGB_565).get();
                    Log.d(TAG, String.format("Bitmap with ID: %d downloaded original sized: %dKB", localCount.get(), bitmapTemp.getByteCount() / 1024));
                    final Matrix m = new Matrix();
                    final float maxHeightPx = context.getResources().getDimension(R.dimen.feed_viewer_img_height);
                    final float maxWidthPx = context.getResources().getDimension(R.dimen.feed_viewer_img_width);
                    m.setRectToRect(new RectF(0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight()),
                            new RectF(0, 0, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                    bitmapTemp.getWidth() > maxWidthPx ? maxWidthPx : bitmapTemp.getWidth(),
                                    context.getResources().getDisplayMetrics()),
                                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, bitmapTemp.getHeight() > maxHeightPx ? maxHeightPx : bitmapTemp.getHeight(),
                                            context.getResources().getDisplayMetrics())), Matrix.ScaleToFit.CENTER);
                    final Bitmap bitmap = Bitmap.createBitmap(bitmapTemp, 0, 0, bitmapTemp.getWidth(), bitmapTemp.getHeight(), m, true);
                    map.put(source, bitmap);
                    Log.d(TAG, String.format("Bitmap with ID %d scaled final sized: %dKB", localCount.get(), bitmap.getByteCount() / 1024)); //can be a huge difference from the original depending on the screen size/density
                    //PicassoUtils.getImageCache(context).set(source, bitmap);

                    target.drawable = new BitmapDrawable(context.getResources(), bitmap);
                    final int dHeight = target.drawable.getIntrinsicHeight();
                    final int dWidth = target.drawable.getIntrinsicWidth();
                    target.setBounds(0, 0, dWidth, dHeight);
                    target.drawable.setBounds(0, 0, dWidth, dHeight);

                    view.removeCallbacks(updateViewRunnable); //any better idea on how i can reduce the view reDraw Spam which cause a huge lag when a feed content a lot of images?
                    view.postDelayed(updateViewRunnable, 300);

                } catch (Exception e) {
                    //
                }
            }
        });
    }

    private static class URLDrawable extends BitmapDrawable {
        Drawable drawable;

        @Override
        public void draw(Canvas canvas) {
            if (drawable != null) {
                drawable.draw(canvas);
            }
        }
    }

}