package com.android.victory.schedule.remote;

import com.android.victory.schedule.service.GetDirection;

import org.apache.commons.collections4.Get;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {


    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/directions/")
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    GetDirection service = retrofit.create(GetDirection.class);


}
