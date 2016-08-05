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

package hr.mk.wpmagazine.helper;

import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.lang.reflect.Method;

import hr.mk.wpmagazine.Utils.TagFactoryUtils;
import hr.mk.wpmagazine.android.component.R;

public class PopUpMessageHelper {

    private static final String TAG = TagFactoryUtils.getTag(PopUpMessageHelper.class);
    private final Activity activity;
    private long delay = 30000; // minimum delay after dismissed
    private long dismissedAt = 0;
    private View popUpView;
    private PopupWindow window;
    private boolean isShowing;
    private OnPopUpListener listener;
    private ImageView imageView;
    private TextView textView;
    private Drawable imageDrawable;

    public PopUpMessageHelper(Activity activity) {
        this.activity = activity;
    }

    public long getDelay() {
        return delay;
    }

    public void setDelay(long delay) {
        this.delay = delay;
    }

    private void initPopUp() {
        initView();
        window = new PopupWindow(popUpView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        window.setFocusable(false);
        window.setBackgroundDrawable(null);
        window.setOutsideTouchable(true);
    }

    private void initView() {
        popUpView = LayoutInflater.from(activity).inflate(R.layout.pop_up_view, null);
        textView = (TextView) popUpView.findViewById(R.id.textViewMsg);
        imageView = (ImageView) popUpView.findViewById(R.id.imageViewIcon);
        popUpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onDismiss();
            }
        });
    }

    private boolean checkInit() {
        return (window == null) || (popUpView == null);
    }

    private boolean canShow() {
        return (System.currentTimeMillis() - dismissedAt) > delay;
    }

    private void onDismiss() {
        if (checkInit())
            return;
        if (isShowing) {
            dismissedAt = System.currentTimeMillis();
            hide(); // dismiss
            if (listener != null)
                listener.onDismiss();
        }
    }

    public void show(CharSequence message) {
        if (!canShow()) {
            android.util.Log.e(TAG, "windows was dismissed, popup windows will be disable for the next: " + ((delay / 1000L) - ((System.currentTimeMillis() - dismissedAt) / 1000L)) + 's');
            return;
        }


        if (message == null)
            throw new NullPointerException("Please no null message is allowed");
        if (activity == null)
            throw new NullPointerException("Null activity, is the activity is running?");

        if (isShowing) {
            updateMessage(message);
            return;
        }

        if (checkInit())
            initPopUp();

        window.setAnimationStyle(R.style.popupAnimation);
        setWindowLayoutType();
        textView.setText(message);
//        final ViewGroup viewGroup = (ViewGroup) ((ViewGroup) activity
//                .findViewById(android.R.id.content)).getChildAt(0);
        final ViewGroup viewGroup = (ViewGroup) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        int[] location = new int[2];
        viewGroup.getLocationOnScreen(location);
        Rect rect = new Rect(location[0], location[1], location[0] + viewGroup.getWidth(), location[1] + viewGroup.getHeight());
        window.showAtLocation(viewGroup, Gravity.NO_GRAVITY, rect.left, rect.bottom / 2);
        isShowing = true;
        if (listener != null)
            listener.onShow();
    }

    public void clear() {
        imageView.setImageDrawable(activity.getResources().getDrawable(R.drawable.drawable_transparent));
        textView.setText("");
        imageDrawable = null;// to prone garbage collection
    }

    public void show(int imageRes, String message) {
        if (!canShow()) {
            android.util.Log.e(TAG, "windows was dismissed, popup windows will be disable for the next: " + ((delay / 1000L) - ((System.currentTimeMillis() - dismissedAt) / 1000L)) + 's');
            return;
        }
        if (checkInit())
            initPopUp();
        imageDrawable = null;
        try {
            imageDrawable = activity.getResources().getDrawable(imageRes);
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
        if (imageDrawable == null)
            throw new NullPointerException("Drawable not found");

        imageView.setImageDrawable(imageDrawable);
        show(message);
    }

    public void hide() {
        if ((window == null) || (popUpView == null) || !isShowing)
            return;
        window.dismiss();
        isShowing = false;
        if (listener != null)
            listener.onHide();
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public void setWindowLayoutType() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            try {
                popUpView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
                Method setWindowLayoutType = PopupWindow.class.getMethod("setWindowLayoutType", int.class);
                setWindowLayoutType.invoke(window, WindowManager.LayoutParams.TYPE_APPLICATION_ATTACHED_DIALOG);

            } catch (Exception e) {
                android.util.Log.e(TAG, "", e);
            }
        }
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void updateMessage(CharSequence message) {
        if ((window == null) || (popUpView == null))
            return;
        if (message == null)
            throw new NullPointerException("Please no null message is allowed");
        textView.setText(message);
    }

    public void updateImage(int imageRes) {
        try {
            imageDrawable = activity.getResources().getDrawable(imageRes);
        } catch (Exception e) {
            Log.d(TAG, "", e);
        }
        if (imageDrawable == null)
            throw new NullPointerException("Drawable not found");

        imageView.setImageDrawable(imageDrawable);
    }

    public void setOnPopUpListner(OnPopUpListener listner) {
        this.listener = listner;
    }

    public interface OnPopUpListener {

        void onShow();

        void onHide();

        void onDismiss();
    }
}
