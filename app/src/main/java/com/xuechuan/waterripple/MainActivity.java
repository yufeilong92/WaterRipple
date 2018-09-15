package com.xuechuan.waterripple;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WaveView mWaveView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initData();

    }

    private void initData() {
        mWaveView.startAnimationin();
    }

    private void initView() {
        mWaveView = (WaveView) findViewById(R.id.wave_view);
    }
}
