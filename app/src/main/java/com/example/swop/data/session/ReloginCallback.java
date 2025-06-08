package com.example.swop.data.session;

import androidx.annotation.Nullable;

public interface ReloginCallback {
    void onResult(@Nullable String token);
}
