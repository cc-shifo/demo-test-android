package com.example.demowarningbar;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.view.KeyEvent;
import android.view.View;

public class DispatchViewKeyEventToActivityListener implements View.OnKeyListener {
    private boolean needHandle = false;
    private boolean keyDown = false;

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {
        int c = event.getKeyCode();
        int act = event.getAction();
        // 返回键交给上面处理
        if (c == KeyEvent.KEYCODE_BACK || v == null) {
            return false;
        } else if (!keyDown && act == KeyEvent.ACTION_DOWN) {
            // repeatCount == 0代表是第一次按下按键，这时开始处理按键事件
            keyDown = true;
            needHandle = event.getRepeatCount() == 0;
        } else if (act == KeyEvent.ACTION_UP) {
            keyDown = false;
        }

        dispatchKeyEvent(v.getContext(), event);
        return true;
    }

    private boolean dispatchKeyEvent(Context context, KeyEvent keyEvent) {
        Activity activity = contextToActivity(context);
        if (activity != null) {
            activity.dispatchKeyEvent(keyEvent);
            return true;
        } else {
            return false;
        }
    }

    private Activity contextToActivity(Context context) {
        if (context == null) {
            return null;
        }
        if (context instanceof Activity) {
            return (Activity) context;
        } else if (context instanceof ContextWrapper) {
            Context context2 = ((ContextWrapper) context).getBaseContext();
            if (context2 instanceof Activity) {
                return (Activity) context2;
            }
        }
        return null;
    }
}
