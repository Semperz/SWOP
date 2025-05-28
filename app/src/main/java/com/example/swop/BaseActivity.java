package com.example.swop;

import android.graphics.ImageDecoder;
import android.graphics.drawable.AnimatedImageDrawable;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.Objects;

public abstract class BaseActivity extends AppCompatActivity {
    private AnimatedImageDrawable bg;

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
}
