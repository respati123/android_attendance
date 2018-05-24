package com.android.victory.schedule.service;

import com.android.victory.schedule.service.Requests.Response;


import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface GetDirection {

    @GET("json")
    Call<Response> getDirection(@Query("origin") String origin, @Query("destination") String destination);



}
