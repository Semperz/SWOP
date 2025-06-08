package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.ProductDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface ProductApi {
    @GET("api/products")
    Call<List<ProductDto>> getAll();

    @GET("api/products/{id}")
    Call<ProductDto> getById(@Path("id") long id);

    @POST("api/products")
    Call<ProductDto> create(@Body ProductDto body);

    @PUT("api/products/{id}")
    Call<ProductDto> update(@Path("id") Long id, @Body ProductDto body);

    @DELETE("api/products/{id}")
    Call<Void> delete(@Path("id") long id);

    @GET("api/products/category/{categoryId}")
    Call<List<ProductDto>> getByCategory(@Path("categoryId") long categoryId);

    @GET("api/products/auction")
    Call<ProductDto> getAuctedProduct();
}

