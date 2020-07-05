package com.example.demo.demobottomsheetdialogfragment;

import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.demo.demobottomsheetdialogfragment.home.HomeMenuDialog;

public class MainActivity extends AppCompatActivity {
//    BaseBottomDialogFragment mBaseBottomDialogFragment;
    BaseBottomDialogFragment2 mBaseBottomDialogFragment;
//    BottomSheetDialogUtils mSheetDialog;
//    BottomSheetDialogUtils2 mSheetDialog;
    BottomSheetDialogUtils3 mSheetDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        mBaseBottomDialogFragment = new ImpBottomDialogFragment();
        Button button = findViewById(R.id.btn_dialog);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSheetDialog != null) {
                    if (mSheetDialog.isShowing()) {
                        mSheetDialog.dismiss();
                    } else {
                        mSheetDialog.show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mSheetDialog = new HomeMenuDialog(this);
//        mSheetDialog = new ImplBottomSheetDialogUtils(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        mBaseBottomDialogFragment.show(getSupportFragmentManager(), "");
        mSheetDialog.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSheetDialog.dismiss();
    }
}