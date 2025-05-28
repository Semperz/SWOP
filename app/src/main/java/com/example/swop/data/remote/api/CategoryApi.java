package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.CategoryDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface CategoryApi {
    @GET("api/categories")
    Call<List<CategoryDto>> getAll();

    @GET("api/categories/{id}")
    Call<CategoryDto> getById(@Path("id") long id);

    @POST("api/categories")
    Call<CategoryDto> create(@Body CategoryDto body);

    @PUT("api/categories/{id}")
    Call<CategoryDto> update(@Path("id") long id, @Body CategoryDto body);

    @DELETE("api/categories/{id}")
    Call<Void> delete(@Path("id") long id);
}

