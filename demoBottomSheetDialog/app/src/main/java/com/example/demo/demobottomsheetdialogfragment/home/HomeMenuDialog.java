package com.example.demo.demobottomsheetdialogfragment.home;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.example.demo.demobottomsheetdialogfragment.BottomSheetDialogUtils2;
import com.example.demo.demobottomsheetdialogfragment.BottomSheetDialogUtils3;
import com.example.demo.demobottomsheetdialogfragment.R;

import java.util.ArrayList;
import java.util.List;

public class HomeMenuDialog extends BottomSheetDialogUtils3 {
    private static final String TAG = "HomeMenuDialog";
    private ViewPager mViewPager;
    /**
     * indicator
     */
    private LinearLayout mIndicatorLinearLayout;
    private int mCurrentPagePosition;

    private int mColumn;
    private int mRow;
    private List<HomeMenuItemData> mItemDataList;

    public HomeMenuDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected int setLayoutId() {
                return R.layout.activity_home_menu_dialog;
//        return R.layout.activity_home_menu_dialog_linear;
    }

    @Override
    protected void initData() {
        loadData();
    }

    private void loadData() {
        mItemDataList = new ArrayList<>();
        Resources res = getContext().getResources();
        mColumn = res.getInteger(R.integer.home_menu_column);
        mRow = res.getInteger(R.integer.home_menu_row);
        String[] labels = res.getStringArray(R.array.home_menu_labels);
        String[] actions = res.getStringArray(R.array.home_menu_onclick_actions);

        TypedArray icons = null;
        try {
            icons = res.obtainTypedArray(R.array.home_menu_icons);
            for (int i = 0; i < labels.length; i++) {
                HomeMenuItemData itemData = new HomeMenuItemData(icons.getResourceId(i,
                        R.drawable.home_menu_item_pos_sign_on), labels[i], actions[i]);
                mItemDataList.add(itemData);
            }
        } catch (Exception e) {
            Log.d(TAG, "loadData: " + e);
        } finally {
            if (icons != null)
                icons.recycle();
        }
    }

    @Override
    protected void iniView() {
        super.iniView();
        inflateViewPager();
    }

    private void inflateViewPager() {
        mViewPager = getView().findViewById(R.id.home_menu_dialog_vpg);
        mIndicatorLinearLayout = getView().findViewById(R.id.home_view_page_indicator_lt);
        int pageItemNum = mColumn * mRow;
        int pageNum = (mItemDataList.size() + pageItemNum - 1) / pageItemNum;
        List<RecyclerView> recyclerViewList = new ArrayList<>();
        for (int i = 0; i < pageNum; i++) {
            RecyclerView recyclerView = new RecyclerView(getContext());
            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams(ViewGroup
                    .LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            recyclerView.setLayoutParams(params);
            /*LayoutInflater inflater = LayoutInflater.from(getContext());
            RecyclerView recyclerView = (RecyclerView)inflater.inflate(R.layout
            .activity_home_menu_recycler_view,
                    mViewPager, false);*/
            recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
            RecyclerView.LayoutManager layoutManager = new CustomGridLayoutManager(getContext(),
                    mColumn);
            recyclerView.setLayoutManager(layoutManager);
            List<HomeMenuItemData> list;
            if (i == pageNum - 1) {
                list = mItemDataList.subList(pageItemNum * i, mItemDataList.size());
            } else {
                list = mItemDataList.subList(pageItemNum * i, pageItemNum * i + pageItemNum);
            }
            HomeMenuRecyclerAdapter adapter = new HomeMenuRecyclerAdapter(list);
            adapter.setClickListener(new HomeMenuRecyclerAdapter.ItemClickListener() {
                @Override
                public void onClick(HomeMenuItemData item) {
                    try {
                        Class<?> cls = Class.forName(item.getComponentClass());
                        Intent intent = new Intent(getContext(), cls);
                        getContext().startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        Log.d(TAG, "onClick: " + e);
                    }
                }
            });
            recyclerView.setAdapter(adapter);
            recyclerView.addItemDecoration(new GridDividerItemDecoration(getContext(),
                    0, 0, 0));
            recyclerViewList.add(recyclerView);
        }
        mViewPager.setAdapter(new HomeMenuVPAdapter<>(recyclerViewList));
        addIndicator(recyclerViewList.size());
    }

    @SuppressLint("InflateParams")
    private void addIndicator(int pageCount) {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        for (int i = 0; i < pageCount; i++) {
            mIndicatorLinearLayout.addView(inflater.inflate(R.layout.activity_home_menu_indicator,
                    mIndicatorLinearLayout, false));
        }

        // 默认显示第一页
        mCurrentPagePosition = 0;
        mIndicatorLinearLayout.getChildAt(0).setActivated(true);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            int prePagePosition = 0;

            public void onPageSelected(int position) {
                // 取消选中
                prePagePosition = mCurrentPagePosition;
                mCurrentPagePosition = position;
                if (prePagePosition != mCurrentPagePosition) {
                    mIndicatorLinearLayout.getChildAt(prePagePosition)
                            .findViewById(R.id.v_dot).setActivated(false);
                }
                // 选中圆点
                mIndicatorLinearLayout.getChildAt(mCurrentPagePosition)
                        .findViewById(R.id.v_dot).setActivated(true);

            }

            public void onPageScrolled(int arg0, float arg1, int arg2) {
                //nothing to do
            }

            public void onPageScrollStateChanged(int arg0) {
                //nothing to do
            }
        });
        mViewPager.setCurrentItem(mCurrentPagePosition);
    }

}
