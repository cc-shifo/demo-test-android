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
        final ScreenSplitHelper.OnChangeComplete mapComplete = () -> {
            // other
            mBinding.tvMapMsg.bringToFront();
            mBinding.tvMapMsg.setVisibility(View.VISIBLE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToFull mapFull = () -> {
            mBinding.tvMapMsg.setText("map full");
            mBinding.tvMap.setText("map full");
            mBinding.tvFpv.setText("fpv quarter");
            mBinding.tvPcd.setText("pcd quarter");
        };
        final ScreenSplitHelper.OnChangeToHalf mapHalf = () -> {
            mBinding.tvMapMsg.setText("map half");
            mBinding.tvMap.setText("map half");
        };
        final ScreenSplitHelper.OnChangeToQuarter mapQuarter = () -> {
            mBinding.tvMapMsg.setText("map quarter");
            mBinding.tvMap.setText("map quarter");
        };

        mMapWindowState = new WindowState(WindowState.FULL_SCREEN, WindowState.CENTER);
        mMapWindowState.setView(mBinding.mapContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mMapWindowState.getView().getId(), mapFull);
        mSplitScreenHelper.setOnChangeToHalf(mMapWindowState.getView().getId(), mapHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mMapWindowState.getView().getId(), mapQuarter);
        mSplitScreenHelper.setOnChangeComplete(mMapWindowState.getView().getId(), mapComplete);

        final ScreenSplitHelper.OnChangeComplete fpvComplete = () -> {
            mBinding.tvFpvMsg.bringToFront();
            mBinding.tvFpvMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToFull fpvFull = () -> {
            mBinding.tvFpvMsg.setText("fpv full");
            mBinding.tvFpv.setText("fpv full");
            mBinding.tvMap.setText("map quarter");
            mBinding.tvPcd.setText("pcd quarter");
        };
        final ScreenSplitHelper.OnChangeToHalf fpvHalf = () -> {
            mBinding.tvFpvMsg.setText("fpv half");
            mBinding.tvFpv.setText("fpv half");
        };
        final ScreenSplitHelper.OnChangeToQuarter fpvQuarter = () -> {
            mBinding.tvFpvMsg.setText("fpv quarter");
            mBinding.tvFpv.setText("fpv quarter");
        };
        mFPVWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.START);
        mFPVWindowState.setView(mBinding.fpvContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mFPVWindowState.getView().getId(), fpvFull);
        mSplitScreenHelper.setOnChangeToHalf(mFPVWindowState.getView().getId(), fpvHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mFPVWindowState.getView().getId(), fpvQuarter);
        mSplitScreenHelper.setOnChangeComplete(mFPVWindowState.getView().getId(), fpvComplete);

        final ScreenSplitHelper.OnChangeComplete pcdComplete = () -> {
            mBinding.tvPcdMsg.bringToFront();
            mBinding.tvPcdMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToFull pcdFull = () -> {
            mBinding.tvPcdMsg.setText("pcd full");
            mBinding.tvPcd.setText("pcd full");
            mBinding.tvMap.setText("map quarter");
            mBinding.tvFpv.setText("fpv quarter");
        };
        final ScreenSplitHelper.OnChangeToHalf pcdHalf = () -> {
            mBinding.tvPcdMsg.setText("pcd half");
            mBinding.tvPcd.setText("pcd half");
        };
        final ScreenSplitHelper.OnChangeToQuarter pcdQuarter = () -> {
            mBinding.tvPcdMsg.setText("pcd quarter");
            mBinding.tvPcd.setText("pcd quarter");
        };

        mPtcWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.END);
        mPtcWindowState.setView(mBinding.pcdContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mPtcWindowState.getView().getId(), pcdFull);
        mSplitScreenHelper.setOnChangeToHalf(mPtcWindowState.getView().getId(), pcdHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mPtcWindowState.getView().getId(), pcdQuarter);
        mSplitScreenHelper.setOnChangeComplete(mPtcWindowState.getView().getId(), pcdComplete);
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