package com.example.demo_framelayout_layout_weight;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;

import com.example.demo_framelayout_layout_weight.databinding.ActivityViewInflateBinding;

import java.util.Random;

public class ViewInflateActivity extends AppCompatActivity {
    private ActivityViewInflateBinding mBinding;
    private ConstraintLayout mConstraintLayout; // cache the ConstraintLayout;
    private ConstraintSet mConstraintSet1;
    private ConstraintSet mConstraintSet2;
    private String mText;
    private Random mRandom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_view_inflate);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_view_inflate);
        mConstraintLayout = (ConstraintLayout) mBinding.getRoot();
        mConstraintSet1 = new ConstraintSet();
        mConstraintSet2 = new ConstraintSet();
        mConstraintSet1.clone(mConstraintLayout);
        mConstraintSet2.clone(this, R.layout.activity_view_inflate);
        mText = mBinding.btnChangeLayoutParam.getText().toString();
        mRandom = new Random(5);
        mBinding.btnAdd1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView1();
            }
        });
        mBinding.btnAdd2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView2();
            }
        });
        mBinding.btnAdd3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView3();
            }
        });
        mBinding.btnAdd4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView4();
            }
        });
        mBinding.btnAdd5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addView5();
            }
        });

        mBinding.btnChangeLayoutParam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeLayoutParam();
            }
        });
    }

    /**
     * ConstraintLayout.LayoutParams 固定宽高
     */
    private void addView1() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                240, 60);
        // layoutParams.height = 60;
        // layoutParams.width = 120;
        // layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        // layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        // layoutParams.matchConstraintPercentWidth = 0.25f;
        // layoutParams.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams
        // .MATCH_CONSTRAINT_PERCENT;
        // layoutParams.matchConstraintPercentHeight = 0.25f;
        // layoutParams.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams
        // .MATCH_CONSTRAINT_PERCENT;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TextView textView = new TextView(this);
        int id = View.generateViewId();
        textView.setId(id);
        textView.setText("addView1");
        textView.setTextSize(36);
        textView.setTextColor(getColor(R.color.purple_500));
        textView.setLayoutParams(layoutParams);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
        //         ConstraintSet.START);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
        //         ConstraintSet.BOTTOM);
        ((ConstraintLayout) mBinding.getRoot()).addView(textView);
        // mConstraintSet1.applyTo(mConstraintLayout);
    }


    private void addView2() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                mConstraintLayout.getLayoutParams());
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.matchConstraintPercentWidth = 0.25f;
        layoutParams.matchConstraintDefaultWidth =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.matchConstraintPercentHeight = 0.25f;
        layoutParams.matchConstraintDefaultHeight =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TextView textView = new TextView(this);
        int id = View.generateViewId();
        textView.setId(id);
        textView.setText("addView2");
        textView.setTextSize(24);
        textView.setBackgroundColor(getColor(R.color.purple_500));
        textView.setTextColor(getColor(R.color.white));
        textView.setLayoutParams(layoutParams);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
        //         ConstraintSet.START);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
        //         ConstraintSet.BOTTOM);
        mConstraintLayout.addView(textView);
        // mConstraintSet1.applyTo(mConstraintLayout);
    }

    private void addView3() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                mConstraintLayout.getLayoutParams());
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.matchConstraintPercentWidth = 0.5f;
        layoutParams.matchConstraintDefaultWidth =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.matchConstraintPercentHeight = 0.5f;
        layoutParams.matchConstraintDefaultHeight =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
        TextView textView = new TextView(this);
        textView.setId(View.generateViewId());
        textView.setText("addView3");
        textView.setTextSize(24);
        textView.setBackgroundColor(getColor(R.color.teal_200));
        textView.setTextColor(getColor(R.color.white));
        textView.setLayoutParams(layoutParams);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
        //         ConstraintSet.START);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
        //         ConstraintSet.BOTTOM);
        mConstraintLayout.addView(textView);
        // mConstraintSet1.applyTo(mConstraintLayout);
    }

    // 使用ids资源文件之中的id。
    private void addView4() {
        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                mConstraintLayout.getLayoutParams());
        layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        layoutParams.matchConstraintPercentWidth = 0.25f;
        layoutParams.matchConstraintDefaultWidth =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.matchConstraintPercentHeight = 0.25f;
        layoutParams.matchConstraintDefaultHeight =
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
        layoutParams.startToEnd = R.id.btn_add1;
        layoutParams.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
        TextView textView = new TextView(this);
        // 编译时ID
        textView.setId(R.id.id_generated_on_build_add_tv_5);
        String id = "id=" + R.id.id_generated_on_build_add_tv_5;
        textView.setText(id);
        textView.setTextSize(24);
        textView.setBackgroundColor(getColor(R.color.black));
        textView.setTextColor(getColor(R.color.white));
        textView.setLayoutParams(layoutParams);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.START, ConstraintSet.PARENT_ID,
        //         ConstraintSet.START);
        // mConstraintSet1.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
        //         ConstraintSet.BOTTOM);
        mConstraintLayout.addView(textView);
        // mConstraintSet1.applyTo(mConstraintLayout);
    }

    private void addView5() {
        // ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
        //         mConstraintLayout.getLayoutParams());
        // layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        // layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
        // layoutParams.matchConstraintPercentWidth = 0.15f;
        // layoutParams.matchConstraintDefaultWidth = ConstraintLayout.LayoutParams
        // .MATCH_CONSTRAINT_PERCENT;
        // layoutParams.matchConstraintPercentHeight = 0.15f;
        // layoutParams.matchConstraintDefaultHeight = ConstraintLayout.LayoutParams
        // .MATCH_CONSTRAINT_PERCENT;
        // layoutParams.startToStart = ConstraintSet.PARENT_ID;
        // layoutParams.endToEnd = ConstraintSet.PARENT_ID;
        // layoutParams.topToTop = ConstraintSet.PARENT_ID;
        // layoutParams.bottomToBottom = ConstraintSet.PARENT_ID;
        TextView textView = new TextView(this);
        textView.setId(View.generateViewId());
        textView.setText("addView5");
        textView.setTextSize(16);
        textView.setGravity(Gravity.CENTER);
        textView.setBackgroundColor(getColor(R.color.black));
        textView.setTextColor(getColor(R.color.white));
        // textView.setLayoutParams(layoutParams);

        mConstraintSet1.constrainPercentWidth(textView.getId(), 0.25f);
        mConstraintSet1.constrainHeight(textView.getId(), ConstraintSet.WRAP_CONTENT);
        mConstraintSet1.connect(textView.getId(), ConstraintSet.START, mBinding.btnAdd5.getId(),
                ConstraintSet.START);
        mConstraintSet1.connect(textView.getId(), ConstraintSet.TOP, ConstraintSet.PARENT_ID,
                ConstraintSet.TOP);
        mConstraintSet1.connect(textView.getId(), ConstraintSet.BOTTOM, ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM);

        mConstraintLayout.addView(textView);
        // 添加过度动画效果，不用加也可以显示view。
        // TransitionManager.beginDelayedTransition(mConstraintLayout);
        mConstraintSet1.applyTo(mConstraintLayout);
    }


    // 修改btnChangeLayoutParam按键的对齐方式，以及大小。
    private void changeLayoutParam() {
        // 百分比 宽，高
        int n = mRandom.nextInt();
        if (n == 2) {
            // applyToLayoutParams
            mConstraintSet1.clear(mBinding.btnChangeLayoutParam.getId());
            mConstraintSet2.clear(mBinding.btnChangeLayoutParam.getId());
            mConstraintSet2.constrainPercentWidth(mBinding.btnChangeLayoutParam.getId(), 0.15f);
            mConstraintSet2.constrainPercentWidth(mBinding.btnChangeLayoutParam.getId(), 0.15f);
            mConstraintSet2.connect(mBinding.btnChangeLayoutParam.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END);
            mConstraintSet2.connect(mBinding.btnChangeLayoutParam.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP);

            String txt = mText + "百分比宽高";
            mBinding.btnChangeLayoutParam.setText(txt);
            mConstraintSet2.applyTo(mConstraintLayout);
        } else if (n == 3) {
            // applyToLayoutParams
            mConstraintSet1.clear(mBinding.btnChangeLayoutParam.getId());
            mConstraintSet2.clear(mBinding.btnChangeLayoutParam.getId());
            mConstraintSet2.constrainWidth(mBinding.btnChangeLayoutParam.getId(),
                    ConstraintSet.WRAP_CONTENT);
            mConstraintSet2.constrainHeight(mBinding.btnChangeLayoutParam.getId(),
                    ConstraintSet.WRAP_CONTENT);
            mConstraintSet2.connect(mBinding.btnChangeLayoutParam.getId(), ConstraintSet.END,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.END);
            mConstraintSet2.connect(mBinding.btnChangeLayoutParam.getId(), ConstraintSet.TOP,
                    ConstraintSet.PARENT_ID,
                    ConstraintSet.TOP);

            String txt = mText + "宽高wrap content";
            mBinding.btnChangeLayoutParam.setText(txt);
            mConstraintSet1.applyTo(mConstraintLayout);
        } else if (n == 4) {
            // 位置
            // mConstraintSet1.clear(mBinding.btnChangeLayoutParam.getId());
            // mConstraintSet2.clear(mBinding.btnChangeLayoutParam.getId());
            ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(
                    mConstraintLayout.getLayoutParams());
            layoutParams.height = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            layoutParams.width = ConstraintLayout.LayoutParams.MATCH_CONSTRAINT;
            layoutParams.matchConstraintPercentWidth = 0.15f;
            layoutParams.matchConstraintDefaultWidth =
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
            layoutParams.matchConstraintPercentHeight = 0.15f;
            layoutParams.matchConstraintDefaultHeight =
                    ConstraintLayout.LayoutParams.MATCH_CONSTRAINT_PERCENT;
            layoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
            layoutParams.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
            String txt = mText + "位置";
            mBinding.btnChangeLayoutParam.setText(txt);
            mBinding.btnChangeLayoutParam.setLayoutParams(layoutParams);
        }


    }
}