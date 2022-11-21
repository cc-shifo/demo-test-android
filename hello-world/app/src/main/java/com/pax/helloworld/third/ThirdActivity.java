package com.pax.helloworld.third;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.pax.helloworld.LogUtil;
import com.pax.helloworld.R;

import java.util.ArrayList;
import java.util.List;

public class ThirdActivity extends AppCompatActivity {
    Button mButton1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);

        mButton1 = findViewById(R.id.btn_toJson);
        mButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        testToJson();
                    }
                }).start();
            }
        });
    }

    private void testToJson() {
        SemiDataPkgParser mPkgParser = new SemiDataPkgParser();
        SemiDataPkg mPkg1 = new SemiDataPkg();
        mPkg1.setTask("sale");
        String json = mPkgParser.toJson(mPkg1);
        LogUtil.d("toJson 1: " + json);
        LogUtil.d("fromJson: " + json);


        SemiDataPkg mPkg2 = new SemiDataPkg();
        mPkg2.setTask("void");
        SemiDataPkg.Data data = new SemiDataPkg.Data();
        data.setAmt("1.00");
        mPkg2.setData(data);
        json = mPkgParser.toJson(mPkg2);
        LogUtil.d("toJson 2: " + json);
        LogUtil.d("fromJson: " + json);


        SemiDataPkg mPkg3 = new SemiDataPkg();
        mPkg3.setTask("settlement");
        data = new SemiDataPkg.Data();
        List<String> acq = new ArrayList<>();
        acq.add("A");
        acq.add("B");
        data.setAcq(acq);
        mPkg3.setData(data);
        json = mPkgParser.toJson(mPkg3);
        LogUtil.d("toJson 3: " + json);
        LogUtil.d("fromJson: " + json);

        SemiDataPkg mPkg4 = new SemiDataPkg();
        mPkg4.setTask("adjust");
        data = new SemiDataPkg.Data();
        data.setAmt("1.00");
        data.setTips("");
        data.setAuthNO("null");
        mPkg2.setData(data);
        json = mPkgParser.toJsonWithSerializeNull(mPkg2);
        LogUtil.d("toJson 4: " + json);
        SemiDataPkg dataPkg = mPkgParser.fromJson(json);
        LogUtil.d("fromJson: ");
        LogUtil.d("Task: " + dataPkg.getTask());
        LogUtil.d("Amt: " + dataPkg.getData().getAmt());
        String t = dataPkg.getData().getTips();
        if (t == null)
            LogUtil.d("tip: null");
        else if (t.length() == 0)
            LogUtil.d("tip: length=0");
        else
            LogUtil.d("tip: " + t);

        String au = dataPkg.getData().getAuthNO();
        if (au == null)
            LogUtil.d("AuthNO: null");
        else if (au.length() == 0)
            LogUtil.d("AuthNO: length=0");
        else
            LogUtil.d("AuthNO: " + au);

        SemiDataPkg mPkg5 = new SemiDataPkg();
        mPkg5.setTask("settlement5");
        data = new SemiDataPkg.Data();
        List<String> acq5 = new ArrayList<>();
        acq5.add("A5");
        acq5.add("B5");
        data.setAcq(acq5);
        mPkg5.setData(data);
        json = mPkgParser.toJsonWithNonTypeToken(mPkg5);
        LogUtil.d("toJson 5: " + json);
        mPkg5 = mPkgParser.fromJsonWithNonTypeToken(json);
        LogUtil.d("fromJson: ");
        LogUtil.d("Task: " + mPkg5.getTask());
        data = mPkg5.getData();
        if (data == null)
            LogUtil.d("data: null");
        else {
            acq5 = data.getAcq();
            for (String a : acq5) {
                LogUtil.d("acq: " + a);
            }
        }
    }
}
