package com.example.swop.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;


import com.example.swop.data.local.SecurePrefs;
import com.example.swop.data.remote.api.AuthApi;
import com.example.swop.data.remote.models.AuthRequest;
import com.example.swop.data.remote.models.LoginResponse;
import com.example.swop.data.util.JwtUtils;

import java.io.IOException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionManager {

    private static final String BASE_URL = "http://10.0.2.2:8080/";
    private static final String PREFS = "creds_prefs";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASS = "password";
    private static final String KEY_TOKEN = "jwt_access";

    private final SharedPreferences prefs;
    private final AuthApi authApi;

    public SessionManager(Context ctx) {
        this.prefs = SecurePrefs.get(ctx, PREFS);


        Retrofit retrofitLite = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        this.authApi = retrofitLite.create(AuthApi.class);
    }

    public synchronized @Nullable String getValidToken() {
        String token = prefs.getString(KEY_TOKEN, null);
        if (token != null && !JwtUtils.isExpired(token)) return token;
        return relogin();
    }

    private @Nullable String relogin() {
        String email = prefs.getString(KEY_EMAIL, null);
        String pass  = prefs.getString(KEY_PASS , null);
        if (email == null || pass == null) return null;
        try {
            Response<LoginResponse> res = authApi.login(new AuthRequest(email, pass)).execute();
            if (!res.isSuccessful() || res.body() == null) return null;
            String newToken = res.body().getToken();
            prefs.edit().putString(KEY_TOKEN, newToken).apply();
            return newToken;
        } catch (IOException e) {
            return null;
        }
    }

    public void saveCredentials(String email, String pass, String token) {
        prefs.edit()
                .putString(KEY_EMAIL, email)
                .putString(KEY_PASS , pass)
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public void clear() { prefs.edit().clear().apply(); }

    public AuthApi getAuthApi() {
        return authApi;
    }
}

