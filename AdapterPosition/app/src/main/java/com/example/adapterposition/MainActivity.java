package com.example.adapterposition;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.adapterposition.databinding.ActivityMainBinding;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final Long[] mArray = new Long[]{22L, 33L, 44L, 55L, 66L, 77L};
    private ActivityMainBinding mBinding;
    private int mN1;
    private int mN2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        RvAdapter adapter = new RvAdapter(new RvAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                mBinding.tvStable.setText(String.valueOf(position));
            }

            @Override
            public void directQueryAdapter(int position) {
                mBinding.tvStableDirectQuery.setText(String.valueOf(position));
            }

            @Override
            public void adapterPosition(int position) {
                String v;

                if (mN1 >= mArray.length) {
                    Log.d(TAG, "adapterPosition: clear mN2");
                    mN1 = 0;
                    v = "" + position;
                } else {
                    v = mBinding.tvStableAdapter.getText().toString() + position;
                }
                mBinding.tvStableAdapter.setText(v);
            }
        }, true);

        mBinding.rvStable.setAdapter(adapter);
        adapter.setList(Arrays.asList(mArray));

        RvAdapter adapterNotStable = new RvAdapter(new RvAdapter.OnItemClick() {
            @Override
            public void onClick(int position) {
                mBinding.tvNotStable.setText(String.valueOf(position));
            }

            @Override
            public void directQueryAdapter(int position) {
                mBinding.tvNOTStableDirectQuery.setText(String.valueOf(position));
            }

            @Override
            public void adapterPosition(int position) {
                String v;
                if (mN2 >= mArray.length) {
                    Log.d(TAG, "adapterPosition: clear mN2");
                    mN2 = 0;
                    v = "" + position;
                } else {
                    v = mBinding.tvNotStableAdapter.getText().toString() + position;
                }
                mBinding.tvNotStableAdapter.setText(v);
                mN2++;
            }
        }, false);

        // mBinding.rvNotStable.setAdapter(adapterNotStable);
        // adapterNotStable.setList(Arrays.asList(mArray));
    }
}