package com.example.swop;

import android.content.Intent;
import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.LayoutRes;
import androidx.appcompat.app.AppCompatActivity;

import com.example.swop.profile.ProfileActivity;
import com.example.swop.shop.CartActivity;
import com.example.swop.shop.CategoryActivity;
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
            setSupportActionBar(tb);
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


    private void setUpToolbar(MaterialToolbar tb) {
        setSupportActionBar(tb);
        tb.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_home) {
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
            } else if (id == R.id.action_profile) {
                startActivity(new Intent(this, ProfileActivity.class));
                return true;
            } else if (id == R.id.action_cart) {
                startActivity(new Intent(this, CartActivity.class));
                return true;
            } else if (id == R.id.cat_lego) {
                openCategory("lego");
                return true;
            } else if (id == R.id.cat_merch) {
                openCategory("merch");
                return true;
            } else{
                return false;
            }
        });
    }

    private void openCategory(String category) {
        Intent i = new Intent(this, CategoryActivity.class).putExtra("category", category);
        Log.d("BaseActivity", "Opening category: " + category);
        if (!category.equals(getIntent().getStringExtra("category"))) {
            startActivity(i);
        } else {
            Log.d("BaseActivity", "Category already open: " + category);
        }
    }
}
