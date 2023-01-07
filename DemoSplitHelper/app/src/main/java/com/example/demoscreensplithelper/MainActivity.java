/*
 * = COPYRIGHT
 *
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date	                 Author	                Action
 * 20230107 	         LiuJian                  Create
 */

package com.example.demoscreensplithelper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.demoscreensplithelper.databinding.ActivitySecondBinding;


public class MainActivity extends AppCompatActivity {
    private static final String MAP_FULL = "map full";
    private static final String PCD_FULL = "pcd full";
    private static final String FPV_FULL = "fpv full";
    private static final String MAP_HALF = "map half";
    private static final String PCD_HALF = "pcd half";
    private static final String FPV_HALF = "fpv half";
    private static final String MAP_QUARTER = "map quarter";
    private static final String PCD_QUARTER = "pcd quarter";
    private static final String FPV_QUARTER = "fpv quarter";
    private static final String MAP_CENTER = "map center";
    private static final String PCD_CENTER = "pcd center";
    private static final String FPV_CENTER = "fpv center";
    private ActivitySecondBinding mBinding;
    private ScreenSplitHelper mSplitScreenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_second);
        initHalfFullScreenLogic();
    }

    private void initHalfFullScreenLogic() {
        mSplitScreenHelper = new ScreenSplitHelper();
        WindowState mapWindowState = new WindowState(WindowState.FULL_SCREEN, WindowState.CENTER);
        WindowState fPVWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.START);
        WindowState ptcWindowState = new WindowState(WindowState.QUARTER_SCREEN, WindowState.END);
        initWindowState(mapWindowState, fPVWindowState, ptcWindowState);
        initMapZoomInOutTouchEvent(mapWindowState);
        initFpvZoomInOutTouchEvent(fPVWindowState);
        initPtcZoomInOutTouchEvent(ptcWindowState);
    }


    /**
     * 初始化分屏逻辑。
     *
     * @param mapWindowState 地图窗口的状态
     * @param fPVWindowState FPV窗口的状态
     * @param ptcWindowState 点云窗口的状态
     */
    private void initWindowState(@NonNull WindowState mapWindowState, @NonNull WindowState
            fPVWindowState, @NonNull WindowState ptcWindowState) {
        final ScreenSplitHelper.OnChangeToFull mapFull = () -> {
            mBinding.tvMapMsg.setText(MAP_FULL);
            mBinding.tvMap.setText(MAP_FULL);
            mBinding.tvFpv.setText(FPV_QUARTER);
            mBinding.tvPcd.setText(PCD_QUARTER);
            mBinding.tvMapMsg.bringToFront();
            mBinding.tvMapMsg.setVisibility(View.VISIBLE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf mapHalf = () -> {
            mBinding.tvMapMsg.bringToFront();
            mBinding.tvMapMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setText(MAP_HALF);
            mBinding.tvMap.setText(MAP_HALF);
        };
        final ScreenSplitHelper.OnChangeToQuarter mapQuarter = () -> {
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvMapMsg.setText(MAP_QUARTER);
            mBinding.tvMap.setText(MAP_QUARTER);
        };

        mapWindowState.setView(mBinding.mapContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(mapWindowState.getView().getId(), mapFull);
        mSplitScreenHelper.setOnChangeToHalf(mapWindowState.getView().getId(), mapHalf);
        mSplitScreenHelper.setOnChangeToQuarter(mapWindowState.getView().getId(), mapQuarter);

        final ScreenSplitHelper.OnChangeToFull fpvFull = () -> {
            mBinding.tvFpvMsg.setText(FPV_FULL);
            mBinding.tvFpv.setText(FPV_FULL);
            mBinding.tvMap.setText(MAP_QUARTER);
            mBinding.tvPcd.setText(PCD_QUARTER);
            mBinding.tvFpvMsg.bringToFront();
            mBinding.tvFpvMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf fpvHalf = () -> {
            mBinding.tvFpvMsg.bringToFront();
            mBinding.tvFpvMsg.setVisibility(View.VISIBLE);
            mBinding.tvFpvMsg.setText(FPV_HALF);
            mBinding.tvFpv.setText(FPV_HALF);
        };
        final ScreenSplitHelper.OnChangeToQuarter fpvQuarter = () -> {
            mBinding.tvFpvMsg.setVisibility(View.GONE);
            mBinding.tvFpvMsg.setText(FPV_QUARTER);
            mBinding.tvFpv.setText(FPV_QUARTER);
        };
        fPVWindowState.setView(mBinding.fpvContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(fPVWindowState.getView().getId(), fpvFull);
        mSplitScreenHelper.setOnChangeToHalf(fPVWindowState.getView().getId(), fpvHalf);
        mSplitScreenHelper.setOnChangeToQuarter(fPVWindowState.getView().getId(), fpvQuarter);

        final ScreenSplitHelper.OnChangeToFull pcdFull = () -> {
            mBinding.tvPcdMsg.setText(PCD_FULL);
            mBinding.tvPcd.setText(PCD_FULL);
            mBinding.tvMap.setText(MAP_QUARTER);
            mBinding.tvFpv.setText(FPV_QUARTER);
            mBinding.tvPcdMsg.bringToFront();
            mBinding.tvPcdMsg.setVisibility(View.VISIBLE);
            mBinding.tvMapMsg.setVisibility(View.GONE);
            mBinding.tvFpvMsg.setVisibility(View.GONE);
        };
        final ScreenSplitHelper.OnChangeToHalf pcdHalf = () -> {
            mBinding.tvPcdMsg.bringToFront();
            mBinding.tvPcdMsg.setVisibility(View.VISIBLE);
            mBinding.tvPcdMsg.setText(PCD_HALF);
            mBinding.tvPcd.setText(PCD_HALF);
        };
        final ScreenSplitHelper.OnChangeToQuarter pcdQuarter = () -> {
            mBinding.tvPcdMsg.setVisibility(View.GONE);
            mBinding.tvPcdMsg.setText(PCD_QUARTER);
            mBinding.tvPcd.setText(PCD_QUARTER);
        };

        ptcWindowState.setView(mBinding.pcdContentLayoutId);
        mSplitScreenHelper.setOnChangeToFull(ptcWindowState.getView().getId(), pcdFull);
        mSplitScreenHelper.setOnChangeToHalf(ptcWindowState.getView().getId(), pcdHalf);
        mSplitScreenHelper.setOnChangeToQuarter(ptcWindowState.getView().getId(), pcdQuarter);
        final ScreenSplitHelper.OnWindowSizeChanged onChangedComplete = () -> {
            // other
            if (mapWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText(MAP_CENTER);
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else if (fPVWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText(FPV_CENTER);
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else if (ptcWindowState.getState() == WindowState.FULL_SCREEN) {
                mBinding.tvTittle.setText(PCD_CENTER);
                mBinding.tvTittle.setVisibility(View.VISIBLE);
                mBinding.tvTittle.bringToFront();
            } else {
                mBinding.tvTittle.setVisibility(View.GONE);
            }
        };
        mSplitScreenHelper.setOnWindowSizeChanged(onChangedComplete);
        mSplitScreenHelper.addView(mapWindowState);
        mSplitScreenHelper.addView(fPVWindowState);
        mSplitScreenHelper.addView(ptcWindowState);
    }

    private void initMapZoomInOutTouchEvent(@NonNull final WindowState mapWindowState) {
        mBinding.mapContentLayoutId.setOnClickListener(
                v -> mSplitScreenHelper.changeWindowSize(mapWindowState
                        .getView().getId()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initFpvZoomInOutTouchEvent(@NonNull final WindowState fPVWindowState) {
        mBinding.fpvContentLayoutId.setOnClickListener(
                v -> mSplitScreenHelper.changeWindowSize(fPVWindowState
                        .getView().getId()));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initPtcZoomInOutTouchEvent(@NonNull final WindowState ptcWindowState) {
        mBinding.pcdContentLayoutId.setOnClickListener(
                v -> mSplitScreenHelper.changeWindowSize(ptcWindowState
                        .getView().getId()));
    }
}