package com.example.movie.api;

import com.example.movie.classes.Movie;
import com.example.movie.classes.MoviesInfo;
import com.example.movie.classes.YoutubeInfo;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    // Creating ends of urls for getting different types of information
    @GET ("movie/popular")
    Call<MoviesInfo> getPopularMovies(@Query("api_key") String apiKey,
                                      @Query("page") String page);

    @GET ("movie/top_rated")
    Call<MoviesInfo> getTopRatedMovies(@Query("api_key") String apiKey,
                                       @Query("page") String page);

    @GET ("movie/upcoming")
    Call<MoviesInfo> getUpcomingMovies(@Query("api_key") String apiKey,
                                       @Query("page") String page);

    @GET ("movie/{id}")
    Call<Movie> getDetails(@Path("id") String id,
                           @Query("api_key") String apiKey);



    @GET ("search/movie")
    Call<MoviesInfo> searchMovies(@Query("api_key") String apiKey,
                             @Query("query") String query,
                             @Query("page") String page);

    @GET ("discover/movie")
    Call<MoviesInfo> getDiscover(@Query("api_key") String apiKey,
                                 @Query("with_genres") String genre);

    @GET ("search")
    Call<YoutubeInfo> searchTailer(@Query("part") String part,
                                    @Query("q") String query,
                                   @Query("key") String apiKey);
}
