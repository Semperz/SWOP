package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.AuthRequest;
import com.example.swop.data.remote.models.LoginResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthApi {
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body AuthRequest body);
}

