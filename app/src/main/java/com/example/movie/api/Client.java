package com.example.movie.api;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class Client {

    public static final String BASE_URL = "https://api.themoviedb.org/3/";
    public static final String BASE_URL_YOUTUBE = "https://www.googleapis.com/youtube/v3/";
    public static Retrofit retrofit = null;
    public static Retrofit retrofitYoutube = null;

    // Creating base url to tmdb
    public static Retrofit getClient()
    {
        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofit;
    }

    public static Retrofit getClientYoutube()
    {
        if(retrofitYoutube == null)
        {
            retrofitYoutube = new Retrofit.Builder().baseUrl(BASE_URL_YOUTUBE).addConverterFactory(GsonConverterFactory.create()).build();
        }
        return retrofitYoutube;
    }
}
