package com.example.democustomsizedialog;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.democustomsizedialog.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding mBinding;
    private RightEnterDialog mRightEnterDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        // 自定义主题
        // mRightEnterDialog = new RightEnterDialog(this, R.style.Theme_Right_Dialog);

        /**
         * Theme.AppCompat.Light.Dialog与Theme.AppCompat.Light.Dialog.Alert区别是
         * Alert除了包含所有Theme.AppCompat.Light.Dialog属性外，还定义了最大和最小宽度属性
         * <item name="android:windowMinWidthMajor">@dimen/abc_dialog_min_width_major</item>
         * <item name="android:windowMinWidthMinor">@dimen/abc_dialog_min_width_minor</item>
         * 所以，Alert主题不适合dialog大小可定制的需求。
         *
         * 主题参考：https://developer.android.google.cn/guide/topics/ui/look-and-feel/themes
         */
        // 产生Window对象，并且设置默认WindowManager.LayoutParam来约束Window的默认大小。
        mRightEnterDialog = new RightEnterDialog(this, R.style.Theme_Right_Dialog);
        // mRightEnterDialog = new RightEnterDialog(this, androidx.appcompat.R.style
        //         .Theme_AppCompat_Light_Dialog);// 可以用的默认主题。
        // mRightEnterDialog = new RightEnterDialog(this, androidx.appcompat.R.style
        //         .Theme_AppCompat_Light_Dialog_Alert); // 不对的主题。主题设置了window的最大最小值。
        // mRightEnterDialog.create(); // 改善show时的效果。提前生成view，将view添加进decor(此时的decor是
        // 在setContentView中产生的，当然decor可以提前生成，确定了Window后就可以通过Window生成decor)。
        // 注意，有的在setContentView设置的Window属性会被之后的setContentView调用给覆盖掉。
        mBinding.btnRightDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mRightEnterDialog.isShowing()) {
                    mRightEnterDialog.show();
                } else {
                    mRightEnterDialog.cancel();
                }
            }
        });
    }
}