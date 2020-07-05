/*
 * = COPYRIGHT
 *          Wuhan Tianyu
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20200617 	        liujian                  Create
 */

package com.example.democircleprogressbar.dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;

import com.example.democircleprogressbar.LogUtils;
import com.example.democircleprogressbar.R;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class SecondActivity extends BasicActivity {
    private static final String TAG = "SecondActivity";
    private static final String ID_OPERATOR_SIGNED_ON = "POSSignOnActivity_ID_OPERATOR_SIGNED_ON";
    private CompositeDisposable mDisposable;
    private Presenter mPosSignOnPresenter;
    private CustomProgressDialog mProgressDialog;

    /**
     * how to transfer operatorId? save in shared preference or transfer parameter from one
     * activity to the other activity?
     */
    public static void startPOSSignOnActivity(@NonNull Context context,
                                              @NonNull String operatorId) {
        Intent intent = new Intent(context, SecondActivity.class);
        intent.putExtra(ID_OPERATOR_SIGNED_ON, operatorId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //nothing
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDisposable != null) {
            mDisposable.clear();
        }
    }

    @Override
    protected int setLayoutId() {
        return R.layout.activity_second;
    }

    @Override
    protected void initData() {
        mDisposable = new CompositeDisposable();
    }

    @Override
    protected void initView() {
        //nothing
        initProgressDialog();
    }

    @Override
    protected void afterViewInit() {
        Disposable disposable = Observable.create(new ObservableOnSubscribe<Boolean>() {
            @Override
            public void subscribe(ObservableEmitter<Boolean> emitter) throws Exception {
                mPosSignOnPresenter = new Presenter();
                if (!emitter.isDisposed()) {
                    emitter.onNext(true);
                    emitter.onComplete();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        mProgressDialog.showProgressDialog();
                        mPosSignOnPresenter.doProcess(new SecondActivityListener() {
                            @Override
                            public void showDialog(String prompt) {
                                mProgressDialog.updateTittleText(prompt);
                            }

                            @Override
                            public void dismissDialog() {
                                if (mProgressDialog.isShowing()) {
                                    LogUtils.d("dismissLoadingDialog");
                                    mProgressDialog.dismiss();
                                }
                            }
                        });
                    }
                });
        mDisposable.add(disposable);
    }

    /**
     * todo: Get value from setting parameters.
     *
     * @return timeout value
     */
    private int getTxnTimeout() {
        return 60000;
    }

    /**
     * Initialize progress dialog
     */
    private void initProgressDialog() {
        mProgressDialog = new CustomProgressDialog(SecondActivity.this);
        mProgressDialog.setTimeoutMs(getTxnTimeout());
        mProgressDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                LogUtils.d("setOnDismissListener.onDismiss thread id: "
                        + Thread.currentThread().getId());
            }
        });
        mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                LogUtils.d("setOnDismissListener.onCancel thread id: "
                        + Thread.currentThread().getId());
            }
        });
    }
}
