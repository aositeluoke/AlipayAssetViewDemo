package com.aositeluoke.alipayassetviewdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    AlipayAssetsView alipayAssetsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        alipayAssetsView = ((AlipayAssetsView) findViewById(R.id.alipayAssetsView));
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //余额5000 余额宝 17778.32  定期4000f 基金6060.89
                alipayAssetsView.setPri(5000f, 17778.32f, 4000f, 6060.89f);
            }
        });
    }

}
