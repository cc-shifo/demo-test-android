/*
 * = COPYRIGHT
 *          xxxx
 * Description: // Detail description about the function of this module,
 *             // interfaces with the other modules, and dependencies.
 * Revision History:
 * Date                    Author                  Action
 * 2022-11-09              LiuJian                 Create
 */

package com.example.demoimmerse;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.zhd.hiair.libbaseui.BaseFragment;
import com.zhd.hiair.modulemoresetting.battery.BatteryFragment;
import com.zhd.hiair.modulemoresetting.databinding.LayoutMoreSettingDialogBinding;
import com.zhd.hiair.modulemoresetting.fc.FCParamSettingFragment;
import com.zhd.hiair.modulemoresetting.lidar.LidarFragment;
import com.zhd.hiair.modulemoresetting.obst.ObstacleFragment;
import com.zhd.hiair.modulemoresetting.rc.RCFragment;
import com.zhd.hiair.modulemoresetting.rtk.RtkFragment;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

/**
 * 状态栏下的更多设置业务入口
 * <p>
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class TestMoreSettingFragment extends BaseFragment<LayoutMoreSettingDialogBinding> {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_SELECTED = "ARG_SELECTED";

    private MoreSettingViewModel mVM;

    /**
     * 默认位置。
     */
    private int mSelected;
    private static final int NO_SELECTED = -1;

    public TestMoreSettingFragment() {
        // Required empty public constructor
        mSelected = NO_SELECTED;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            mSelected = bundle.getInt(ARG_SELECTED);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        mVM = new ViewModelProvider(this).get(MoreSettingViewModel.class);
        return view;
    }


    @Override
    protected int initLayoutId() {
        return R.layout.layout_more_setting_dialog;
    }

    @Override
    protected void initViewData(@NonNull View view) {

    }

    @Override
    protected void initView(@NonNull View view) {
        initVP();
        bindTabAndVP2();
    }

    /**
     * 初始化vp
     */
    private List<Fragment> fragments;
    private FragmentTransaction mFragmentTransaction;
    private void initVP() {
        fragments = new ArrayList<>(6);
        fragments.add(new FCParamSettingFragment());
        fragments.add(new ObstacleFragment());
        fragments.add(new RCFragment());
        fragments.add(new BatteryFragment());
        fragments.add(new LidarFragment());
        fragments.add(new RtkFragment());
        // View child = mBinding.moreSettingDialogContentVp2.getChildAt(0);
        // if (child instanceof RecyclerView) {
        //     child.setOverScrollMode(View.OVER_SCROLL_NEVER);
        // }
        //
        // FragAdapter fragAdapter = new FragAdapter(TestMoreSettingFragment.this, fragments);
        // mBinding.moreSettingDialogContentVp2.setUserInputEnabled(false);
        // mBinding.moreSettingDialogContentVp2.setOffscreenPageLimit(1);
        // mBinding.moreSettingDialogContentVp2.setAdapter(fragAdapter);
        // mFragmentTransaction = getChildFragmentManager().beginTransaction();
        // mFragmentTransaction.add(mBinding.moreSettingContentFrag.getId(), new FCParamSettingFragment())
        //         .add(mBinding.moreSettingContentFrag.getId(), new ObstacleFragment())
        //         .add(mBinding.moreSettingContentFrag.getId(), new RCFragment())
        //         .add(mBinding.moreSettingContentFrag.getId(), new BatteryFragment())
        //         .add(mBinding.moreSettingContentFrag.getId(), new LidarFragment())
        //         .add(mBinding.moreSettingContentFrag.getId(), new RtkFragment());
    }

    /**
     * 将tab与设置子项关联。
     */
    private void bindTabAndVP2() {
        mBinding.moreSettingDialogRv.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                       @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                if (parent.getChildAdapterPosition(view) == 0) {
                    outRect.top = (int) getResources().getDimension(R.dimen
                            .more_setting_dialog_rv_first_item_top_margin);
                } else {
                    outRect.top = (int) getResources().getDimension(R.dimen
                            .more_setting_dialog_rv_item_decor_height);
                }
            }
        });
        final MoreSettingRvAdapter adapter = new MoreSettingRvAdapter(this::setCurrentPageItem);
        mBinding.moreSettingDialogRv.setAdapter(adapter);
        mVM.observeTabItems().observe(this, tabItemData -> {
            adapter.updateInfoList(tabItemData);
            if (mSelected >= adapter.getItemCount()) {
                throw new InvalidParameterException("invalid selected tab index: " + mSelected);
            }
            adapter.selectTab(0);
            setCurrentPageItem(0);
        });
        mVM.loadTabItems(requireContext());
    }

    private void setCurrentPageItem(@IntRange(from = 0) int adapterPosition) {
        getChildFragmentManager().beginTransaction()
                .replace(mBinding.moreSettingContentFrag.getId(), fragments.get(adapterPosition))
                .commit();
    }
}