package com.example.prashantadvait_foundation.network;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface APIService {

        @GET("media-coverages?limit=100")
       Call<List<ResponseModel>> getResponseData(@Query("limit") int limit);
       // Call<List<ResponseModel>> getResponseData();
}
