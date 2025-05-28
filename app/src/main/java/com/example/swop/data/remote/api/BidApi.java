package com.example.swop.data.remote.api;

import com.example.swop.data.remote.models.BidDto;
import java.util.List;
import retrofit2.Call;
import retrofit2.http.*;

public interface BidApi {
    @POST("api/bids")
    Call<BidDto> placeBid(@Body BidDto body);

    @GET("api/bids/product/{productId}")
    Call<List<BidDto>> getBidsByProduct(@Path("productId") long productId);

    @GET("api/bids/product/{productId}/highest")
    Call<BidDto> getHighestBid(@Path("productId") long productId);

    @GET("api/bids/me")
    Call<List<BidDto>> getMyBids();

    @PUT("api/bids/{bidId}")
    Call<BidDto> updateBid(@Path("bidId") long bidId, @Body BidDto body);

    @DELETE("api/bids/{bidId}")
    Call<Void> deleteBid(@Path("bidId") long bidId);

    @PATCH("api/bids/{bidId}/status")
    Call<BidDto> updateStatus(@Path("bidId") long bidId, @Query("status") String status);
}

