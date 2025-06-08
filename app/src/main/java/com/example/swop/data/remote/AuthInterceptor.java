package com.example.swop.data.remote;

import androidx.annotation.NonNull;

import com.example.swop.data.session.SessionManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {

    private final SessionManager session;

    public AuthInterceptor(SessionManager session) { this.session = session; }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();
        if (original.url().encodedPath().contains("/api/auth/login")) {
            return chain.proceed(original);
        }

        String token = session.getToken();
        Request req = (token == null) ? original : original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();

        Response res = chain.proceed(req);
        if (res.code() == 401) {
            session.clear();
        }
        return res;
    }
}

