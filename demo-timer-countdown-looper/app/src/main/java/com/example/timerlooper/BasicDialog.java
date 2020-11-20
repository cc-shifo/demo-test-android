/*
 * = COPYRIGHT
 *     TianYu
 *
 * Description:
 *
 * Date                    Author                    Action
 * 2020-06-29              LiuJian                    Create
 */

package com.example.timerlooper;

import android.content.Context;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;

public abstract class BasicDialog extends AlertDialog {
    public BasicDialog(Context context) {
        super(context);
        create();
    }

    public BasicDialog(Context context, int themeResId) {
        super(context, themeResId);
        create();
    }

    public BasicDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        create();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayoutId());
        initData();
        initView();
    }



    /**
     * firstly, set ui for this activity
     */
    protected abstract int setLayoutId();

    /**
     * secondly, initialize data for UI
     */
    protected abstract void initData();

    /**
     * thirdly, update UI.
     */
    protected abstract void initView();

}
