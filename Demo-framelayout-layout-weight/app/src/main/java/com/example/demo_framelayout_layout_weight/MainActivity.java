package com.example.demo_framelayout_layout_weight;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demo_framelayout_layout_weight.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private DisplayMetrics mDisplayMetrics;
    private UIWindowState mDefaultWindowState;
    private UIWindowState mCameraVideoWindowState;
    private UIWindowState mPCVideoWindowState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // mBinding.t1.setAlpha(0.1f);
        mBinding.t1.setOpaque(true);
        mBinding.t2.setAlpha(0.5f);
        // mBinding.t2.setOpaque(true);

        mDisplayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);

        mDefaultWindowState = new UIWindowState(UIWindowState.FULL_SCREEN,
                UIWindowState.CENTER);
        mCameraVideoWindowState = new UIWindowState(UIWindowState.QUARTER_SCREEN,
                UIWindowState.START);
        mPCVideoWindowState = new UIWindowState(UIWindowState.QUARTER_SCREEN,
                UIWindowState.END);
        mBinding.ltTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mapZoomInOut();
            }
        });
        mBinding.ltFpv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fpvZoomInOut();
            }
        });

        mBinding.ltPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pCloudZoomInOut();
            }
        });
    }

    private void mapZoomInOut() {
        if (mDefaultWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            return;
        }

        if (mDefaultWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
            // full screen
            FrameLayout.LayoutParams lp =
                    (FrameLayout.LayoutParams) mBinding.ltTxt.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels;
            lp.height = mDisplayMetrics.heightPixels;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.FULL_SCREEN);

            // todo don't change location when it's already been in state QUARTER_SCREEN
            // if (mCameraVideoWindowState.getWindowState() == UIWindowState.QUARTER_SCREEN) {
            //
            // }

            // quarter screen
            lp = new FrameLayout.LayoutParams(mBinding.ltFpv.getLayoutParams());
            lp.width = mDisplayMetrics.widthPixels / 4;
            lp.height = mDisplayMetrics.heightPixels / 4;
            lp.gravity = Gravity.START | Gravity.BOTTOM;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
            // quarter screen
            lp = new FrameLayout.LayoutParams(
                    mDisplayMetrics.widthPixels / 4, mDisplayMetrics.heightPixels / 4);
            lp.gravity = Gravity.END | Gravity.BOTTOM;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);

            mBinding.ltOther.bringToFront();
        } else if (mCameraVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltFpv.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            // half screen
            lp = (FrameLayout.LayoutParams) mBinding
                    .ltTxt.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltPc.bringToFront();
        } else if (mPCVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltPc.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            lp = (FrameLayout.LayoutParams) mBinding
                    .ltTxt.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltTxt.bringToFront();
        }
    }

    private void fpvZoomInOut() {
        if (mCameraVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            return;
        }

        if (mCameraVideoWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
            // full screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding.ltFpv
                    .getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels;
            lp.height = mDisplayMetrics.heightPixels;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.FULL_SCREEN);

            // quarter screen
            lp = (FrameLayout.LayoutParams) mBinding.ltTxt
                    .getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 4;
            lp.height = mDisplayMetrics.heightPixels / 4;
            lp.gravity = Gravity.START | Gravity.BOTTOM;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
            // quarter screen
            lp = (FrameLayout.LayoutParams) mBinding.ltPc
                    .getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 4;
            lp.height = mDisplayMetrics.heightPixels / 4;
            lp.gravity = Gravity.END | Gravity.BOTTOM;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);

            mBinding.ltOther.bringToFront();
        } else if (mDefaultWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltTxt.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            // half screen
            lp = (FrameLayout.LayoutParams) mBinding
                    .ltFpv.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltPc.bringToFront();
        } else if (mPCVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltPc.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            lp = (FrameLayout.LayoutParams) mBinding
                    .ltFpv.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltTxt.bringToFront();
        }
    }

    private void pCloudZoomInOut() {
        if (mPCVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            return;
        }

        if (mPCVideoWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
            // full screen
            FrameLayout.LayoutParams lp1 = (FrameLayout.LayoutParams) mBinding
                    .ltPc.getLayoutParams();
            lp1.width = mDisplayMetrics.widthPixels;
            lp1.height = mDisplayMetrics.heightPixels;
            mBinding.ltPc.setLayoutParams(lp1);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.FULL_SCREEN);

            // quarter screen
            FrameLayout.LayoutParams lp2 = (FrameLayout.LayoutParams) mBinding.ltTxt
                    .getLayoutParams();
            lp2.width = mDisplayMetrics.widthPixels / 4;
            lp2.height = mDisplayMetrics.heightPixels / 4;
            lp2.gravity = Gravity.START | Gravity.BOTTOM;
            mBinding.ltTxt.setLayoutParams(lp2);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
            // quarter screen
            FrameLayout.LayoutParams lp3 = (FrameLayout.LayoutParams) mBinding.ltFpv
                    .getLayoutParams();
            lp3.gravity = Gravity.END | Gravity.BOTTOM;
            lp3.width = mDisplayMetrics.widthPixels / 4;
            lp3.height = mDisplayMetrics.heightPixels / 4;
            mBinding.ltFpv.setLayoutParams(lp3);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);

            mBinding.ltOther.bringToFront();
        } else if (mDefaultWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltTxt.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltTxt.setLayoutParams(lp);
            mBinding.ltTxt.bringToFront();
            mDefaultWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            // half screen
            lp = (FrameLayout.LayoutParams) mBinding
                    .ltPc.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltFpv.bringToFront();
        } else if (mCameraVideoWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
            // half screen
            FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) mBinding
                    .ltFpv.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.START | Gravity.TOP;
            mBinding.ltFpv.setLayoutParams(lp);
            mBinding.ltFpv.bringToFront();
            mCameraVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            lp = (FrameLayout.LayoutParams) mBinding
                    .ltPc.getLayoutParams();
            lp.width = mDisplayMetrics.widthPixels / 2;
            lp.height = mDisplayMetrics.heightPixels;
            lp.gravity = Gravity.END | Gravity.TOP;
            mBinding.ltPc.setLayoutParams(lp);
            mBinding.ltPc.bringToFront();
            mPCVideoWindowState.setWindowState(UIWindowState.HALF_SCREEN);

            mBinding.ltTxt.bringToFront();
        }
    }
}