package com.example.swop.data.session;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.swop.data.local.SecurePrefs;
import com.example.swop.data.remote.api.AuthApi;
import com.example.swop.data.remote.models.AuthRequest;
import com.example.swop.data.remote.models.LoginResponse;
import com.example.swop.data.util.JwtUtils;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SessionManager {

    private static final String BASE_URL = "https://swopbackend.onrender.com/";
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

    // asíncrono: recibe un callback
    public void getValidToken(ReloginCallback callback) {
        String token = prefs.getString(KEY_TOKEN, null);
        if (token != null && !JwtUtils.isExpired(token)) {
            callback.onResult(token);
        } else {
            reloginAsync(callback);
        }
    }
    public String getToken() {
        return prefs.getString(KEY_TOKEN, null);
    }

    public void reloginAsync(ReloginCallback callback) {
        String email = prefs.getString(KEY_EMAIL, null);
        String pass  = prefs.getString(KEY_PASS , null);
        if (email == null || pass == null) {
            callback.onResult(null);
            return;
        }
        authApi.login(new AuthRequest(email, pass)).enqueue(new retrofit2.Callback<LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<LoginResponse> call, retrofit2.Response<LoginResponse> res) {
                if (!res.isSuccessful() || res.body() == null) {
                    callback.onResult(null);
                    return;
                }
                String newToken = res.body().getToken();
                prefs.edit().putString(KEY_TOKEN, newToken).apply();
                callback.onResult(newToken);
            }
            @Override
            public void onFailure(retrofit2.Call<LoginResponse> call, Throwable t) {
                callback.onResult(null);
            }
        });
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

    public String getUserEmail() {
        return prefs.getString(KEY_EMAIL, null);
    }

    public boolean isLoggedIn() {
        String token = prefs.getString(KEY_TOKEN, null);
        return token != null && !JwtUtils.isExpired(token);
    }
}