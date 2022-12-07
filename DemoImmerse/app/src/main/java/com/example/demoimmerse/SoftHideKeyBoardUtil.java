package com.example.demoimmerse;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;

public class SoftHideKeyBoardUtil {

    private Activity activity;
    private View mChildOfContent;
    private int usableHeightPrevious;
    private FrameLayout.LayoutParams frameLayoutParams;
    private int contentHeight;
    private boolean isfirst = true;
    private int statusBarHeight;

    private SoftHideKeyBoardUtil(Activity activity) {

        this.activity = activity;

        FrameLayout content = (FrameLayout) activity.findViewById(android.R.id.content);

        mChildOfContent = content.getChildAt(0);

        mChildOfContent.getViewTreeObserver()
                .addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

                    public void onGlobalLayout() {

                        if (isfirst) {

                            contentHeight = mChildOfContent.getHeight();

                            isfirst = false;

                        }

                        possiblyResizeChildOfContent();

                    }

                });

        frameLayoutParams = (FrameLayout.LayoutParams) mChildOfContent.getLayoutParams();

    }

    public static void assistActivity(Activity activity) {

        new SoftHideKeyBoardUtil(activity);

    }

    private void possiblyResizeChildOfContent() {

        int usableHeightNow = computeUsableHeight(activity);

        if (usableHeightNow != usableHeightPrevious) {

            int usableHeightSansKeyboard = mChildOfContent.getRootView().getHeight();

            int heightDifference = usableHeightSansKeyboard - usableHeightNow;

            if (heightDifference > (usableHeightSansKeyboard / 4)) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

                    frameLayoutParams.height =
                            usableHeightSansKeyboard - heightDifference + statusBarHeight;

                } else {

                    frameLayoutParams.height = usableHeightSansKeyboard - heightDifference;

                }

            } else {

                frameLayoutParams.height = contentHeight;

            }

            mChildOfContent.requestLayout();

            usableHeightPrevious = usableHeightNow;

        }

    }

    private int computeUsableHeight(Activity activity) {

        Rect frame = new Rect();

        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);

        int statusBarHeight = frame.top;

        Rect r = new Rect();

        mChildOfContent.getWindowVisibleDisplayFrame(r);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            return (r.bottom - r.top) + statusBarHeight;

        }

        return (r.bottom - r.top);

    }

}
