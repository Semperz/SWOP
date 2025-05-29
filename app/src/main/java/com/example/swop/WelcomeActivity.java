package com.example.swop;

import android.os.Bundle;

public class WelcomeActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }
    @Override protected int getLayoutResId() {return R.layout.activity_welcome;}
}