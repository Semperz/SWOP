package com.example.swop.data.repository;

import android.content.Context;
import android.util.Log;

import com.example.swop.data.remote.RetrofitClient;
import com.example.swop.data.remote.api.BidApi;
import com.example.swop.data.remote.models.BidDto;
import com.example.swop.data.util.ApiCallback;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BidRepository {
    private final BidApi api;
    public BidRepository(Context ctx) { api = RetrofitClient.getInstance(ctx).create(BidApi.class); }

    public void placeBid(BidDto dto, ApiCallback<BidDto> cb) { api.placeBid(dto).enqueue(wrap(cb, "placeBid")); }
    public void getByProduct(long productId, ApiCallback<List<BidDto>> cb) { api.getBidsByProduct(productId).enqueue(wrap(cb, "getByProduct")); }
    public void getHighest(long productId, ApiCallback<BidDto> cb) { api.getHighestBid(productId).enqueue(wrap(cb, "getHighest")); }
    public void getMyBids(ApiCallback<List<BidDto>> cb) { api.getMyBids().enqueue(wrap(cb, "getMyBids")); }
    public void updateBid(long bidId, BidDto dto, ApiCallback<BidDto> cb) { api.updateBid(bidId, dto).enqueue(wrap(cb, "updateBid")); }
    public void deleteBid(long bidId, ApiCallback<Boolean> cb) { api.deleteBid(bidId).enqueue(boolWrap(cb, "deleteBid")); }
    public void updateStatus(long bidId, String status, ApiCallback<BidDto> cb) { api.updateStatus(bidId, status).enqueue(wrap(cb, "updateStatus")); }

    private <R> Callback<R> wrap(ApiCallback<R> cb, String tag) { return new Callback<R>() {
        @Override public void onResponse(Call<R> call, Response<R> res) {
            if (res.isSuccessful()) {
                if (res.body() != null) {
                    cb.onSuccess(res.body());
                } else {
                    cb.onSuccess(null);  // "no hay puja"
                }
            } else {
                cb.onFailure(new Exception("HTTP " + res.code()));
            } }
        @Override public void onFailure(Call<R> call, Throwable t) { Log.e("BidRepo", tag, t); cb.onFailure(t);} }; }
    private Callback<Void> boolWrap(ApiCallback<Boolean> cb, String tag) { return new Callback<Void>() {
        @Override public void onResponse(Call<Void> call, Response<Void> res) {
            cb.onSuccess(res.isSuccessful());
        }
        @Override public void onFailure(Call<Void> call, Throwable t) { Log.e("BidRepo", tag, t); cb.onFailure(t);} }; }
}
