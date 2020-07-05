/*
 * = COPYRIGHT
 *     TianYu
 *
 * Description:
 *
 * Date                    Author                    Action
 * 2020-06-30              Liu Jian                    Create
 */

package com.example.democircleprogressbar.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.IntRange;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.democircleprogressbar.LogUtils;
import com.example.democircleprogressbar.R;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class CustomProgressDialog extends BasicDialog {
    private TextView mTittle;
    private CustomProgressBar mProgressBar;
    private static final int defaultTimeoutMs = 6000;
    private int mTimeoutMs;
    private Disposable mDisposable;

    public CustomProgressDialog(Context context) {
        super(context);
    }

    public CustomProgressDialog(Context context, boolean cancelable,
                                OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public CustomProgressDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.dialog_time_count_down;
    }

    @Override
    protected void initData() {
        //noting
    }

    @Override
    protected void initView() {
        super.initView();
        mTittle = findViewById(R.id.dialog_progress_dialog_title);
        mProgressBar = findViewById(R.id.dialog_progress_bar);
    }

    /**
     * when calling {@link Dialog#show()}, {@link Dialog#create()} will be called, until then
     *  {@link Dialog#onCreate(Bundle)} will be executed, so any UI operation can not be done
     *  before {@link Dialog#onCreate(Bundle)} finish executing.
     */
    @Override
    protected void onStart() {
        super.onStart();
        mProgressBar.setMax(mTimeoutMs / 1000);

        Rect r = new Rect();
        Objects.requireNonNull(getWindow()).getDecorView().getWindowVisibleDisplayFrame(r);
        LogUtils.d("onStart r: " + r.left + " " + r.right + " " + r.top  + " " + r.bottom);
        LogUtils.d("onStart Tittle: " + mTittle.getLeft() + " " + mTittle.getRight()
                + " " + mTittle.getTop()  + " " + mTittle.getBottom());
        LogUtils.d("onStart ProgressBar: " + mProgressBar.getLeft() + " " + mProgressBar.getRight()
                + " " + mProgressBar.getTop()  + " " + mProgressBar.getBottom());
        WindowManager.LayoutParams p = getWindow().getAttributes();
        LogUtils.d("onStart ProgressBar: " + p.width + " " + p.width
                + " " + p.gravity  + " " + p.x + " " + p.y);
    }



    /**
     * when calling {@link Dialog#cancel()}, a cancel message will be
     * sent before {@link Dialog#dismiss()}. {@link Dialog#onStop()}  will be called in
     * {@link Dialog#dismiss()} and maybe an cancel or dismiss message will be posted through
     * {@link android.os.Handler}, the posted messages will be obtained by
     * {@link android.os.Handler}, then {@link android.os.Handler} will call the
     * {@link OnCancelListener} or {@link OnDismissListener}
     */
    @Override
    protected void onStop() {
        super.onStop();
        if (mDisposable != null && !mDisposable.isDisposed()) {
            LogUtils.d("onStop !!!");
            mDisposable.dispose();
            mDisposable = null;
        }
    }

    /**
     * Set timeout in millisecond.
     *
     * @param timeoutMs timeout value
     */
    public void setTimeoutMs(@IntRange(from = 0, to = 999999999L) int timeoutMs) {
        mTimeoutMs = timeoutMs;
    }

    /**
     * Update progress status of progress bar according to timeout value. In default, there's a 60s
     * timeout.
     */
    public void showProgressDialog() {
        if (!this.isShowing()) {
            this.show();
        }
        if (mTimeoutMs <= 0) {
            mTimeoutMs = defaultTimeoutMs;
        }
        WindowManager.LayoutParams p = Objects.requireNonNull(getWindow()).getAttributes();
        LogUtils.d("showProgressDialog: " + p.width + " " + p.width
                + " " + p.gravity  + " " + p.x + " " + p.y);

        final int timeout = mTimeoutMs / 1000;
        mDisposable = Observable.intervalRange(1, timeout, 0, 1,
                TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtils.d("showProgressDialog Observable: " + aLong);
                        mProgressBar.setProgress(timeout - aLong.intValue());
                        if (aLong.intValue() == timeout) {
                            if (CustomProgressDialog.this.isShowing()) {
                                CustomProgressDialog.this.dismiss();
                                LogUtils.d("showProgressDialog Observable: dismiss self");
                            }
                        }
                    }
                });
    }

    /**
     * @param tittle set tittle content
     */
    public void updateTittleText(String tittle) {
        mTittle.setText(tittle);
    }
}
