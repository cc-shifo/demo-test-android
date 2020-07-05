/*
 * = COPYRIGHT
 *     TianYu
 *
 * Description:
 *
 * Date                    Author                    Action
 * 2020-06-29              LiuJian                    Create
 */

package com.example.democircleprogressbar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.CallSuper;

public abstract class BasicDialog extends Dialog {
    public BasicDialog(Context context) {
        this(context, 0);
    }

    public BasicDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public BasicDialog(Context context, int themeResId) {
        super(context, themeResId);
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
    @CallSuper
    protected void initView() {
        setCancelable(false);
        setCanceledOnTouchOutside(false);
    }

}
