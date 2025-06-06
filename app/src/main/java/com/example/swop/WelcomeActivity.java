package com.example.swop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import com.example.swop.bids.BidsActivity;
import com.example.swop.data.session.SessionManager;
import com.example.swop.login.LoginCallback;
import com.example.swop.login.LoginDialogFragment;
import com.example.swop.login.LoginResult;
import com.example.swop.shop.mainwindow.MainWindow;

public class WelcomeActivity extends BaseActivity implements LoginCallback {
    Button btnTienda, btnPujas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        SessionManager sessionManager = new SessionManager(this);
        initiazeUI();
        btnTienda.setOnClickListener(v -> {
            startActivity(new Intent(this, MainWindow.class));
        });

        btnPujas.setOnClickListener(v -> {
            if (sessionManager.getValidToken() != null) {
                // Token válido, ir directo a BidsActivity
                startActivity(new Intent(this, BidsActivity.class));
            } else {
                // No token o expirado -> mostrar diálogo login
                LoginDialogFragment dialog = new LoginDialogFragment();
                dialog.setCallback(this);
                dialog.show(getSupportFragmentManager(), "LoginDialog");
            }
        });
    }
    @Override protected int getLayoutResId() {return R.layout.activity_welcome;}


    private void initiazeUI() {
         btnPujas = findViewById(R.id.btnBids);
         btnTienda = findViewById(R.id.btnStore);
    }

    @Override
    public void onLoginResult(LoginResult result) {
        if (result.success) {
            // Login correcto -> ir a BidsActivity
            startActivity(new Intent(this, BidsActivity.class));
        } else {
            Toast.makeText(this, "No se ha podido loguear", Toast.LENGTH_SHORT).show();
        }
    }

}
