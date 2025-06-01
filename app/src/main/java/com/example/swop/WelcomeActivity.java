package com.example.swop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.example.swop.shop.mainwindow.MainWindow;

public class WelcomeActivity extends BaseActivity {
    Button btnTienda, btnPujas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initiazeUI();
        btnTienda.setOnClickListener(v -> {
            startActivity(new Intent(this, MainWindow.class));
        });
    }
    @Override protected int getLayoutResId() {return R.layout.activity_welcome;}


    private void initiazeUI() {
         btnPujas = findViewById(R.id.btnBids);
         btnTienda = findViewById(R.id.btnStore);
    }
}
