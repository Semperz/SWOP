package com.example.swop;

import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swop.data.session.SessionManager;
import com.example.swop.login.LoginCallback;
import com.example.swop.login.LoginDialogFragment;
import com.example.swop.login.LoginResult;
import com.example.swop.profile.ProfileActivity;
import com.example.swop.shop.cartwindow.CartActivity;
import com.example.swop.shop.categorywindow.CategoryActivity;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {
    private AnimatedImageDrawable bg;

    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(getLayoutResId());
        applyAnimatedBackground();

        MaterialToolbar tb = findViewById(R.id.toolbar);
        if (tb != null) {
            setUpToolbar(tb);
        }
    }

    @LayoutRes protected abstract int getLayoutResId();

    protected void applyAnimatedBackground(){
        View root = getWindow().getDecorView();

        ImageDecoder.Source src = ImageDecoder.createSource(getResources(), R.raw.gif_fondo_estrellas);

        try {
            bg = (AnimatedImageDrawable) ImageDecoder.decodeDrawable(src);
            bg.setRepeatCount(AnimatedImageDrawable.REPEAT_INFINITE);
            bg.start();
            root.setBackground(bg);
        } catch (IOException e) {
            Log.e("No se pudo decodificar el fondo: ", Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (bg != null) bg.start();
    }

    @Override
    protected void onStop() {
        if (bg != null) bg.start();
        super.onStop();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        Log.d("MENU", "Menú inflado");
        return true;
    }

    private void setUpToolbar(MaterialToolbar tb) {
        setSupportActionBar(tb);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        tb.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            SessionManager sessionManager = new SessionManager(this);

            if (id == R.id.action_profile || id == R.id.action_cart) {
                sessionManager.getValidToken(token -> {
                    runOnUiThread(() -> {
                        if (token != null) {
                            // Si hay token, abre la actividad correspondiente
                            if (id == R.id.action_profile) {
                                startActivity(new Intent(this, ProfileActivity.class));
                            } else {
                                startActivity(new Intent(this, CartActivity.class));
                            }
                        } else {
                            // No token -> login
                            LoginDialogFragment dialog = new LoginDialogFragment();
                            dialog.setCallback(result -> {
                                if (result.success) {
                                    // Si el login es correcto, abre la actividad
                                    if (id == R.id.action_profile) {
                                        startActivity(new Intent(BaseActivity.this, ProfileActivity.class));
                                    } else {
                                        startActivity(new Intent(BaseActivity.this, CartActivity.class));
                                    }
                                }
                            });
                            dialog.show(getSupportFragmentManager(), "LoginDialog");
                        }
                    });
                });
                return true;
            } else if (id == R.id.action_home) {
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
            } else if (id == R.id.cat_lego) {
                openCategory("lego");
                return true;
            } else if (id == R.id.cat_merch) {
                openCategory("merchandising");
                return true;
            } else {
                return false;
            }
        });
    }



    private void openCategory(String category) {
        Intent i = new Intent(this, CategoryActivity.class)
                .putExtra("category", category)
                .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Log.d("Toolbar", "Category option clicked: " + category);

        startActivity(i);
    }
}
