package com.example.swop.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.api.CustomerApi;
import com.example.swop.data.remote.models.CustomerDto;
import com.example.swop.data.util.ApiCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CustomerRepository {
    private final CustomerApi api;
    public CustomerRepository(Context ctx) { api = RetrofitClient.getInstance(ctx).create(CustomerApi.class); }

    /* ADMIN */
    public void getAll(ApiCallback<List<CustomerDto>> cb) { api.getAll().enqueue(wrap(cb, "getAll")); }
    public void getById(long id, ApiCallback<CustomerDto> cb) { api.getById(id).enqueue(wrap(cb, "getById")); }
    public void update(long id, CustomerDto dto, ApiCallback<CustomerDto> cb) { api.update(id, dto).enqueue(wrap(cb, "update")); }
    public void delete(long id, ApiCallback<Boolean> cb) { api.delete(id).enqueue(boolWrap(cb, "delete")); }
    /* Públicos */
    public void create(CustomerDto dto, ApiCallback<CustomerDto> cb) { api.create(dto).enqueue(wrap(cb, "create")); }
    /* Perfil propio */
    public void me(ApiCallback<CustomerDto> cb) { api.me().enqueue(wrap(cb, "me")); }
    public void updateMe(CustomerDto dto, ApiCallback<CustomerDto> cb) { api.updateMe(dto).enqueue(wrap(cb, "updateMe")); }
    public void deleteMe(ApiCallback<Boolean> cb) { api.deleteMe().enqueue(boolWrap(cb, "deleteMe")); }

    private <R> Callback<R> wrap(ApiCallback<R> cb, String tag) { return new Callback<R>() {
        @Override public void onResponse(Call<R> call, Response<R> res) { if (res.isSuccessful()) cb.onSuccess(res.body()); else cb.onFailure(new Exception("HTTP "+res.code())); }
        @Override public void onFailure(Call<R> call, Throwable t) { Log.e("CustomerRepo", tag, t); cb.onFailure(t);} }; }
    private Callback<Void> boolWrap(ApiCallback<Boolean> cb, String tag) { return new Callback<Void>() {
        @Override public void onResponse(Call<Void> call, Response<Void> res) { cb.onSuccess(res.isSuccessful()); }
        @Override public void onFailure(Call<Void> call, Throwable t) { Log.e("CustomerRepo", tag, t); cb.onFailure(t);} }; }
}
