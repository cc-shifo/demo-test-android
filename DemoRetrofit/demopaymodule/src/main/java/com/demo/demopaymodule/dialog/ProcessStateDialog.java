
/*
 *
 * = COPYRIGHT
 *          TianYu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200916    	         LiuJian                  Create
 */

package com.demo.demopaymodule.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.IntRange;
import androidx.annotation.StringRes;


import com.demo.demopaymodule.R;
import com.demo.demopaymodule.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.functions.Predicate;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class ProcessStateDialog extends BasicDialog {
    private ProcessStateView mStateView;
    private ProcessStateView.State mState;
    private ProcessStateView.State mStateOld;
    private ProcessStateView.StateListener mStateListener;

    private TextView mMsgTextView;
    private String mMessage;
    private Button mButton;

    /**
     * for countdown
     */
    private boolean mEnableCountDown;
    private int mTimeoutMs;
    /**
     * count-down dialog
     */
    private CompositeDisposable mCompositeDisposable;

    public ProcessStateDialog(Context context) {
        super(context, R.style.dialog_common_style);
    }

    public ProcessStateDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public ProcessStateDialog(Context context, boolean cancelable,
                              OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected int setLayoutId() {
        return R.layout.youxing_dialog_process_state;
    }

    @Override
    protected void initData() {
        // nothing
    }

    @Override
    protected void initView() {
        init();
    }

    public void init() {
        this.setCanceledOnTouchOutside(false);
        this.setCancelable(false);
        mStateView = findViewById(R.id.dialog_process_state_view);
        assert mStateView != null;
        mMsgTextView = findViewById(R.id.dialog_process_state_message);
        mButton = findViewById(R.id.dialog_common_btn_OK);
        assert mButton != null;
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProcessStateDialog.this.cancel();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCompositeDisposable = new CompositeDisposable();
        setState();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mStateView.stopStateAnia();
        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            LogUtils.d("onStop !!!");
            mCompositeDisposable.clear();
            mCompositeDisposable = null;
        }
    }

    public void setState(ProcessStateView.State state, String message,
                         ProcessStateView.StateListener listener) {
        setState(state, message, listener, false, 0);
    }

    public void setState(ProcessStateView.State state, @StringRes int message,
                         ProcessStateView.StateListener listener) {
        setState(state, getContext().getResources().getString(message), listener,
                false, 0);
    }

    /**
     * Set timeout in millisecond for counting down.
     *
     * @param enable    Enable count down if true.
     * @param timeoutMs If countdown is enabled, it will be the timeout value.
     */
    public void setState(ProcessStateView.State state, String message,
                         ProcessStateView.StateListener listener,
                         boolean enable, @IntRange(from = 0, to = 999999999L) int timeoutMs) {
        mStateOld = mState;
        mState = state;
        mMessage = message;
        mStateListener = listener;
        mEnableCountDown = state == ProcessStateView.State.STATE_PROCESSING && enable;
        mTimeoutMs = timeoutMs;
        if (this.isShowing()) {
            setState();
        }
    }

    /**
     * Set timeout in millisecond for counting down.
     *
     * @param enable    enable count down if true.
     * @param timeoutMs if countdown is enabled, it will be the timeout value.
     */
    public void setState(ProcessStateView.State state, @StringRes int message,
                         ProcessStateView.StateListener listener,
                         boolean enable, @IntRange(from = 0, to = 999999999L) int timeoutMs) {
        setState(state, getContext().getResources().getString(message), listener,
                enable, timeoutMs);
    }

    private void setState() {
        if (mState != ProcessStateView.State.STATE_PROCESSING && mStateOld == ProcessStateView.State.STATE_PROCESSING) {
            mStateView.stopStateAnia();
        }

        if (mCompositeDisposable != null && !mCompositeDisposable.isDisposed()) {
            LogUtils.d("changeState mDisposableCountDown !!!");
            mCompositeDisposable.clear();
        }

        mMsgTextView.setText(mMessage);
        mStateView.setState(mState);
        mStateView.setStateListener(mStateListener);
        mButton.setVisibility(mState == ProcessStateView.State.STATE_FAILED ?
                View.VISIBLE : View.INVISIBLE);
        if (/*mState == ProcessStateView.State.STATE_PROCESSING && */mEnableCountDown) {
            startCountDown();
        } else {
            mStateView.setText(null);
        }
        this.setCancelable(mState == ProcessStateView.State.STATE_SUCCESS);
        if (mState == ProcessStateView.State.STATE_SUCCESS) {
            mStateView.setStateImage(getContext().getDrawable(R.drawable.icon_success));
        } else if (mState == ProcessStateView.State.STATE_FAILED) {
            mStateView.setStateImage(getContext().getDrawable(R.drawable.icon_fail));
        } else if (mState == ProcessStateView.State.STATE_WARN) {
            mStateView.setStateImage(getContext().getDrawable(R.drawable.icon_warning));
        }
        mStateView.startStateAnia();
    }

    /**
     * Update progress status of progress bar according to timeout value. In default, there's a 60s
     * timeout.
     * <p>
     * Multi-Thread {@link Disposable}
     */
    private void startCountDown() {
        mTimeoutMs = mTimeoutMs <= 0 ? 60000 : mTimeoutMs;
        final int timeout = mTimeoutMs / 1000;
        mCompositeDisposable.add(Observable.intervalRange(1, timeout, 0, 1,
                TimeUnit.SECONDS)
                .takeWhile(new Predicate<Long>() {// method 1
                    @Override
                    public boolean test(Long aLong) throws Throwable {
                        /*LogUtils.d("showProgressDialog Observable: aLong= " + aLong + ", " +
                                "thread= "
                                + Thread.currentThread().getId());*/
                        return (mCompositeDisposable != null && !mCompositeDisposable.isDisposed());
                    }
                })
                .onTerminateDetach() // method 2
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        //LogUtils.d("showProgressDialog Observable: " + aLong);
                        int t = timeout - aLong.intValue();
                        if (ProcessStateDialog.this.isShowing()) {
                            mStateView.setText(t + "s");
                        }
                    }
                })
        );

    }

}
