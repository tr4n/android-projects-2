package com.example.pixabayapi.networks;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface iPixabayService {

    @GET("api/")
    Call<PixabayResponse> get(
            @Query("key") String key,
            @Query("q") String q,
            @Query("image_type") String imageType,
            @Query("page") Integer page,
            @Query("per_page") Integer perPage
    );
}
