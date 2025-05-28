package com.example.swop.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.api.ProductApi;
import com.example.swop.data.remote.models.ProductDto;
import com.example.swop.data.util.ApiCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ProductRepository {
    private final ProductApi api;


    public ProductRepository(Context ctx) {
        api = RetrofitClient.getInstance(ctx).create(ProductApi.class);
    }

    public void getAll(ApiCallback<List<ProductDto>> cb) {
        api.getAll().enqueue(wrap(cb, "getAllProducts"));
    }

    public void getById(long id, ApiCallback<ProductDto> cb) {
        api.getById(id).enqueue(wrap(cb, "getProduct"));
    }

    public void getByCategory(long categoryId, ApiCallback<List<ProductDto>> cb) {
        api.getByCategory(categoryId).enqueue(wrap(cb, "getProductsByCategory"));
    }

    public void create(ProductDto dto, ApiCallback<ProductDto> cb) {
        api.create(dto).enqueue(wrap(cb, "createProduct"));
    }

    public void update(long id, ProductDto dto, ApiCallback<ProductDto> cb) {
        api.update(id, dto).enqueue(wrap(cb, "updateProduct"));
    }

    public void delete(long id, ApiCallback<Boolean> cb) {
        api.delete(id).enqueue(boolWrap(cb, "deleteProduct"));
    }

    //Helpers
    private <R> Callback<R> wrap(ApiCallback<R> cb, String tag) { return new Callback<R>() {
            @Override public void onResponse(Call<R> call, Response<R> res) { if (res.isSuccessful()) cb.onSuccess(res.body()); else cb.onFailure(new Exception("HTTP " + res.code()));}
            @Override public void onFailure(Call<R> call, Throwable t) {Log.e("ProductRepo", tag, t); cb.onFailure(t); }}; }
    private Callback<Void> boolWrap(ApiCallback<Boolean> cb, String tag) { return new Callback<Void>() {
        @Override public void onResponse(Call<Void> call, Response<Void> res) { cb.onSuccess(res.isSuccessful()); }
        @Override public void onFailure(Call<Void> call, Throwable t) { Log.e("ProductRepo", tag, t); cb.onFailure(t);} }; }
}
