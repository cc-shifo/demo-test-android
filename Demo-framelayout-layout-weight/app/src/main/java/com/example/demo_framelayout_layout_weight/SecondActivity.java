package com.example.demo_framelayout_layout_weight;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demo_framelayout_layout_weight.databinding.ActivitySecondBinding;

public class SecondActivity extends AppCompatActivity {
    private ActivitySecondBinding mBinding;

    private WindowState mMapWindowState;
    private WindowState mFPVWindowState;
    private WindowState mPtcWindowState;
    private ScreenSplitHelper mSplitScreenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initHalfFullScreenLogic();
    }

    private void initHalfFullScreenLogic() {
        initWindowState();
        initMapZoomInOutTouchEvent();
        initFpvZoomInOutTouchEvent();
        initPtcZoomInOutTouchEvent();
    }


    private void initWindowState() {

        mSplitScreenHelper = new ScreenSplitHelper();
        final ScreenSplitHelper.OnChangeToFull mapFull = () -> {
            mBinding.tvMapMsg.setText("map full");
            mBinding.tvMap.setText("map full");
            mBinding.tvFpv.setText("fpv quarter");
            mBinding.tvPcd.setText("pcd quarter");
            mBinding.tvMapMsg.bringToFront();
            mBinding.tvMapMsg.setVisibility(View.VISIBLE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf mapHalf = () -> {
            mBinding.tvMapMsg.bringToFront();
            mBinding.tvMapMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setText("map half");
            mBinding.tvMap.setText("map half");
        };
        final ScreenSplitHelper.OnChangeToQuarter mapQuarter = () -> {
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvMapMsg.setText("map quarter");
            mBinding.tvMap.setText("map quarter");
        };

        mMapWindowState = new WindowState(WindowState.FULL_SCREEN, WindowState.CENTER);
        mMapWindowState.setView(mBinding.mapContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mMapWindowState.getView().getId(), mapFull);
        mSplitScreenHelper.setOnChangeToHalf(mMapWindowState.getView().getId(), mapHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mMapWindowState.getView().getId(), mapQuarter);

        final ScreenSplitHelper.OnChangeToFull fpvFull = () -> {
            mBinding.tvFpvMsg.setText("fpv full");
            mBinding.tvFpv.setText("fpv full");
            mBinding.tvMap.setText("map quarter");
            mBinding.tvPcd.setText("pcd quarter");
            mBinding.tvFpvMsg.bringToFront();
            mBinding.tvFpvMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf fpvHalf = () -> {
            mBinding.tvFpvMsg.bringToFront();
            mBinding.tvFpvMsg.setVisibility(View.VISIBLE);
            mBinding.tvFpvMsg.setText("fpv half");
            mBinding.tvFpv.setText("fpv half");
        };
        final ScreenSplitHelper.OnChangeToQuarter fpvQuarter = () -> {
            mBinding.tvFpvMsg.setVisibility(View.GONE);
            mBinding.tvFpvMsg.setText("fpv quarter");
            mBinding.tvFpv.setText("fpv quarter");
        };
        mFPVWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.START);
        mFPVWindowState.setView(mBinding.fpvContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mFPVWindowState.getView().getId(), fpvFull);
        mSplitScreenHelper.setOnChangeToHalf(mFPVWindowState.getView().getId(), fpvHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mFPVWindowState.getView().getId(), fpvQuarter);

        final ScreenSplitHelper.OnChangeToFull pcdFull = () -> {
            mBinding.tvPcdMsg.setText("pcd full");
            mBinding.tvPcd.setText("pcd full");
            mBinding.tvMap.setText("map quarter");
            mBinding.tvFpv.setText("fpv quarter");
            mBinding.tvPcdMsg.bringToFront();
            mBinding.tvPcdMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf pcdHalf = () -> {
            mBinding.tvPcdMsg.bringToFront();
            mBinding.tvPcdMsg.setVisibility(View.VISIBLE);
            mBinding.tvPcdMsg.setText("pcd half");
            mBinding.tvPcd.setText("pcd half");
        };
        final ScreenSplitHelper.OnChangeToQuarter pcdQuarter = () -> {
            mBinding.tvPcdMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setText("pcd quarter");
            mBinding.tvPcd.setText("pcd quarter");
        };

        mPtcWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.END);
        mPtcWindowState.setView(mBinding.pcdContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mPtcWindowState.getView().getId(), pcdFull);
        mSplitScreenHelper.setOnChangeToHalf(mPtcWindowState.getView().getId(), pcdHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mPtcWindowState.getView().getId(), pcdQuarter);
        final ScreenSplitHelper.OnChangedComplete onChangedComplete = () -> {
            // other
            if (mMapWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText("map center");
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else if (mFPVWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText("fpv center");
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else if (mPtcWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText("ptc center");
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else {
                mBinding.tvTittle.setVisibility(View.GONE);
            }
        };
        mSplitScreenHelper.setOnChangeComplete(onChangedComplete);
        mSplitScreenHelper.addView(mMapWindowState);
        mSplitScreenHelper.addView(mFPVWindowState);
        mSplitScreenHelper.addView(mPtcWindowState);
    }

    private void initMapZoomInOutTouchEvent() {
        mBinding.mapContentLayoutId.setOnClickListener(v -> mSplitScreenHelper.zoomInOut(mMapWindowState
                .getView().getId()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFpvZoomInOutTouchEvent() {
        mBinding.fpvContentLayoutId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSplitScreenHelper.zoomInOut(mFPVWindowState
                        .getView().getId());
            }
        });
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPtcZoomInOutTouchEvent() {
        mBinding.pcdContentLayoutId.setOnClickListener(v -> mSplitScreenHelper.zoomInOut(mPtcWindowState
                .getView().getId()));
    }
}