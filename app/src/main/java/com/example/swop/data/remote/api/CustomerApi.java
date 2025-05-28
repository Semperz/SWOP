package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.CustomerDto;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface CustomerApi {
    // ADMIN endpoints
    @GET("api/customers")
    Call<List<CustomerDto>> getAll();

    @GET("api/customers/{id}")
    Call<CustomerDto> getById(@Path("id") long id);

    @PUT("api/customers/{id}")
    Call<CustomerDto> update(@Path("id") long id, @Body CustomerDto body);

    @DELETE("api/customers/{id}")
    Call<Void> delete(@Path("id") long id);

    // públicos
    @POST("api/customers")
    Call<CustomerDto> create(@Body CustomerDto body);

    // perfil propio
    @GET("api/customers/me")
    Call<CustomerDto> me();

    @PUT("api/customers/me")
    Call<CustomerDto> updateMe(@Body CustomerDto body);

    @DELETE("api/customers/me")
    Call<Void> deleteMe();
}


