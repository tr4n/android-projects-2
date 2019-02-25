package com.example.pixabayapi.networks;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitInstance {
    public static Retrofit instance;

    public static Retrofit getInstance() {
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl("http://pixabay.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }

        return instance;
    }
}
