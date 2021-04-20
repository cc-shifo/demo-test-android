package com.example.democircleprogressbar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class GradientProgressBarActivity extends AppCompatActivity implements View.OnClickListener {

    private GradientProgressBar gpb;
    private Button btn1;
    private Button btn2;
    private boolean flag=false;
    private int progress;
    private boolean increase;
    private boolean decrease;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    increase=true;
                    progress = gpb.getPercent();
                    gpb.setPercent(++progress);
                    if(progress >= 100){
                        gpb.setPercent(100);
                        if(flag){
                            increase=false;
                            handler.removeMessages(0);
                            handler.sendEmptyMessageDelayed(1,100);
                        }
                    }else{
                        handler.sendEmptyMessageDelayed(0,100);
                    }
                    break;
                case 1:
                    decrease=true;
                    gpb.setPercent(progress--);
                    if(progress <= 0){
                        decrease=false;
                        handler.removeMessages(1);
                        gpb.setPercent(0);
                        handler.sendEmptyMessageDelayed(0,100);
                    }else{
                        handler.sendEmptyMessageDelayed(1,100);
                    }
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        initView();
        handler.sendEmptyMessageDelayed(0,100);

    }

    private void initView() {
        gpb = (GradientProgressBar) findViewById(R.id.gpb);
        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);

        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                flag=true;
                if(increase){
                    handler.sendEmptyMessageDelayed(0,100);
                }else{
                    handler.sendEmptyMessageDelayed(1,100);
                }

                break;
            case R.id.btn2:
                handler.removeMessages(0);
                handler.removeMessages(1);

                break;
        }
    }
}