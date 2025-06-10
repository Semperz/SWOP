package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.OrderDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface OrderApi {
    // ADMIN
    @GET("api/orders")
    Call<List<OrderDto>> getAll();

    // usuario
    @GET("api/orders/my-orders")
    Call<List<OrderDto>> myOrders();

    @GET("api/orders/{id}")
    Call<OrderDto> getById(@Path("id") long id);

    @POST("api/orders")
    Call<OrderDto> create(@Body OrderDto body);

    @PUT("api/orders/{id}")
    Call<OrderDto> update(@Path("id") long id, @Body OrderDto body);

    @DELETE("api/orders/{id}")
    Call<Void> delete(@Path("id") long id);

    @GET("api/orders/details/productsname/{orderDetailId}")
    Call<List<String>> getProductNameByOrderDetailId(@Path("orderDetailId") long id);
}

