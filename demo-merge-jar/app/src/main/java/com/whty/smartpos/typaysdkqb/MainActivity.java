package com.whty.smartpos.typaysdkqb;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.whty.smartpos.typaysdk.impl.TYSmartPosApiImpl;
import com.whty.smartpos.typaysdk.inter.ITYPayManagerListener;
import com.whty.smartpos.typaysdk.inter.ITYSmartPosSdkManager;
import com.whty.smartpos.typaysdk.model.RequestParams;
import com.whty.smartpos.typaysdk.model.ResponseParams;
import com.whty.smartpos.typaysdk.model.TransType;

public class MainActivity extends AppCompatActivity {

    private ITYSmartPosSdkManager itySmartPosSdkManager;
    private String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        itySmartPosSdkManager = TYSmartPosApiImpl.getInstance();
        itySmartPosSdkManager.setTYPayManagerListener(new ITYPayManagerListener() {
            @Override
            public void onResult(ResponseParams respParams) {
                Log.e(TAG,"respParams = "+respParams.toString());
            }
        });

        init();
    }

    private void init() {
        Button init = findViewById(R.id.init);
        Button login = findViewById(R.id.sale);
        init.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itySmartPosSdkManager.init(MainActivity.this);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestParams requestParams = new RequestParams();
                requestParams.setTransId(TransType.SALE);
                requestParams.setForceLogin(true);
                itySmartPosSdkManager.doTrans(requestParams);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        itySmartPosSdkManager.release();
    }
}