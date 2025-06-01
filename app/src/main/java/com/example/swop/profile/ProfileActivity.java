package com.example.swop.profile;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;


import com.example.swop.BaseActivity;
import com.example.swop.R;

public class ProfileActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
    }

    @Override protected int getLayoutResId() {return R.layout.activity_welcome;}

}