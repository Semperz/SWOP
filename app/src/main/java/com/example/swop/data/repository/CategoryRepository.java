package com.example.swop.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.api.CategoryApi;
import com.example.swop.data.remote.models.CategoryDto;
import com.example.swop.data.util.ApiCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryRepository {
    private final CategoryApi api;
    public CategoryRepository(Context ctx) {
        api = RetrofitClient.getInstance(ctx).create(CategoryApi.class);
    }
    public void getAll(ApiCallback<List<CategoryDto>> cb) { api.getAll().enqueue(wrap(cb, "getAll")); }
    public void getById(long id, ApiCallback<CategoryDto> cb) { api.getById(id).enqueue(wrap(cb, "getById")); }
    public void create(CategoryDto dto, ApiCallback<CategoryDto> cb) { api.create(dto).enqueue(wrap(cb, "create")); }
    public void update(long id, CategoryDto dto, ApiCallback<CategoryDto> cb) { api.update(id, dto).enqueue(wrap(cb, "update")); }
    public void delete(long id, ApiCallback<Boolean> cb) { api.delete(id).enqueue(boolWrap(cb, "delete")); }
    /* same helpers (could be extracted) */
    private <R> Callback<R> wrap(ApiCallback<R> cb, String tag) { return new Callback<R>() {
        @Override public void onResponse(Call<R> call, Response<R> res) { if (res.isSuccessful()) cb.onSuccess(res.body()); else cb.onFailure(new Exception("HTTP "+res.code())); }
        @Override public void onFailure(Call<R> call, Throwable t) { Log.e("CategoryRepo", tag, t); cb.onFailure(t);} }; }
    private Callback<Void> boolWrap(ApiCallback<Boolean> cb, String tag) { return new Callback<Void>() {
        @Override public void onResponse(Call<Void> call, Response<Void> res) { cb.onSuccess(res.isSuccessful()); }
        @Override public void onFailure(Call<Void> call, Throwable t) { Log.e("CategoryRepo", tag, t); cb.onFailure(t);} }; }
}
