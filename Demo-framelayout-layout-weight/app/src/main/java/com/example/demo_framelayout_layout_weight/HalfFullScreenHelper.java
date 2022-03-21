package com.example.demo_framelayout_layout_weight;

public class HalfFullScreenHelper {
    // private void mapZoomInOut() {
    //     // todo set some views to be invisible
    //     if (mMapWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
    //         // map full screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mMapWindowState.setWindowState(UIWindowState.FULL_SCREEN);
    //
    //         // fpv quarter screen
    //         // TODO remember the first window
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainFpvId.bringToFront();
    //         mFPVWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // ptc quarter screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams
    //                 .MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams
    //                 .MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainPointCloudId.bringToFront();
    //         mPtcWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         // TODO set visible for other view
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //             if (mBinding.btnMapEditSaveGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnEditParameters.bringToFront();
    //                 mBinding.btnSaveParameters.bringToFront();
    //             }
    //         }
    //     } else if (mFPVWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // map half screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams
    //                         .MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainFpvId.getId();
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mMapWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // fpv half screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainSceneView.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         mFPVWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // ptc
    //         mBinding.activityFlyModeMainPointCloudId.bringToFront();
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         // TODO set visible for other view
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //             if (mBinding.btnMapEditSaveGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnEditParameters.bringToFront();
    //                 mBinding.btnSaveParameters.bringToFront();
    //             }
    //         }
    //     } else if (mPtcWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // map half screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainPointCloudId.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mMapWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // ptc half screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainSceneView.getId();
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mPtcWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // fpv quarter screen
    //         mBinding.activityFlyModeMainFpvId.bringToFront();
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         // TODO set visible for other view
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //             if (mBinding.btnMapEditSaveGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnEditParameters.bringToFront();
    //                 mBinding.btnSaveParameters.bringToFront();
    //             }
    //         }
    //     }
    // }
    //
    // private void fpvZoomInOut() {
    //     // todo set some views to be invisible
    //     if (mFPVWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
    //         // fpv full screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         mFPVWindowState.setWindowState(UIWindowState.FULL_SCREEN);
    //
    //         // map quarter screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainSceneView.bringToFront();
    //         mMapWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // ptc quarter screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainPointCloudId.bringToFront();
    //         mPtcWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     } else if (mMapWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // fpv half screen
    //         ConstraintLayout.LayoutParams layoutParams =
    //                 new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams
    //                         .MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainSceneView.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         // mBinding.activityFlyModeMainFpvId.bringToFront();
    //         mFPVWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // map half screen
    //         layoutParams =
    //                 new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams
    //                         .MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainFpvId.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         // mBinding.activityFlyModeMainSceneView.bringToFront();
    //         mMapWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // ptc quarter screen
    //         mBinding.activityFlyModeMainPointCloudId.bringToFront();
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     } else if (mPtcWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // fpv half screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainPointCloudId.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         // mBinding.activityFlyModeMainFpvId.bringToFront();
    //         mFPVWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // ptc half screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         /*layoutParams.matchConstraintPercentWidth = 0.5f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;*/
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainFpvId.getId();
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         // mBinding.activityFlyModeMainPointCloudId.bringToFront();
    //         mPtcWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // map quarter screen
    //         mBinding.activityFlyModeMainSceneView.bringToFront();
    //
    //         // other
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     }
    // }
    //
    // private void ptCloudZoomInOut() {
    //     if (mPtcWindowState.getWindowState() == UIWindowState.HALF_SCREEN) {
    //         // ptc full screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mPtcWindowState.setWindowState(UIWindowState.FULL_SCREEN);
    //
    //         // map quarter screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainSceneView.bringToFront();
    //         mMapWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // fpv quarter screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.matchConstraintPercentWidth = 0.25f;
    //         layoutParams.matchConstraintDefaultWidth =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         layoutParams.matchConstraintPercentHeight = 0.25f;
    //         layoutParams.matchConstraintDefaultHeight =
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
    //         // TODO remember the first window
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         mBinding.activityFlyModeMainFpvId.bringToFront();
    //         mFPVWindowState.setWindowState(UIWindowState.QUARTER_SCREEN);
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     } else if (mMapWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // ptc half screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainSceneView.getId();
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mPtcWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // map half screen
    //         layoutParams =
    //                 new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams
    //                         .MATCH_CONSTRAINT, ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainPointCloudId.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainSceneView.setLayoutParams(layoutParams);
    //         mMapWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // quarter fpv
    //         mBinding.activityFlyModeMainFpvId.bringToFront();
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     } else if (mFPVWindowState.getWindowState() == UIWindowState.FULL_SCREEN) {
    //         // ptc half screen
    //         ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToEnd = mBinding.activityFlyModeMainFpvId.getId();
    //         layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainPointCloudId.setLayoutParams(layoutParams);
    //         mPtcWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // fpv half screen
    //         layoutParams = new ConstraintLayout.LayoutParams(
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
    //                 ConstraintLayout.LayoutParams.MATCH_CONSTRAINT);
    //         layoutParams.horizontalWeight = 1;
    //         layoutParams.verticalWeight = 1;
    //         layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.endToStart = mBinding.activityFlyModeMainPointCloudId.getId();
    //         layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
    //         layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
    //         mBinding.activityFlyModeMainFpvId.setLayoutParams(layoutParams);
    //         mFPVWindowState.setWindowState(UIWindowState.HALF_SCREEN);
    //
    //         // map quarter screen
    //         mBinding.activityFlyModeMainSceneView.bringToFront();
    //
    //         // other
    //         // mBinding.statusBarMsg.bringToFront();
    //         mBinding.statusBarMsg.getRoot().bringToFront();
    //         mBinding.btnFlyControl.bringToFront();
    //         mBinding.btnFlyReturnHome.bringToFront();
    //         mBinding.activityFlyModeMainTopEndPanelId.getRoot().bringToFront();
    //         mBinding.Compass.bringToFront();
    //
    //         mBinding.btnFirstControl.bringToFront();
    //         mBinding.btnSecondControl.bringToFront();
    //
    //         if (!mFlyManually) {
    //             if (mBinding.btnMapClearDeleteGroup.getVisibility() == View.VISIBLE) {
    //                 mBinding.btnMapClearOverlay.bringToFront();
    //                 mBinding.btnMapDeleteOneFlightPoint.bringToFront();
    //             }
    //
    //             mBinding.btnEditParameters.bringToFront();
    //             mBinding.btnSaveParameters.bringToFront();
    //         }
    //     }
    // }
}
